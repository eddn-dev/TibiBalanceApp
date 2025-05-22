// src/main/java/com/app/di/MetricsModule.kt
package com.app.tibibalance.di

import com.app.tibibalance.data.repository.MetricsRepository
import com.app.tibibalance.data.repository.RoomMetricsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @module MetricsModule
 * @brief Módulo de Hilt para proveer implementaciones de MetricsRepository.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MetricsModule {

    /**
     * @brief Vincula la interfaz MetricsRepository con su implementación RoomMetricsRepository.
     * @param impl Instancia de RoomMetricsRepository inyectada por Hilt.
     * @return Una instancia de MetricsRepository.
     */
    @Binds
    @Singleton
    abstract fun bindMetricsRepository(
        impl: RoomMetricsRepository
    ): MetricsRepository
}
