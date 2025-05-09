/**
 * @file    DatabaseModule.kt
 * @ingroup di_module // Grupo específico para módulos Hilt/DI
 * @brief   Módulo Hilt que configura y provee la instancia singleton de la base de datos Room ([AppDb]) y sus DAOs.
 *
 * @details Este módulo es responsable de:
 * - Construir la instancia única de [AppDb] utilizando [Room.databaseBuilder],
 * especificando el nombre del archivo ("tibibalance.db") y el contexto de la aplicación.
 * - Configurar la estrategia de migración destructiva (`fallbackToDestructiveMigration`)
 * para simplificar el desarrollo inicial (¡no usar en producción sin migraciones!).
 * - Proveer las instancias de los Data Access Objects (DAOs) necesarios para interactuar
 * con las tablas de la base de datos: [ProfileDao], [HabitDao] y [HabitTemplateDao].
 *
 * Todas las dependencias proporcionadas por este módulo están en el ámbito [SingletonComponent],
 * asegurando que solo se cree una instancia de la base de datos y de cada DAO durante
 * el ciclo de vida de la aplicación.
 */
package com.app.tibibalance.di

import android.content.Context
import androidx.room.Room
import com.app.tibibalance.data.local.AppDb
import com.app.tibibalance.data.local.dao.HabitDao
import com.app.tibibalance.data.local.dao.HabitTemplateDao
import com.app.tibibalance.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @brief Módulo de Dagger Hilt para proporcionar dependencias relacionadas con la base de datos Room.
 * @details Instalado en [SingletonComponent] para asegurar que las instancias
 * proporcionadas (base de datos y DAOs) sean singletons.
 */
@Module
@InstallIn(SingletonComponent::class) // Define el ámbito del módulo y sus providers
object DatabaseModule {

    /* ────────────── Proveedor de la Base de Datos (AppDb) ────────────── */

    /**
     * @brief Provee la instancia singleton de la base de datos Room [AppDb].
     * @details Utiliza [Room.databaseBuilder] para construir la base de datos.
     * La anotación `@ApplicationContext` indica a Hilt que inyecte el contexto
     * de la aplicación. `fallbackToDestructiveMigration()` se usa para evitar
     * definir migraciones explícitas durante el desarrollo; **esto borrará la base de datos
     * si el esquema cambia y no se debe usar en producción sin una estrategia de migración adecuada**.
     * La anotación `@Singleton` asegura que Hilt cree una única instancia.
     *
     * @param ctx El [Context] de la aplicación, inyectado por Hilt.
     * @return La instancia singleton de [AppDb].
     */
    @Provides // Indica a Hilt que este método provee una dependencia
    @Singleton // Asegura que solo haya una instancia de AppDb en toda la app
    fun provideDatabase(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(
            context = ctx,
            klass = AppDb::class.java,
            name = "tibibalance.db" // Nombre del archivo de la base de datos
        )
            .fallbackToDestructiveMigration() // ¡Precaución! Borra datos si el esquema cambia. No usar en producción sin migraciones.
            .build() // Construye la instancia de RoomDatabase

    /* ────────────── Proveedores de DAOs ────────────── */

    /**
     * @brief Provee la instancia del DAO [ProfileDao].
     * @details Obtiene el DAO a partir de la instancia de [AppDb] inyectada.
     * Como `AppDb` es `@Singleton`, Hilt reutilizará la misma instancia de BD
     * para obtener este DAO. Los DAOs proporcionados de esta manera suelen
     * tener el mismo ciclo de vida que la base de datos.
     *
     * @param db La instancia singleton de [AppDb].
     * @return Una instancia de [ProfileDao].
     */
    @Provides
    fun provideProfileDao(db: AppDb): ProfileDao = db.profileDao()

    /**
     * @brief Provee la instancia del DAO [HabitDao].
     * @param db La instancia singleton de [AppDb].
     * @return Una instancia de [HabitDao].
     */
    @Provides
    fun provideHabitDao(db: AppDb): HabitDao = db.habitDao()

    /**
     * @brief Provee la instancia del DAO [HabitTemplateDao].
     * @param db La instancia singleton de [AppDb].
     * @return Una instancia de [HabitTemplateDao].
     */
    @Provides
    fun provideHabitTemplateDao(db: AppDb): HabitTemplateDao = db.habitTemplateDao()
}
