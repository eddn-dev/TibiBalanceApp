// src/main/java/com/app/tibibalance/data/repository/MetricsRepository.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.domain.model.DailyMetrics

/**
 * @interface MetricsRepository
 * @brief Interfaz que define operaciones de persistencia para métricas diarias.
 */
interface MetricsRepository {

    /**
     * @brief Guarda un objeto [DailyMetrics] en la fuente de datos.
     */
    suspend fun saveMetrics(metrics: DailyMetrics)

    /**
     * @brief Recupera las métricas diarias más recientes.
     * @return Un [DailyMetrics] o null si no hay datos.
     */
    suspend fun getLatestMetrics(): DailyMetrics?
}
