// src/main/java/com/app/data/local/entity/MetricsEntity.kt
package com.app.tibibalance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_metrics")
data class MetricsEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "steps")
    val steps: Long,

    @ColumnInfo(name = "active_calories")
    val activeCalories: Double,

    @ColumnInfo(name = "exercise_minutes")
    val exerciseMinutes: Long,

    @ColumnInfo(name = "avg_heart_rate")
    val avgHeartRate: Double?
)
