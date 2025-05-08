// data/repository/HabitTemplateRepository.kt
package com.app.tibibalance.data.repository

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
 * @file    HabitTemplateRepository.kt
 * @ingroup data_repository
 * @brief   Sincroniza la colección **`habitTemplates`** entre Firestore y Room.
 *
 * - Expone un flujo continuo de plantillas para la UI.
 * - Permite refrescar la colección manualmente (`refreshOnce`).
 * - Gestiona un *listener* en tiempo real (`startSync`) que mantiene la
 *   caché local actualizada con cualquier cambio remoto.
 */

/**
 * @class   HabitTemplateRepository
 * @brief   Repositorio *offline-first* de plantillas de hábito.
 *
 * @constructor Inyecta DAO local, servicio remoto y *dispatcher* de IO.
 *
 * @param dao DAO de Room para la tabla `habit_templates`.
 * @param svc Servicio Firestore que expone `fetchOnce` y `observe`.
 * @param io  *CoroutineDispatcher* para operaciones de E/S.
 */
@Singleton
class HabitTemplateRepository @Inject constructor(
    private val dao: HabitTemplateDao,
    private val svc: HabitTemplateService,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    /**
     * @brief Flujo reactivo de plantillas cacheadas.
     *
     * @details La lista se ordena por `category` y `name` en la consulta SQL.
     *          El `map` convierte cada entidad a su modelo de dominio.
     */
    val templates: Flow<List<HabitTemplate>> =
        dao.observeAll()                      // Flow<List<Entity>>
            .map { list ->                    // map cada entidad → dominio
                list.map { it.toDomain() }
            }
            .flowOn(io)

    /**
     * @brief   Descarga puntual de todas las plantillas desde Firestore.
     *
     * @details Ejecuta `fetchOnce()` y realiza *upsert* en Room.
     *          Útil en el arranque de la app o en un *pull-to-refresh*.
     */
    suspend fun refreshOnce() {
        val remote = svc.fetchOnce()          // List<HabitTemplate>
        dao.upsert(remote.map { it.toEntity() })
    }

    /**
     * @brief Inicia la sincronización en tiempo real.
     *
     * @param scope `CoroutineScope` vinculado al ciclo de vida (p.ej. `lifecycleScope`).
     *
     * @details Escucha `svc.observe()` y actualiza Room en cada cambio.
     *          Los errores se capturan y se registran sin propagar
     *          excepción para evitar caídas de la app.
     */
    fun startSync(scope: CoroutineScope) {
        scope.launch(io) {
            svc.observe()                     // Flow<List<HabitTemplate>>
                .onEach { list ->
                    dao.upsert(list.map { it.toEntity() })
                }
                .catch { e ->                 // log o métricas; nunca crashea
                    println("Template sync error: ${e.message}")
                }
                .collect()
        }
    }
}
