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
 * Sincroniza la colección `habitTemplates` entre Firestore (remoto) y Room (local).
 *
 *  - La propiedad [templates] expone un *Flow* que la UI puede observar de forma reactiva.
 *  - [refreshOnce] fuerza una descarga única (útil en App Startup o pull-to-refresh).
 *  - [startSync] escucha los cambios en tiempo real y hace *upsert* en Room.
 */
@Singleton
class HabitTemplateRepository @Inject constructor(
    private val dao: HabitTemplateDao,
    private val svc: HabitTemplateService,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    /** Flujo continuo que la UI puede recolectar con collectAsState(). */
    val templates: Flow<List<HabitTemplate>> =
        dao.observeAll()                      // Flow<List<Entity>>
            .map { list ->                    // map cada entidad → dominio
                list.map { it.toDomain() }
            }
            .flowOn(io)

    /** Descarga una vez todos los documentos y los guarda en Room. */
    suspend fun refreshOnce() {
        val remote = svc.fetchOnce()          // List<HabitTemplate>
        dao.upsert(remote.map { it.toEntity() })
    }

    /**
     * Comienza a escuchar los cambios remotos y los fusiona en Room.
     * Debe llamarse desde un [CoroutineScope] ligado al ciclo de vida,
     * por ejemplo: `templateRepo.startSync(lifecycleScope)`.
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
