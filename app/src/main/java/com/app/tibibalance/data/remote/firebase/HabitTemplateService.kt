/**
 * @file    HabitTemplateService.kt
 * @ingroup data_remote_service // Grupo específico para servicios remotos
 * @brief   Servicio para interactuar con la colección `habitTemplates` en Firebase Firestore.
 *
 * @details Esta clase proporciona métodos para leer las plantillas de hábitos predefinidas
 * almacenadas en Firestore. Ofrece dos modos de acceso:
 * 1.  **`fetchOnce()`**: Realiza una única lectura de todos los documentos de la colección.
 * Es útil para la carga inicial de datos o para refrescos manuales.
 * 2.  **`observe()`**: Establece un listener en tiempo real que emite la lista completa
 * de plantillas cada vez que se produce un cambio en la colección remota,
 * utilizando la extensión `snapshots()` de Firestore KTX.
 *
 * Ambas operaciones utilizan la función de extensión [toHabitTemplate] para mapear
 * los `DocumentSnapshot` de Firestore al modelo de dominio [HabitTemplate],
 * descartando (`mapNotNull`) cualquier documento que no pueda ser parseado correctamente
 * para evitar errores y asegurar la integridad de los datos en la aplicación.
 * Este servicio es de solo lectura, asumiendo que las plantillas se gestionan
 * externamente (e.g., consola Firebase, scripts).
 */
package com.app.tibibalance.data.remote.firebase

import com.app.tibibalance.data.remote.mapper.toHabitTemplate // Mapper para convertir DocumentSnapshot a HabitTemplate
import com.app.tibibalance.domain.model.HabitTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots // Extensión KTX para Flows en tiempo real
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await // Para convertir Task a suspend function

/**
 * @class   HabitTemplateService
 * @brief   Servicio de acceso a datos para las plantillas de hábitos en Firestore.
 * @see com.app.tibibalance.data.repository.HabitTemplateRepository Repositorio que consume este servicio.
 *
 * @constructor Inyecta la instancia de [FirebaseFirestore] proporcionada por Hilt.
 * @param fs Instancia de [FirebaseFirestore] para interactuar con la base de datos.
 */
class HabitTemplateService @Inject constructor(
    private val fs: FirebaseFirestore
) {

    /** Referencia a la colección 'habitTemplates' en Firestore. */
    private val collectionRef = fs.collection("habitTemplates")

    /**
     * @brief Realiza una única descarga de todas las plantillas de hábitos desde Firestore.
     *
     * @details Ejecuta una operación `get()` sobre la colección `habitTemplates`.
     * Cada documento recuperado se mapea a un [HabitTemplate] usando [toHabitTemplate].
     * Los documentos que fallen el mapeo (devuelvan `null`) son filtrados.
     *
     * @return Una [List] de [HabitTemplate] que representa el estado actual de la colección remota.
     * Puede devolver una lista vacía si la colección no existe o está vacía.
     * @throws com.google.firebase.FirebaseException Si ocurre un error durante la comunicación con Firestore
     * (e.g., problemas de red, permisos insuficientes).
     */
    suspend fun fetchOnce(): List<HabitTemplate> =
        collectionRef
            .get() // Obtiene los documentos una vez
            .await() // Convierte la Task de Firebase a una función suspendible
            .documents // Accede a la lista de DocumentSnapshot
            .mapNotNull { it.toHabitTemplate() } // Mapea cada documento a HabitTemplate, descartando nulos

    /**
     * @brief Observa la colección `habitTemplates` en tiempo real y emite la lista actualizada de plantillas.
     *
     * @details Utiliza la función de extensión `snapshots()` de `firebase-firestore-ktx` para
     * crear un [Flow] que emite un `QuerySnapshot` cada vez que hay cambios en la colección.
     * Cada emisión del `QuerySnapshot` se transforma en una `List<HabitTemplate>`
     * mapeando los documentos con [toHabitTemplate] y filtrando los nulos.
     *
     * @return Un [Flow] que emite la lista ([List]) actualizada de [HabitTemplate] cada vez que
     * la colección remota cambia. El flujo puede emitir una lista vacía.
     * @note Este flujo continuará emitiendo hasta que el `CoroutineScope` que lo colecciona sea cancelado.
     * Los errores durante la escucha son manejados por el colector del flujo (normalmente en el repositorio).
     */
    fun observe(): Flow<List<HabitTemplate>> =
        collectionRef
            .snapshots() // Escucha cambios en tiempo real (requiere firebase-firestore-ktx)
            .map { querySnapshot -> // Transforma cada QuerySnapshot emitido
                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toHabitTemplate() // Mapea y filtra documentos inválidos
                }
            }
}
