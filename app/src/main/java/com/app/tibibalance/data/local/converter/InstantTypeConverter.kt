/**
 * @file    InstantTypeConverter.kt
 * @ingroup data_local_converter
 * @brief   Converters de kotlinx.datetime <-> tipos primitivos.
 */
package com.app.tibibalance.data.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantTypeConverter {

    @TypeConverter
    fun fromInstant(i: Instant?): Long? = i?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(ms: Long?): Instant? = ms?.let { Instant.fromEpochMilliseconds(it) }
}
