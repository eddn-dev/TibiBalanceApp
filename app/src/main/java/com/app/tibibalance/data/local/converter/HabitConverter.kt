package com.app.tibibalance.data.local.converter

import androidx.room.TypeConverter
import com.app.tibibalance.domain.model.Habit
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HabitConverter {

    /* -------- Habit ↔ JSON -------- */
    @TypeConverter
    fun fromJson(json: String): Habit = Json.decodeFromString(json)

    @TypeConverter
    fun toJson(habit: Habit): String = Json.encodeToString(habit)

    /* -------- Instant ↔ epochMillis -------- */
    @TypeConverter
    fun fromInstant(i: Instant?): Long? = i?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(epoch: Long?): Instant? =
        epoch?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromKxLocalDate(d: kotlinx.datetime.LocalDate?): String? =
        d?.toString()              // «2025-05-07»

    @TypeConverter
    fun toKxLocalDate(iso: String?): kotlinx.datetime.LocalDate? =
        iso?.let { kotlinx.datetime.LocalDate.parse(it) }

}
