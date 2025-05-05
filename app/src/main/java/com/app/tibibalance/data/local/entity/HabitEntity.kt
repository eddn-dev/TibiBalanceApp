/* data/local/entity/HabitEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")            // ✅ solo @Entity
data class HabitEntity(
    @PrimaryKey val id: String,
    val json: String                     // ✅ ningún tipo exótico aquí
)
