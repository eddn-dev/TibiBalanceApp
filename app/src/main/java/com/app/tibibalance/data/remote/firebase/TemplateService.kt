/* data/remote/firebase/TemplateService.kt */
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @file    TemplateService.kt
 * @ingroup data_remote
 * @brief   Servicio **read-only** para la colección pública `habitTemplates`.
 *
 * Centraliza la lógica de lectura sobre la colección, facilitando su
 * inyección en los repositorios y manteniendo un único punto de
 * mantenimiento.  No realiza escrituras ni borrados; toda modificación
 * se gestiona desde la consola de Firebase o scripts administrativos.
 *
 * El SDK de Firestore está configurado con *persistence = true* (por
 * defecto), por lo que las lecturas funcionan sin conexión cuando los
 * documentos están cacheados localmente.
 */
@Singleton
class TemplateService @Inject constructor(
    private val db: FirebaseFirestore
) {

    /**
     * @brief Obtiene la lista completa de plantillas.
     *
     * @details Cada elemento es un par `docId → dataMap`.
     *          Si un documento carece de datos (`null`), se reemplaza por
     *          `emptyMap()` para evitar *NullPointerException*.
     *
     * @return Lista de pares `(id, Map<String, Any>)` con todas las
     *         plantillas existentes; vacía si la colección está vacía.
     * @throws Exception Propaga la excepción de Firebase si la operación falla.
     */
    suspend fun list(): List<Pair<String, Map<String, Any>>> =
        db.collection("habitTemplates").get().await().documents
            .mapNotNull { doc -> doc.id to (doc.data ?: emptyMap()) }

    /**
     * @brief Descarga puntual de una plantilla por su ID.
     *
     * @param templateId Identificador del documento en la colección.
     * @return Mapa de campos de la plantilla, o `emptyMap()` si el
     *         documento no existe (pudo ser eliminado).
     *
     * @note  La llamada funciona *offline*: si el documento está en la
     *        caché local, Firestore lo devolverá sin requerir red.
     * @throws Exception Propaga la excepción de Firebase si `get()` falla.
     */
    suspend fun fetch(templateId: String): Map<String, Any> =
        db.collection("habitTemplates")
            .document(templateId)
            .get()
            .await()                               // convierte Task a suspensión
            .data ?: emptyMap()
}
