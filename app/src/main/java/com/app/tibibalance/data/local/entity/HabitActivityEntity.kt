/**
 * @file    HabitActivityEntity.kt
 * @ingroup data_local_entity
 * @brief   Tabla Room que registra cada interacción con un hábito.
 */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_activity")
data class HabitActivityEntity(
    @PrimaryKey val id      : String,
    val habitId             : String,
    val type                : String,  // ALERT | COMPLETED | SKIPPED
    val epochMillis         : Long
)
