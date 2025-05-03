// di/ServiceModule.kt
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

    @Binds
    @Singleton
    abstract fun bindAuthService(
        impl: FirebaseAuthService
    ): AuthService
}
