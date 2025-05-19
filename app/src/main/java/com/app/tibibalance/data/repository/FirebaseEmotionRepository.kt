// src/main/java/com/app/tibibalance/data/repository/FirebaseEmotionRepository.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.data.local.dao.EmotionDao
import com.app.tibibalance.data.mapper.toDomainRecord
import com.app.tibibalance.data.mapper.toEntity
import com.app.tibibalance.data.mapper.toFirestoreMap
import com.app.tibibalance.data.mapper.toEntityFromSnapshot
import com.app.tibibalance.ui.screens.emotional.EmotionRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseEmotionRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dao: EmotionDao,
    @com.app.tibibalance.di.IoDispatcher private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : EmotionalRepository {

    /** Observa los registros locales y los mapea a modelo de UI */
    override fun observeEmotions(): Flow<List<EmotionRecord>> =
        dao.observeAll()
            .map { list -> list.map { it.toDomainRecord() } }

    /** Primero guarda en Firestore, luego actualiza Room */
    override suspend fun saveEmotion(record: EmotionRecord) = withContext(ioDispatcher) {
        val uid = auth.uid ?: error("Usuario no autenticado")
        val key = record.date.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        val docRef = db.collection("profiles")
            .document(uid)
            .collection("emotions")
            .document(key)

        // Guardado remoto
        docRef.set(record.toFirestoreMap(), SetOptions.merge()).await()
        // Guardado local
        dao.upsert(record.toEntity())
    }

    // Para sincronizar cambios remotos → locales
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private var unsubscribe: () -> Unit = {}

    init {
        // Reconectar cuando cambia el estado de autenticación
        auth.addAuthStateListener { fb -> scope.launch { reconnect(fb.uid) } }
        scope.launch { reconnect(auth.uid) }
    }

    private suspend fun reconnect(uid: String?) {
        // Detiene la escucha anterior
        unsubscribe()

        if (uid == null) {
            // Al cerrar sesión, limpiar la tabla local
            dao.deleteAll()
            return
        }

        // ─── Initial load desde Firestore a Room ───
        val initialSnapshot = db.collection("profiles")
            .document(uid)
            .collection("emotions")
            .get()
            .await()
        initialSnapshot.documents.forEach { doc ->
            scope.launch {
                dao.upsert(doc.toEntityFromSnapshot())
            }
        }

        // ─── Escucha en tiempo real la colección de emociones ───
        val registration = db.collection("profiles")
            .document(uid)
            .collection("emotions")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snaps, error ->
                if (error != null) return@addSnapshotListener
                snaps?.documentChanges?.forEach { dc ->
                    scope.launch {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> {
                                // El ID del doc es la fecha en formato ISO_LOCAL_DATE
                                val dateStr = dc.document.id
                                dao.deleteByDate(dateStr)
                            }
                            else -> {
                                // Inserta o actualiza la entidad local con el snapshot
                                val entity = dc.document.toEntityFromSnapshot()
                                dao.upsert(entity)
                            }
                        }
                    }
                }
            }

        unsubscribe = { registration.remove() }
    }
}
