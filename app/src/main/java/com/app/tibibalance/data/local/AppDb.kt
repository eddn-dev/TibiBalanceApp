/* data/local/AppDb.kt */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.data.local.entity.UserProfileEntity   // ← conviértelo en @Entity

@Database(
    entities = [
        UserProfileEntity::class,   // ahora SÍ es @Entity
        HabitEntity::class          // nueva tabla de hábitos
    ],
    version = 2,                    // bump: se añadió tabla
    exportSchema = true             // recomendable en producción
)
@TypeConverters(HabitConverter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun habitDao()   : HabitDao
}
