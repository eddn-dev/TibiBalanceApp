/**
 * @file    FirebaseHabitRepository.kt
 * @ingroup data_repository
 * @brief   Repositorio Room + Firestore (estrategia LWW).
 */
package com.app.tibibalance.data.repository

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
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHabitRepository @Inject constructor(
    private val db  : FirebaseFirestore,
    private val dao : HabitDao,
    private val auth: FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : HabitRepository {

    /* ─────────── Observe lista completa ─────────── */

    override fun observeHabits(): Flow<List<Habit>> =
        dao.observeAll()
            .map { list -> list.map(HabitEntity::toDomain) }
            .distinctUntilChanged()

    /* ─────────── NUEVO · Observe un hábito ───────── */

    override fun observeHabit(id: String): Flow<Habit?> =
        dao.observeById(id)                 // Flow<HabitEntity?>
            .map { it?.toDomain() }         // Habit? → modelo de dominio

    /* ─────────── CRUD (sin cambios) ─────────── */

    override suspend fun addHabit(habit: Habit): String = withContext(io) {
        val uid = auth.uid ?: error("no session")
        val doc = db.userHabits(uid).document()
        val final = habit.copy(id = doc.id)
        doc.set(final.toFirestoreMap(), SetOptions.merge()).await()
        dao.upsert(final.toEntity())
        doc.id
    }

    override suspend fun updateHabit(habit: Habit) = withContext(io) {
        val uid = auth.uid ?: return@withContext
        db.userHabits(uid).document(habit.id)
            .set(habit.toFirestoreMap(), SetOptions.merge()).await()
        dao.upsert(habit.toEntity())
    }

    override suspend fun deleteHabit(id: String) = withContext(io) {
        val uid = auth.uid ?: return@withContext
        db.userHabits(uid).document(id).delete().await()
        dao.delete(id)
    }

    override suspend fun setCheckedToday(id: String, checked: Boolean) = withContext(io) {
        val uid = auth.uid ?: return@withContext
        db.userHabits(uid).document(id)
            .update("doneToday", checked).await()
    }

    /* ─────────── Listener Firestore → Room (igual) ─────────── */

    private var stop: () -> Unit = {}
    private val scope = CoroutineScope(SupervisorJob() + io)

    init {
        auth.addAuthStateListener { fb -> scope.launch { reconnect(fb.uid) } }
        scope.launch { reconnect(auth.uid) }
    }

    private suspend fun reconnect(uid: String?) {
        stop(); stop = {}
        if (uid == null) { dao.clear(); return }
        scope.launch {
            db.userHabits(uid).get().await().documents.forEach { snap ->
                dao.upsert(snap.toHabit().toEntity())
            }
        }
        stop = db.userHabits(uid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snaps, err ->
                if (err != null) { err.printStackTrace(); return@addSnapshotListener }
                snaps?.documentChanges?.forEach { dc ->
                    scope.launch {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> dao.delete(dc.document.id)
                            else                        -> dao.upsert(dc.document.toHabit().toEntity())
                        }
                    }
                }
            }::remove
    }

    /* ─────────── Helper path Firestore ─────────── */

    private fun FirebaseFirestore.userHabits(uid: String) =
        collection("profiles").document(uid).collection("habits")
}
