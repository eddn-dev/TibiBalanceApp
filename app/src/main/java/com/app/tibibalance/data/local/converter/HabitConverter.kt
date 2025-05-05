package com.app.tibibalance.data.local.converter

import androidx.room.TypeConverter
import com.app.tibibalance.domain.model.Habit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HabitConverter {

    @TypeConverter
    fun fromJson(json: String): Habit =
        Json.decodeFromString(json)          // serializer se infiere

    @TypeConverter
    fun toJson(habit: Habit): String =
        Json.encodeToString(habit)           // idem
}
