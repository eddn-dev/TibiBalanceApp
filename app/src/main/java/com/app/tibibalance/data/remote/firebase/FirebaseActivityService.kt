package com.app.tibibalance.data.remote.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.app.tibibalance.domain.model.HabitActivity
import kotlinx.datetime.Instant
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/* FirebaseActivityService.kt (arreglo) */
class FirebaseActivityService @Inject constructor(
    private val auth: FirebaseAuth,
    private val db  : FirebaseFirestore
) {

    fun uploadAsync(act: HabitActivity) {
        val uid = auth.uid ?: return                // sin sesión = no sync
        db.collection("profiles")
            .document(uid)
            .collection("habits")
            .document(act.habitId)                    // ⚠️ 4 segmentos
            .collection("activities")
            .document()                               // auto-id
            .set(act)                                 // o toFirestoreMap()
            .addOnSuccessListener {
                Log.d("FbActSvc", "▶️ actividad subida (${act.type})")
            }
            .addOnFailureListener { e ->
                Log.e("FbActSvc", "💥 fallo subiendo actividad", e)
            }
    }
}
