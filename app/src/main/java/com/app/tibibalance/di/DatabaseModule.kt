/* di/DatabaseModule.kt */
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
 * @file    DatabaseModule.kt
 * @ingroup di
 * @brief   Módulo Hilt que provee la instancia única de Room y sus DAO.
 *
 * <h4>Responsabilidades</h4>
 * <ul>
 *   <li>Construir la base de datos {@link AppDb} con el nombre
 *       <code>"tibibalance.db"</code>.</li>
 *   <li>Exponer los DAO <code>ProfileDao</code>, <code>HabitDao</code> y
 *       <code>HabitTemplateDao</code> como dependencias inyectables.</li>
 * </ul>
 *
 * > **Nota:** Se usa <code>fallbackToDestructiveMigration()</code> dado que el
 * > proyecto está en fase de desarrollo académico y aún no se han
 * > implementado rutas de migración entre versiones de esquema.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /* ────────────── Base de datos ────────────── */

    /**
     * @brief   Provee la instancia singleton de Room.
     *
     * @param ctx Contexto de aplicación (inyectado por Hilt).
     * @return   Objeto {@link AppDb}.
     */
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(ctx, AppDb::class.java, "tibibalance.db")
            .fallbackToDestructiveMigration()   // uso escolar, sin rutas de migración
            .build()                            // Hilt gestiona el singleton

    /* ────────────── DAOs existentes ────────────── */

    /** @brief Provee {@link ProfileDao}. */
    @Provides fun provideProfileDao(db: AppDb): ProfileDao       = db.profileDao()

    /** @brief Provee {@link HabitDao}. */
    @Provides fun provideHabitDao  (db: AppDb): HabitDao         = db.habitDao()

    /* ────────────── Nuevo DAO (plantillas) ────────────── */

    /** @brief Provee {@link HabitTemplateDao}. */
    @Provides fun provideHabitTemplateDao(db: AppDb): HabitTemplateDao = db.habitTemplateDao()
}
