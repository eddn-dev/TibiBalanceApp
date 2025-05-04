package com.app.tibibalance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/* ────────────────────────────────
   Qualifiers (marcan cada tipo de dispatcher)
   ──────────────────────────────── */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/* ────────────────────────────────
   Module
   ──────────────────────────────── */

/**
 * Exponen los *dispatchers* estándar de Kotlin Coroutines.
 *
 * Al inyectarlos (en vez de llamar a `Dispatchers.IO` directamente) podemos
 * sustituirlos por `UnconfinedTestDispatcher` en pruebas unitarias, logrando
 * tests deterministas y sin *flaky threads*.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    /** CPU-bound tasks, e.g. JSON parsing, crypto */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /** IO-bound tasks, e.g. red/green network, Room, file system */
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /** Interacciones con la UI (Main thread) */
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
