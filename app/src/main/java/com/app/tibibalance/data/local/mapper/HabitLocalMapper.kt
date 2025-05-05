package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.domain.model.Habit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/* HabitEntity ⇄ Habit (dominio) */
fun HabitEntity.toDomain(): Habit =
    Json.decodeFromString<Habit>(json)              // serializer se infiere

fun Habit.toEntity(): HabitEntity =
    HabitEntity(id, Json.encodeToString(this))      // idem
