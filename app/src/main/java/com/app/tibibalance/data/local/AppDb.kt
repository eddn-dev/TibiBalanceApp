/* data/local/AppDb.kt */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.dao.HabitDao
import com.app.tibibalance.data.local.dao.HabitTemplateDao          // ← nuevo DAO
import com.app.tibibalance.data.local.dao.ProfileDao
import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.data.local.entity.HabitTemplateEntity    // ← nueva entidad
import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.data.local.mapper.NotifConverters

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
    HabitConverter::class,   // ➊ tu conversor anterior
    NotifConverters::class   // ➋ el nuevo para listas <String>/<Int>
)
abstract class AppDb : RoomDatabase() {

    /* DAOs existentes */
    abstract fun profileDao()       : ProfileDao
    abstract fun habitDao()         : HabitDao

    /* Nuevo DAO */
    abstract fun habitTemplateDao() : HabitTemplateDao
}
