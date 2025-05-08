/**
 * @file    HabitTemplateRepository.kt
 * @ingroup data_repository
 * @brief   Repositorio singleton para gestionar las plantillas de hábitos ([HabitTemplate]).
 *
 * @details Implementa una estrategia *offline-first* sincronizando la colección
 * pública `habitTemplates` de Firestore con una tabla local en Room
 * ([HabitTemplateEntity]). La UI observa los datos locales a través del [Flow]
 * expuesto por [templates].
 *
 * Ofrece dos mecanismos de actualización desde Firestore:
 * 1.  **`refreshOnce()`**: Una descarga manual y completa de todas las plantillas,
 * útil para la carga inicial o para forzar una actualización.
 * 2.  **`startSync()`**: Inicia un listener en tiempo real que mantiene la caché
 * local (Room) actualizada automáticamente con cualquier cambio detectado en
 * la colección remota de Firestore. Incluye manejo de errores para evitar
 * que fallos en el listener detengan la aplicación.
 *
 * Las operaciones de base de datos y red se ejecutan en el [CoroutineDispatcher]
 * de IO inyectado (`@IoDispatcher`).
 */
package com.app.tibibalance.data.repository

import android.util.Log // Para logging en startSync
import com.app.tibibalance.data.local.dao.HabitTemplateDao
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.firebase.HabitTemplateService
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @class   HabitTemplateRepository
 * @brief   Repositorio offline-first para las plantillas de hábitos, sincronizado con Firestore.
 * @see HabitTemplate Modelo de dominio que representa una plantilla.
 * @see HabitTemplateEntity Entidad Room para la persistencia local.
 * @see HabitTemplateDao DAO para interactuar con la tabla Room.
 * @see HabitTemplateService Servicio para interactuar con Firestore.
 *
 * @constructor Inyecta las dependencias necesarias (DAO, Servicio remoto, Dispatcher IO) mediante Hilt.
 * @param dao DAO de Room ([HabitTemplateDao]) para acceder a la tabla local `habit_templates`.
 * @param svc Servicio ([HabitTemplateService]) para obtener datos de plantillas desde Firestore.
 * @param io Dispatcher de Coroutines ([CoroutineDispatcher]) marcado con [@IoDispatcher] para operaciones de IO.
 */
