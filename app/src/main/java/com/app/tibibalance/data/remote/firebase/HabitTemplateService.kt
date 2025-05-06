/* data/remote/firebase/HabitTemplateService.kt */
package com.app.tibibalance.data.remote.firebase

import com.app.tibibalance.data.remote.mapper.toHabitTemplate
import com.app.tibibalance.domain.model.HabitTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class HabitTemplateService @Inject constructor(
    private val fs: FirebaseFirestore
) {

    /** Descarga puntual (p. ej. pull-to-refresh) */
    suspend fun fetchOnce(): List<HabitTemplate> =
        fs.collection("habitTemplates")
            .get()
            .await()
            .documents
            .mapNotNull { it.toHabitTemplate() }      // ⬅️ filtra null

    /** Escucha en tiempo real → Flow<List<Template>> */
    fun observe(): Flow<List<HabitTemplate>> =
        fs.collection("habitTemplates")
            .snapshots()                              // requiere ktx-firestore
            .map { qs ->
                qs.documents.mapNotNull { it.toHabitTemplate() }
            }
}
