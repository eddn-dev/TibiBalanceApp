/**
 * @file    ServiceModule.kt
 * @ingroup di
 * @brief   Enlaces de servicios remotos a sus implementaciones.
 *
 * Registra **FirebaseAuthService** como la implementación única de
 * [AuthService] dentro del grafo Hilt.
 */
package com.app.tibibalance.di

import com.app.tibibalance.data.remote.firebase.AuthService
import com.app.tibibalance.data.remote.firebase.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    /** Enlaza [AuthService] con [FirebaseAuthService] como *singleton*. */
    @Binds
    @Singleton
    abstract fun bindAuthService(
        impl: FirebaseAuthService
    ): AuthService
}
