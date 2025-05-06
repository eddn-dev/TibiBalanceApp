package com.app.tibibalance.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

// data/local/entity/HabitTemplateEntity.kt
@Entity(tableName = "habit_templates")
data class HabitTemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val icon: String,
    val message: String,
    val repeatEvery: Int,
    val repeatType: String,
    val notifMode: String,
    val scheduled: Boolean
)
