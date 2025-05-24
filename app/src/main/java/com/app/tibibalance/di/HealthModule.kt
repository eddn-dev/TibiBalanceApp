// app/src/main/java/com/app/tibibalance/di/HealthModule.kt
package com.app.tibibalance.di

import com.app.tibibalance.data.health.FakeDailyHistoryDataSource
import com.app.tibibalance.data.health.FakeIntradayHealthStatsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthModule {
    @Provides
    @Singleton
    fun provideDailyHistorySource(): FakeDailyHistoryDataSource =
        FakeDailyHistoryDataSource()

    // --- NUEVO proveedor intradía ---
    @Provides
    @Singleton
    fun provideIntradayHealthStatsSource(): FakeIntradayHealthStatsDataSource =
        FakeIntradayHealthStatsDataSource()
}


