/* data/local/AppDb.kt */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.tibibalance.domain.model.UserProfile

/**
 * Central Room database for the app.
 *
 * – Lists every @Entity you want to persist.
 * – Exposes each DAO the rest of the code needs.
 */
@Database(
    entities = [UserProfile::class],   // ① one table for now
    version  = 1,                     // ② bump when you change schema
    exportSchema = false              // ③ disable auto-export (dev only)
)
abstract class AppDb : RoomDatabase() {

    /** Primary accessor for profile data */
    abstract fun profileDao(): ProfileDao
}
