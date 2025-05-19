/**
 * @file    DatabaseModule.kt
 * @ingroup di_module
 *
 * @brief   Módulo Hilt que configura y provee la instancia singleton de la base de datos
 *          Room ([AppDb]) y TODOS sus DAOs, incluido el nuevo [HabitActivityDao]. // 🆕
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
import com.app.tibibalance.data.local.dao.*
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(
            context = ctx,
            klass   = AppDb::class.java,
            name    = "tibibalance.db"
        )
            // ⚠️ OJO: elimina datos si cambias el esquema sin migrar.
            .fallbackToDestructiveMigration()
            .build()

    /* ────────────── DAOs ────────────── */

    @Provides fun provideProfileDao      (db: AppDb): ProfileDao       = db.profileDao()
    @Provides fun provideHabitDao        (db: AppDb): HabitDao         = db.habitDao()
    @Provides fun provideHabitTemplateDao(db: AppDb): HabitTemplateDao = db.habitTemplateDao()

    /** @brief Provee el DAO que registra cada ALERT/COMPLETED/SKIPPED de un hábito. */ // 🆕
    @Provides fun provideHabitActivityDao(db: AppDb): HabitActivityDao = db.habitActivityDao() // 🆕

    /** @brief Provee el DAO para el registro de emociones diarias. */
    @Provides
    fun provideEmotionDao(db: AppDb): EmotionDao = db.emotionDao()

}
