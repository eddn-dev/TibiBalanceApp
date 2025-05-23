/**
 * @file    DatabaseModule.kt
 * @ingroup di_module
 *
 * @brief   Módulo Hilt que configura y provee la instancia singleton de la base de datos
 *          Room ([AppDb]) y TODOS sus DAOs, incluido el nuevo [HabitActivityDao] y [MetricsDao].
 *
 * @details  Responsabilidades:
 *  • Crear la base de datos con [`Room.databaseBuilder`].
 *  • Aplicar `fallbackToDestructiveMigration()` **solo en desarrollo**.
 *  • Exponer, vía `@Provides`, cada DAO requerido por los repositorios de datos.
 *
 * Todas las dependencias viven en [SingletonComponent] ⇒ una única instancia durante
 * toda la vida de la aplicación.
 */
package com.app.tibibalance.di

import android.content.Context
import androidx.room.Room
import com.app.tibibalance.data.local.AppDb
import com.app.tibibalance.data.local.dao.HabitActivityDao
import com.app.tibibalance.data.local.dao.HabitDao
import com.app.tibibalance.data.local.dao.HabitTemplateDao
import com.app.tibibalance.data.local.dao.MetricsDao
import com.app.tibibalance.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /* ────────────── Base de datos ────────────── */

    /**
     * @brief Construye la instancia singleton de la base de datos Room.
     * @param ctx Contexto de la aplicación.
     * @return Instancia de [AppDb].
     *
     * ⚠️ Si cambias el esquema sin migrar, se eliminarán los datos existentes.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(
            context = ctx,
            klass   = AppDb::class.java,
            name    = "tibibalance.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    /* ────────────── DAOs ────────────── */

    /** @brief DAO para manejar el perfil de usuario. */
    @Provides
    fun provideProfileDao(db: AppDb): ProfileDao =
        db.profileDao()

    /** @brief DAO para gestionar entidades de hábito. */
    @Provides
    fun provideHabitDao(db: AppDb): HabitDao =
        db.habitDao()

    /** @brief DAO para manejar plantillas de hábito. */
    @Provides
    fun provideHabitTemplateDao(db: AppDb): HabitTemplateDao =
        db.habitTemplateDao()

    /** @brief DAO para registrar cada ALERT/COMPLETED/SKIPPED de un hábito. */
    @Provides
    fun provideHabitActivityDao(db: AppDb): HabitActivityDao =
        db.habitActivityDao()

    /** @brief DAO para gestionar las métricas diarias de salud. */
    @Provides
    fun provideMetricsDao(db: AppDb): MetricsDao =
        db.metricsDao()
}
