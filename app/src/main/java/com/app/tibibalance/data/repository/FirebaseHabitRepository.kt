/**
 * @file    FirebaseHabitRepository.kt
 * @ingroup data_repository
 * @brief   Repositorio de hábitos basado en **Room + Firestore**.
 *
 * - **Fuente única de verdad local**: la tabla `habits` (Room) es la que
 *   observa la UI.
 * - **Sincronización bidireccional**:
 *   1. Cambios locales (`addHabit`, `updateHabit`, `deleteHabit`) escriben
 *      en Firestore y luego cachean en Room.
 *   2. Un *snapshot listener* escucha la colección remota
 *      `profiles/{uid}/habits` y refleja cualquier cambio entrante en Room.
 *
 * La política de resolución de conflictos es **Last-Write-Wins (LWW)**:
 * Firestore sobrescribe y el listener actualiza la caché; los IDs son
 * iguales en ambas fuentes para simplificar los `upsert`.
 */
package com.app.tibibalance.data.repository

import android.util.Log
import com.app.tibibalance.data.local.dao.HabitDao
import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.mapper.toFirestoreMap
import com.app.tibibalance.data.remote.mapper.toHabit
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.Habit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @class   FirebaseHabitRepository
 * @brief   Implementación de [HabitRepository] sincronizada con Firestore.
 *
 * @constructor Inyecta las dependencias mediante Hilt.
 * @param db    Instancia de [FirebaseFirestore].
 * @param dao   DAO local para la tabla `habits`.
 * @param auth  SDK de autenticación; se usa para obtener el UID.
 * @param io    *Dispatcher* de IO para tareas fuera del hilo principal.
 */
@Singleton
class FirebaseHabitRepository @Inject constructor(
    private val db   : FirebaseFirestore,
    private val dao  : HabitDao,
    private val auth : FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : HabitRepository {

    /* ─────────────── Flow público ─────────────── */

    /**
     * @copydoc HabitRepository.observeHabits
     *
     * Observa la tabla de Room, la convierte al modelo de dominio y emite
     * sólo cuando hay cambios reales (`distinctUntilChanged()`).
     */
    override fun observeHabits(): Flow<List<Habit>> =
        dao.observeAll()
            .map { it.map(HabitEntity::toDomain) }
            .distinctUntilChanged()

    /* ─────────────── CRUD remoto / local ─────────────── */

    /**
     * @copydoc HabitRepository.addHabit
     *
     * - Genera un docId en Firestore.
     * - Sube el mapa resultante (`merge`) y cachea el resultado en Room.
     *
     * @return El identificador asignado.
     */
    override suspend fun addHabit(habit: Habit): String = withContext(io) {
        val uid = auth.uid ?: error("no session")
        val doc = db.userHabits(uid).document()
        val final = habit.copy(id = doc.id)
        doc.set(final.toFirestoreMap(), SetOptions.merge()).await()
        dao.upsert(final.toEntity())
        doc.id
    }

    /**
     * @copydoc HabitRepository.updateHabit
     */
    /*override suspend fun updateHabit(habit: Habit): Unit = withContext(io) {
        val uid = auth.uid ?: return@withContext
        val map = habit.toFirestoreMap()
        Log.d("FIREBASE_MAP", map.toString()) // 👈 Aquí imprimimos el mapa completo
        db.userHabits(uid).document(habit.id)
            .set(habit.toFirestoreMap(), SetOptions.merge()).await()
        dao.upsert(habit.toEntity())
    }*/

    override suspend fun updateHabit(habit: Habit): Unit = withContext(io) {
        val uid = auth.uid ?: return@withContext

        // Obtener el mapa serializado base
        val firestoreMap = habit.toFirestoreMap().toMutableMap()

        // Asegurar que notifConfig.enabled esté incluido
        val notifMap = (firestoreMap["notifConfig"] as? MutableMap<String, Any?>)?.toMutableMap()
            ?: mutableMapOf()
        notifMap["enabled"] = habit.notifConfig.enabled
        firestoreMap["notifConfig"] = notifMap

        Log.d("FIREBASE_MAP_FIXED", firestoreMap.toString()) // Confirmar que enabled está presente

        db.userHabits(uid).document(habit.id)
            .set(firestoreMap, SetOptions.merge()).await()

        dao.upsert(habit.toEntity())
    }


    /**
     * @copydoc HabitRepository.deleteHabit
     */
    override suspend fun deleteHabit(id: String): Unit = withContext(io) {
        val uid = auth.uid ?: return@withContext
        db.userHabits(uid).document(id).delete().await()
        dao.delete(id)
    }

    /* ───────── Snapshot listener Firestore → Room ───────── */

    /** Función nula que se cambia por la devolución de `addSnapshotListener`. */
    private var stop: () -> Unit = {}

    /** Ámbito dedicado a sincronización, ligado a un `SupervisorJob`. */
    private val scope = CoroutineScope(SupervisorJob() + io)

    /** Se engancha al cambio de sesión para reconectar el listener. */
    init {
        auth.addAuthStateListener { fb -> scope.launch { reconnect(fb.uid) } }
        scope.launch { reconnect(auth.uid) }
    }

    /** (Re)conecta el listener a la colección remota del usuario. */
    private suspend fun reconnect(uid: String?) {
        stop(); stop = {}
        if (uid == null) { dao.clear(); return }

        stop = db.userHabits(uid).addSnapshotListener { snaps, err ->
            if (err != null) { err.printStackTrace(); return@addSnapshotListener }
            snaps?.documentChanges?.forEach { dc ->
                val habit = dc.document.toHabit()
                scope.launch {
                    when (dc.type) {
                        DocumentChange.Type.REMOVED -> dao.delete(habit.id)
                        else                        -> dao.upsert(habit.toEntity())
                    }
                }
            }
        }::remove
    }

    /* ─────────── Extensión conveniente ─────────── */

    /** @return Referencia a `profiles/{uid}/habits`. */
    private fun FirebaseFirestore.userHabits(uid: String) =
        collection("profiles").document(uid).collection("habits")

    /* ── Operación parcial (solo checkedToday) ── */

    /**
     * @copydoc HabitRepository.setCheckedToday
     *
     * Actualiza únicamente el campo `doneToday` en Firestore; el listener
     * remolcará el cambio de vuelta a Room.
     */
    override suspend fun setCheckedToday(id: String, checked: Boolean): Unit =
        withContext(io) {
            val uid = auth.uid ?: return@withContext
            db.userHabits(uid).document(id)
                .update("doneToday", checked)    // usa el nombre de campo que tengas
                .await()
        }



}
