/**
 * @file    RepositoryModule.kt
 * @ingroup di
 * @brief   Enlaces de repositorios a sus implementaciones concretas.
 *
 * Este módulo Hilt conecta las interfaces de la capa de datos con sus
 * implementaciones basadas en Firebase, todas registradas como *singleton*
 * para garantizar una única instancia durante el ciclo de vida de la app.
 */
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

/**
 * @brief Módulo Hilt que declara los vínculos Impl → Interfaz.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /** Enlaza [AuthRepository] con [FirebaseAuthRepository]. */
    @Binds
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository

    /** Enlaza [HabitRepository] con [FirebaseHabitRepository] como *singleton*. */
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: FirebaseHabitRepository
    ): HabitRepository
}
