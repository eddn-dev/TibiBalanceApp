/* data/local/mapper/HabitTemplateEntityMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import com.app.tibibalance.domain.model.*

/* ───────────── Dominio ➜ Entity ───────────── */
fun HabitTemplate.toEntity() = HabitTemplateEntity(
    id              = id,
    name            = name,
    description     = description,
    category        = category.name,
    icon            = icon,

    sessionQty      = sessionQty,
    sessionUnit     = sessionUnit,

    repeatPreset    = repeatPreset,
    periodQty       = periodQty,
    periodUnit      = periodUnit,

    notifMode       = notifCfg.mode,
    notifMessage    = notifCfg.message,
    notifTimesOfDay = notifCfg.timesOfDay,
    /* ⬇⬇ convierte Set<Int> → List<Int> */
    notifDaysOfWeek = notifCfg.weekDays.days.toList(),
    notifAdvanceMin = notifCfg.advanceMin,
    notifVibrate    = notifCfg.vibrate,

    scheduled       = scheduled
)

/* ───────────── Entity ➜ Dominio ───────────── */
fun HabitTemplateEntity.toDomain() = HabitTemplate(
    id            = id,
    name          = name,
    description   = description,
    category      = HabitCategory.fromRaw(category),
    icon          = icon,

    sessionQty    = sessionQty,
    sessionUnit   = sessionUnit,

    repeatPreset  = repeatPreset,
    periodQty     = periodQty,
    periodUnit    = periodUnit,

    notifCfg      = NotifConfig(
        mode        = notifMode,
        message     = notifMessage,
        timesOfDay  = notifTimesOfDay,
        /* ⬇⬇ convierte List<Int> → Set<Int> → WeekDays */
        weekDays    = WeekDays(notifDaysOfWeek.toSet()),
        advanceMin  = notifAdvanceMin,
        vibrate     = notifVibrate
    ),

    scheduled     = scheduled
)
