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

/**
 * @file    HabitTemplateService.kt
 * @ingroup data_remote
 * @brief   Servicio de Firebase Firestore para **habitTemplates**.
 *
 * Provee dos operaciones:
 * 1. **`fetchOnce()`**  – Descarga puntual (útil para *pull-to-refresh* o
 *    precarga inicial).
 * 2. **`observe()`**    – Flujo reactivo que escucha cambios en tiempo real
 *    mediante `snapshots()` del SDK *ktx*.
 *
 * Cada documento se transforma a modelo de dominio [HabitTemplate] usando
 * `DocumentSnapshot.toHabitTemplate()`, filtrando los nulos para evitar
 * errores de parseo.
 */
class HabitTemplateService @Inject constructor(
    private val fs: FirebaseFirestore
) {

    /**
     * @brief Descarga puntual de todas las plantillas.
     *
     * @details Ejecuta una lectura *get()* sobre la colección
     *          **`habitTemplates`** y la transforma en una lista de
     *          [HabitTemplate].
     *
     * @return Lista de plantillas, vacía si la colección está vacía.
     * @throws Exception Propaga cualquier error de la operación Firestore.
     */
    suspend fun fetchOnce(): List<HabitTemplate> =
        fs.collection("habitTemplates")
            .get()
            .await()
            .documents
            .mapNotNull { it.toHabitTemplate() }      // ⬅️ filtra null

    /**
     * @brief Observa la colección en tiempo real.
     *
     * @details Utiliza `snapshots()` (Firestore KTX) para emitir un
     *          [Flow] que reacciona a cada cambio en **`habitTemplates`**.
     *
     * @return Flujo reactivo con la lista actualizada de [HabitTemplate].
     */
    fun observe(): Flow<List<HabitTemplate>> =
        fs.collection("habitTemplates")
            .snapshots()                              // requiere ktx-firestore
            .map { qs ->
                qs.documents.mapNotNull { it.toHabitTemplate() }
            }
}
