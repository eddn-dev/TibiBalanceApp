// src/main/java/com/app/tibibalance/data/local/AppDb.kt
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.converter.InstantTypeConverter
import com.app.tibibalance.data.local.dao.*
import com.app.tibibalance.data.local.entity.*
import com.app.tibibalance.data.local.dao.MetricsDao
import com.app.tibibalance.data.local.entity.MetricsEntity
import com.app.tibibalance.data.local.mapper.NotifConverters

@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitTemplateEntity::class,
        HabitActivityEntity::class,
        MetricsEntity::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(
    HabitConverter::class,
    InstantTypeConverter::class,
    NotifConverters::class
)
abstract class AppDb : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun habitDao(): HabitDao
    abstract fun habitTemplateDao(): HabitTemplateDao
    abstract fun habitActivityDao(): HabitActivityDao

    /** @brief DAO para las métricas diarias. */
    abstract fun metricsDao(): MetricsDao
}