@Singleton
class HabitTemplateRepository @Inject constructor(
    private val dao: HabitTemplateDao,
    private val svc: HabitTemplateService,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    /**
     * @brief Flujo reactivo que expone la lista de plantillas de hábitos desde la caché local (Room).
     *
     * @details Observa la tabla `habit_templates` a través de `dao.observeAll()`.
     * La consulta del DAO ordena los resultados por `category` y luego por `name`.
     * Cada lista de [HabitTemplateEntity] emitida por el DAO se mapea a una lista
     * del modelo de dominio [HabitTemplate] usando la función de extensión `toDomain()`.
     * La operación de mapeo se realiza en el dispatcher [io].
     *
     * @return Un [Flow] que emite la `List<HabitTemplate>` más reciente almacenada localmente,
     * ordenada por categoría y nombre.
     */
    val templates: Flow<List<HabitTemplate>> =
        dao.observeAll() // Flow<List<HabitTemplateEntity>> desde Room (ordenado por SQL)
            .map { entityList -> // Mapea la lista de entidades a una lista de modelos de dominio
                entityList.map { entity -> entity.toDomain() } // Mapea cada entidad individualmente
            }
            .flowOn(io) // Asegura que la operación de mapeo se ejecute en el dispatcher IO

    /**
     * @brief   Realiza una descarga única de todas las plantillas desde Firestore y actualiza la caché local en Room.
     *
     * @details Llama a `svc.fetchOnce()` para obtener la lista completa de [HabitTemplate]
     * desde Firestore. Luego, transforma esta lista en una lista de [HabitTemplateEntity]
     * usando la función de extensión `toEntity()` y finalmente llama a `dao.upsert()`
     * para insertar o actualizar todas las plantillas en la base de datos local Room.
     * Esta función es útil para la carga inicial de datos al arrancar la app o para
     * implementar una funcionalidad de "refrescar" manualmente.
     * Se recomienda ejecutar esta función dentro de un `CoroutineScope` adecuado y en el dispatcher [io].
     *
     * @throws Exception Si la llamada a `svc.fetchOnce()` o `dao.upsert()` falla (e.g., error de red,
     * error de base de datos), la excepción será propagada al llamador.
     */
    suspend fun refreshOnce() {
        // Nota: Se asume que esta función se llama desde un contexto que ya usa el dispatcher 'io'
        // o se envuelve explícitamente con withContext(io) si es necesario.
        try {
            Log.d("HabitTplRepo", "Iniciando refreshOnce...")
            val remoteTemplates = svc.fetchOnce() // Obtiene datos de Firestore
            val entities = remoteTemplates.map { it.toEntity() } // Mapea a entidades Room
            dao.upsert(entities) // Actualiza la base de datos local
            Log.d("HabitTplRepo", "refreshOnce completado. ${entities.size} plantillas actualizadas en Room.")
        } catch (e: Exception) {
            Log.e("HabitTplRepo", "Error durante refreshOnce", e)
            throw e // Relanza la excepción para que sea manejada por la capa superior
        }
    }

    /**
     * @brief Inicia un listener en tiempo real para mantener la caché local (Room) sincronizada con Firestore.
     *
     * @details Lanza una corrutina en el [CoroutineScope] proporcionado (y en el dispatcher [io])
     * que se suscribe al [Flow] devuelto por `svc.observe()`. Cada vez que Firestore emite
     * una nueva lista de [HabitTemplate] (debido a cambios remotos), esta función:
     * 1. Mapea la lista de dominio a una lista de entidades [HabitTemplateEntity].
     * 2. Llama a `dao.upsert()` para actualizar la base de datos local Room.
     *
     * Utiliza `.catch` para interceptar errores que puedan ocurrir en el flujo del listener
     * (e.g., pérdida de conexión, problemas de permisos). Estos errores se loguean pero
     * no cancelan la corrutina principal, permitiendo que la sincronización se reanude
     * potencialmente si el problema se resuelve.
     *
     * @param scope El [CoroutineScope] que controlará el ciclo de vida de esta corrutina de
     * observación (e.g., `viewModelScope`, `lifecycleScope`). Cuando este scope se cancele,
     * la colección del Flow (`collect()`) se detendrá automáticamente.
     */
    fun startSync(scope: CoroutineScope) {
        scope.launch(io) { // Lanza la corrutina en el dispatcher IO y dentro del scope proporcionado
            Log.d("HabitTplRepo", "Iniciando startSync (listener en tiempo real)...")
            svc.observe() // Obtiene el Flow<List<HabitTemplate>> del servicio Firestore
                .onEach { templateList -> // Se ejecuta cada vez que el Flow emite una nueva lista
                    try {
                        val entities = templateList.map { it.toEntity() } // Mapea a entidades Room
                        dao.upsert(entities) // Actualiza la base de datos local
                        Log.d("HabitTplRepo", "Sincronización: ${entities.size} plantillas actualizadas en Room.")
                    } catch (e: Exception) {
                        Log.e("HabitTplRepo", "Error durante el upsert en onEach de startSync", e)
                        // Considerar si se debe notificar este error de alguna manera
                    }
                }
                .catch { e -> // Captura errores terminales del Flow (del listener de Firestore)
                    Log.e("HabitTplRepo", "Error en el Flow de observe() de HabitTemplateService", e)
                    // El Flow termina aquí si hay un error no recuperable en el listener
                    // Se podría añadir lógica de reintento aquí si fuera necesario
                }
                .collect() // Inicia la colección del Flow; la corrutina se suspenderá aquí hasta que el scope se cancele
            Log.d("HabitTplRepo", "startSync finalizado (scope cancelado).") // Se logueará al cancelar el scope
        }
    }
}
