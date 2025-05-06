/* data/local/entity/HabitEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val json: String
)
