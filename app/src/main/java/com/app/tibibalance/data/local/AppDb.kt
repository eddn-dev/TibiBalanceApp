/* data/local/AppDb.kt */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.dao.*
import com.app.tibibalance.data.local.entity.*
import com.app.tibibalance.data.local.mapper.NotifConverters // ← NUEVO

/**
 * @file    AppDb.kt
 * @ingroup data_local
 * @brief   Definición de la base de datos Room de **TibiBalance**.
 *
 * - Incluye las entidades:
 *   - [UserProfileEntity]
 *   - [HabitEntity]
 *   - [HabitTemplateEntity]
 * - Registra los *TypeConverter*:
 *   - [HabitConverter]  → serializa `Habit` y tipos `Instant` / `LocalDate`.
 *   - [NotifConverters] → serializa listas usadas en `NotifConfig`.
 *
 * La versión **3** refleja el último cambio de esquema (añadido de listas
 * para notificaciones).  Se habilita **`exportSchema = true`** para que
 * Room genere archivos de historial en `schemas/`, lo cual permite
 * verificar migraciones durante el *CI*.
 */

/**
 * @brief Subclase de [RoomDatabase] que expone los DAO de la aplicación.
 *
 * Los métodos abstractos devuelven implementaciones generadas
 * automáticamente por Room en tiempo de compilación.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitTemplateEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(
    HabitConverter::class,
    NotifConverters::class
)
abstract class AppDb : RoomDatabase() {

    /** @brief Acceso al DAO de perfil de usuario. */
    abstract fun profileDao(): ProfileDao

    /** @brief Acceso al DAO de hábitos. */
    abstract fun habitDao(): HabitDao

    /** @brief Acceso al DAO de plantillas de hábito. */
    abstract fun habitTemplateDao(): HabitTemplateDao
}
