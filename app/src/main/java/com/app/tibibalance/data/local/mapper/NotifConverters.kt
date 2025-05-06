package com.app.tibibalance.data.local.mapper
import androidx.room.TypeConverter

private const val SEP = "|"

class NotifConverters {

    /* -------- List<String> (“08:00|20:30”) -------- */
    @TypeConverter
    fun fromTimes(src: List<String>?): String = src?.joinToString(SEP).orEmpty()

    @TypeConverter
    fun toTimes(db: String): List<String> =
        db.takeIf { it.isNotEmpty() }?.split(SEP) ?: emptyList()

    /* -------- List<Int> (“1|3|5”) -------- */
    @TypeConverter
    fun fromDays(src: List<Int>?): String = src?.joinToString(SEP).orEmpty()

    @TypeConverter
    fun toDays(db: String): List<Int> =
        db.takeIf { it.isNotEmpty() }?.split(SEP)?.map { it.toInt() } ?: emptyList()
}
