// src/main/java/com/app/tibibalance/data/repository/RoomMetricsRepository.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.data.local.dao.MetricsDao
import com.app.tibibalance.data.local.entity.MetricsEntity
import com.app.tibibalance.domain.model.DailyMetrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @class RoomMetricsRepository
 * @brief Implementación de [MetricsRepository] usando Room.
 */
@Singleton
class RoomMetricsRepository @Inject constructor(
    private val dao: MetricsDao
) : MetricsRepository {

    /**
     * @brief Inserta o reemplaza las métricas diarias en la base de datos.
     */
    override suspend fun saveMetrics(metrics: DailyMetrics) {
        dao.insert(
            MetricsEntity(
                date = metrics.date,
                steps = metrics.steps,
                activeCalories = metrics.activeCalories,
                exerciseMinutes = metrics.exerciseMinutes,
                avgHeartRate = metrics.avgHeartRate
            )
        )
    }

    /**
     * @brief Obtiene la última fila de métricas ordenada por fecha descendente.
     * @return Un objeto [DailyMetrics] o null si no hay registros.
     */
    override suspend fun getLatestMetrics(): DailyMetrics? {
        return dao.getLatestMetrics()
            ?.let { entity ->
                DailyMetrics(
                    date = entity.date,
                    steps = entity.steps,
                    activeCalories = entity.activeCalories,
                    exerciseMinutes = entity.exerciseMinutes,
                    avgHeartRate = entity.avgHeartRate
                )
            }
    }
}
