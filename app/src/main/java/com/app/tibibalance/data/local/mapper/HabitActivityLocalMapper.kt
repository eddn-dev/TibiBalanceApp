/**
 * @file    HabitActivityLocalMapper.kt
 * @ingroup data_local_mapper
 * @brief   Extensiones de conversión Entity ↔ Domain.
 */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitActivityEntity
import com.app.tibibalance.domain.model.HabitActivity
import kotlinx.datetime.Instant

/** Entity → Domain */
fun HabitActivityEntity.toDomain() = HabitActivity(
    id        = id,
    habitId   = habitId,
    type      = HabitActivity.Type.valueOf(type),
    timestamp = Instant.fromEpochMilliseconds(epochMillis)
)

/** Domain → Entity */
fun HabitActivity.toEntity() = HabitActivityEntity(
    id          = id,
    habitId     = habitId,
    type        = type.name,
    epochMillis = timestamp.toEpochMilliseconds()
)
