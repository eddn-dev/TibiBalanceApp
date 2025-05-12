/**
 * @file    AppDb.kt
 * @ingroup data_local_db
 *
 * @brief   Definición de la base de datos Room para TibiBalance.
 *
 * @details Incluye las nuevas tablas de actividad de hábito y los type-converters
 *          necesarios para `kotlinx.datetime.Instant` y `LocalDate`.
 */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.converter.InstantTypeConverter      // 🆕
import com.app.tibibalance.data.local.dao.*
import com.app.tibibalance.data.local.entity.*
import com.app.tibibalance.data.local.mapper.NotifConverters

@Database(
    entities = [
        UserProfileEntity::class   ,
        HabitEntity::class         ,
        HabitTemplateEntity::class ,
        HabitActivityEntity::class      // 🆕 registra ALERT/COMPLETED/SKIPPED
    ],
    version = 5,                  // 🆕  incrementa al añadir entidad nueva
    exportSchema = true
)
@TypeConverters(
    HabitConverter::class         ,
    InstantTypeConverter::class ,   // 🆕 convierte Instant/LocalDate a tipos primitivos
    NotifConverters::class
)
abstract class AppDb : RoomDatabase() {

    abstract fun profileDao()       : ProfileDao
    abstract fun habitDao()         : HabitDao
    abstract fun habitTemplateDao() : HabitTemplateDao
    abstract fun habitActivityDao() : HabitActivityDao      // 🆕
}
