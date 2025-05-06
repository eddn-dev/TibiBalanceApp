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

@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitTemplateEntity::class                                // ← añadida
    ],
    version = 3,            // bump: se añade tabla habit_templates
    exportSchema = true
)
@TypeConverters(HabitConverter::class)
abstract class AppDb : RoomDatabase() {

    /* DAOs existentes */
    abstract fun profileDao()       : ProfileDao
    abstract fun habitDao()         : HabitDao

    /* DAO para plantillas ----- */
    abstract fun habitTemplateDao() : HabitTemplateDao
}
