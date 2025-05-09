/**
 * @file    DispatcherModule.kt
 * @ingroup di_module // Grupo específico para módulos Hilt/DI
 * @brief   Módulo Hilt que expone los [CoroutineDispatcher] estándar de Kotlin Coroutines como dependencias inyectables.
 *
 * @details
 * <h4>Motivación</h4>
 * Inyectar los dispatchers (en lugar de usar directamente `Dispatchers.IO`,
 * `Dispatchers.Default`, etc., en los repositorios o ViewModels) es una
 * buena práctica que facilita las pruebas unitarias. Permite sustituir los
 * dispatchers reales por dispatchers de prueba (e.g., `StandardTestDispatcher`,
 * `UnconfinedTestDispatcher` de `kotlinx-coroutines-test`) en los tests,
 * lo que ayuda a controlar la ejecución de las corrutinas, evitar concurrencia
 * no deseada (*flaky tests*) y hacer las pruebas más deterministas y rápidas.
 *
 * <h4>Qualifiers</h4>
 * Se utilizan Qualifiers personalizados para diferenciar los distintos tipos de
 * dispatchers que se pueden inyectar:
 * - [DefaultDispatcher]: Para tareas intensivas en CPU (equivalente a `Dispatchers.Default`).
 * - [IoDispatcher]: Para operaciones de entrada/salida (red, disco, base de datos)
 * (equivalente a `Dispatchers.IO`). Definido en [FirebaseModule].
 * - [MainDispatcher]: Para operaciones que deben ejecutarse en el hilo principal de UI
 * (equivalente a `Dispatchers.Main`).
 *
 * @see CoroutineDispatcher
 * @see Dispatchers
 * @see Qualifier
 * @see DefaultDispatcher
 * @see IoDispatcher
 * @see MainDispatcher
 */
package com.app.tibibalance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier // Asegúrate de usar javax.inject para Hilt

/* ────────────────────────────────────────────────────────────── */
/* Qualifiers                               */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief Qualifier para identificar el [CoroutineDispatcher] optimizado para tareas **CPU-bound**.
 * @details Utilízalo para inyectar un dispatcher adecuado para cálculos intensivos,
 * procesamiento de listas grandes, parseo JSON/XML, etc., que no involucren
 * bloqueo por I/O. Corresponde a [Dispatchers.Default].
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * @brief Qualifier para identificar el [CoroutineDispatcher] optimizado para operaciones de **I/O-bound**.
 * @details Utilízalo para inyectar un dispatcher adecuado para operaciones de red,
 * lectura/escritura de archivos, acceso a bases de datos (Room), etc.
 * Corresponde a [Dispatchers.IO].
 * @note Este Qualifier se define también en [FirebaseModule] y se reutiliza aquí para claridad.
 */
// @Qualifier // Ya definido en FirebaseModule, no es necesario redeclararlo si está en el mismo classpath de compilación
// @Retention(AnnotationRetention.BINARY)
// annotation class IoDispatcher // Comentado si ya existe en FirebaseModule

/**
 * @brief Qualifier para identificar el [CoroutineDispatcher] asociado al hilo principal de la UI.
 * @details Utilízalo para inyectar un dispatcher que permita interactuar de forma segura
 * con la interfaz de usuario desde una corrutina (e.g., actualizar un `StateFlow`
 * observado por la UI). Corresponde a [Dispatchers.Main].
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/* ────────────────────────────────────────────────────────────── */
/* Módulo Hilt                              */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief Módulo Hilt que provee las instancias estándar de [CoroutineDispatcher].
 * @details Este módulo, instalado en [SingletonComponent], define métodos `@Provides`
 * para que Hilt pueda inyectar los dispatchers [Dispatchers.Default], [Dispatchers.IO],
 * y [Dispatchers.Main] donde se requieran, utilizando los [Qualifier] correspondientes
 * ([DefaultDispatcher], [IoDispatcher], [MainDispatcher]) para desambiguar.
 */
@Module
@InstallIn(SingletonComponent::class) // Las dependencias serán singletons a nivel de aplicación
object DispatcherModule {

    /**
     * @brief Provee el [CoroutineDispatcher] por defecto, optimizado para tareas CPU-bound.
     * @return La instancia de [Dispatchers.Default].
     */
    @Provides
    @DefaultDispatcher // Asocia este provider con el Qualifier @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * @brief Provee el [CoroutineDispatcher] optimizado para operaciones de I/O.
     * @return La instancia de [Dispatchers.IO].
     */
    @Provides
    @IoDispatcher // Asocia este provider con el Qualifier @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * @brief Provee el [CoroutineDispatcher] que opera en el hilo principal de la UI.
     * @return La instancia de [Dispatchers.Main].
     */
    @Provides
    @MainDispatcher // Asocia este provider con el Qualifier @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
