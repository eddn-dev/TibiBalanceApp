package com.app.tibibalance.data.repository

import com.app.tibibalance.data.local.dao.HabitActivityDao
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.service.FirebaseActivityService
import com.app.tibibalance.domain.model.HabitActivity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class ActivityRepository @Inject constructor(
    private val dao : HabitActivityDao,
    private val api : FirebaseActivityService
) {

    /** Guarda localmente y sincroniza en la nube (cola offline de Firestore). */
    suspend fun log(act: HabitActivity) {
        dao.insert(act.toEntity())
        api.uploadAsync(act)      // fire-and-forget
    }

    /** Flujo reactivo de actividades de un hábito. */
    fun observe(habitId: String) =
        dao.observe(habitId)           // Flow<List<Entity>>
            .map { list ->             // Flow<List<Domain>>
                list.map { it.toDomain() }
            }
}
