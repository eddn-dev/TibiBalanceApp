package com.app.wear.data

import com.app.wear.model.Habit
import com.app.wear.model.HabitActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun fetchHabits(): List<Habit> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No hay usuario")
        val snap = firestore
            .collection("users")
            .document(uid)
            .collection("habits")
            .get()
            .await()
        return snap.toObjects(Habit::class.java)
    }

    suspend fun fetchActivities(habitId: String): List<HabitActivity> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("No hay usuario")
        val snap = firestore
            .collection("users")
            .document(uid)
            .collection("habits")
            .document(habitId)
            .collection("activities")
            .get()
            .await()
        return snap.toObjects(HabitActivity::class.java)
    }
}
