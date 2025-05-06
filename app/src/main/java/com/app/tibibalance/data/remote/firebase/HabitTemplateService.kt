package com.app.tibibalance.data.remote.firebase

import com.app.tibibalance.data.remote.mapper.toHabitTemplate
import com.app.tibibalance.domain.model.HabitTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// data/remote/firebase/HabitTemplateService.kt
class HabitTemplateService @Inject constructor(
    private val fs: FirebaseFirestore
) {

    /** una sola descarga, p. ej. para refresco manual */
    suspend fun fetchOnce(): List<HabitTemplate> =
        fs.collection("habitTemplates").get().await()
            .documents.map { it.toHabitTemplate() }

    /** escucha en tiempo real ⇢ Flow<List<Template>> */
    fun observe(): Flow<List<HabitTemplate>> =
        fs.collection("habitTemplates")
            .snapshots()                // ktx 24.3.0+ da Flow<QuerySnapshot>
            .map { qs -> qs.map { it.toHabitTemplate() } }
}
