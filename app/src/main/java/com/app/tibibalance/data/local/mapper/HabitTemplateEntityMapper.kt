// data/local/mapper/HabitTemplateEntityMapper.kt
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import com.app.tibibalance.domain.model.HabitTemplate

fun HabitTemplate.toEntity() = HabitTemplateEntity(
    id, name, description, category, icon,
    message, repeatEvery, repeatType, notifMode, scheduled
)

fun HabitTemplateEntity.toDomain() = HabitTemplate(
    id, name, description, category, icon,
    message, repeatEvery, repeatType, notifMode, scheduled
)
