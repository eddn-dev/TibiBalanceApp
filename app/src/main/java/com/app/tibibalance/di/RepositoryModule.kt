// di/RepositoryModule.kt
package com.app.tibibalance.di

import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.FirebaseAuthRepository
import com.app.tibibalance.data.repository.FirebaseHabitRepository
import com.app.tibibalance.data.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository              //  ← IMPORTANTE
    ): AuthRepository

    /** Liga la interfaz HabitRepository con su implementación Singleton */
    @Binds                    // ⚠️ orden correcto: Impl → Interfaz
    @Singleton
    abstract fun bindHabitRepository(
        impl: FirebaseHabitRepository
    ): HabitRepository
}
