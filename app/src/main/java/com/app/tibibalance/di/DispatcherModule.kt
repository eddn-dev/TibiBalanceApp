/**
 * @file    DispatcherModule.kt
 * @ingroup di
 * @brief   Módulo Hilt que expone los *CoroutineDispatcher* estándar.
 *
 * <h4>Motivación</h4>
 * Inyectar los <i>dispatchers</i> (en lugar de usar directamente
 * <code>Dispatchers.IO</code>, <code>Dispatchers.Default</code>, etc.)
 * permite sustituirlos por variantes de prueba —p.ej.
 * <code>UnconfinedTestDispatcher</code>— logrando tests deterministas y
 * evitando <i>flaky threads</i>.
 *
 * <h4>Qualifiers</h4>
 * Cada anotación marca el tipo de carga que manejará el dispatcher:
 * - {@link DefaultDispatcher} → tareas <b>CPU-bound</b>.
 * - {@link IoDispatcher}      → operaciones <b>I/O-bound</b>.
 * - {@link MainDispatcher}    → trabajo en el hilo principal (UI).
 */
package com.app.tibibalance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/* ────────────────────────────────────────────────────────────── */
/*                       Qualifiers                               */
/* ────────────────────────────────────────────────────────────── */


/* ─────────────── Qualifier: Default ─────────────── */

/** Marca el *dispatcher* para tareas **CPU-bound** (p.ej., parseo JSON). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/* ─────────────── Qualifier: Main ─────────────── */

/** Marca el *dispatcher* asociado al hilo principal (UI). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/* ────────────────────────────────────────────────────────────── */
/*                       Módulo Hilt                              */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief   Proveedor singleton de *CoroutineDispatcher*.
 *
 * Cada método expone un dispatcher con su correspondiente
 * {@link Qualifier} para que pueda inyectarse mediante Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    /** @brief Dispatcher **CPU-bound** (`Dispatchers.Default`). */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /** @brief Dispatcher **I/O-bound** (`Dispatchers.IO`). */
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /** @brief Dispatcher asociado al hilo de la UI (`Dispatchers.Main`). */
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
