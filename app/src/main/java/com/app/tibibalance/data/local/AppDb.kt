/* data/local/AppDb.kt */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.dao.*
import com.app.tibibalance.data.local.entity.*
import com.app.tibibalance.data.local.mapper.NotifConverters         // ← NUEVO

@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitTemplateEntity::class
    ],
    version = 3,
    exportSchema = true
)
/* ──────────────────────────────────────────────────────────────
 * Todos los convertidores “clásicos” se registran aquí con
 * @TypeConverters, lo que le dice a Room que los instancie por
 * reflexión (deben tener ctor vacío o ser object).
 * ────────────────────────────────────────────────────────────── */
@TypeConverters(
    HabitConverter::class,
    NotifConverters::class
)
abstract class AppDb : RoomDatabase() {
    abstract fun profileDao()      : ProfileDao
    abstract fun habitDao()        : HabitDao
    abstract fun habitTemplateDao(): HabitTemplateDao
}
