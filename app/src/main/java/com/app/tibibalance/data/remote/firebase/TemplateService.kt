/**
 * @file    TemplateService.kt
 * @ingroup data_remote_service // Grupo específico para servicios remotos
 * @brief   Servicio **read-only** para acceder a los datos crudos de la colección `habitTemplates` en Firebase Firestore.
 *
 * @details Esta clase centraliza la lógica de lectura directa sobre la colección pública
 * `habitTemplates`. Está diseñada para ser inyectada en repositorios
 * (como [com.app.tibibalance.data.repository.HabitTemplateRepository]) que luego se encargarán
 * de mapear estos datos crudos al modelo de dominio ([com.app.tibibalance.domain.model.HabitTemplate]).
 *
 * No realiza escrituras ni borrados; la gestión de las plantillas se asume externa.
 * Las operaciones utilizan `await()` para integrarse con Coroutines y se benefician
 * de la caché offline de Firestore si está habilitada.
 */
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Faltan imports en el código original, pero no son necesarios para ESTA versión del servicio
// import com.app.tibibalance.data.remote.mapper.toHabitTemplate
// import com.app.tibibalance.domain.model.HabitTemplate
// import com.google.firebase.firestore.ktx.snapshots
// import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.map

/**
 * @class   TemplateService
 * @brief   Servicio singleton para obtener datos de plantillas de Firestore.
 * @details Provee métodos suspendibles para leer todos los documentos o uno específico
 * de la colección `habitTemplates`, devolviendo los datos como mapas genéricos.
 *
 * @constructor Inyecta la instancia de [FirebaseFirestore] proporcionada por Hilt.
 * @param db Instancia de [FirebaseFirestore].
 */
@Singleton
class TemplateService @Inject constructor(
    private val db: FirebaseFirestore
) {
    /** Referencia a la colección 'habitTemplates' en Firestore. */
    private val collectionRef = db.collection("habitTemplates")

    /**
     * @brief Obtiene la lista completa de plantillas como pares de (ID, Mapa de datos).
     *
     * @details Realiza una operación `get()` en la colección `habitTemplates`.
     * Para cada documento encontrado, extrae su ID y su mapa de datos (`DocumentSnapshot.data`).
     * Si un documento no tiene datos (`data` es `null`), se usa un mapa vacío en su lugar
     * para evitar errores de nulabilidad.
     *
     * @return Una [List] de [Pair]<[String], [Map]<[String], [Any]>>, donde cada par
     * representa el ID del documento y sus campos como un mapa. Devuelve una lista
     * vacía si la colección no existe o está vacía.
     * @throws com.google.firebase.FirebaseException Si ocurre un error durante la lectura en Firestore.
     */
    suspend fun list(): List<Pair<String, Map<String, Any?>>> = // Any? para ser más precisos con Firestore
        collectionRef.get().await().documents
            .mapNotNull { doc ->
                // Devuelve el par (ID, Mapa de datos o mapa vacío si no hay datos)
                doc.id to (doc.data ?: emptyMap())
            }

    /**
     * @brief Descarga puntual los datos de una plantilla específica por su ID.
     *
     * @details Realiza una operación `get()` sobre un documento específico dentro de la
     * colección `habitTemplates` usando el `templateId` proporcionado.
     *
     * @param templateId El identificador único (docId) de la plantilla a obtener.
     * @return Un [Map]<[String], [Any]?> con los campos y valores del documento Firestore,
     * o un mapa vacío (`emptyMap()`) si el documento no existe o no contiene datos.
     * @note La operación puede devolver datos de la caché local de Firestore si está disponible
     * y no hay conexión de red.
     * @throws com.google.firebase.FirebaseException Si ocurre un error durante la lectura en Firestore.
     */
    suspend fun fetch(templateId: String): Map<String, Any?> = // Any? para ser más precisos
        collectionRef
            .document(templateId) // Referencia al documento específico
            .get() // Obtiene el documento una vez
            .await() // Convierte Task a suspend function
            .data // Obtiene el mapa de datos (puede ser null)
            ?: emptyMap() // Devuelve mapa vacío si data es null
}
