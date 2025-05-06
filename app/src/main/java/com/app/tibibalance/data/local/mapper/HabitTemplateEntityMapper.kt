/* data/local/mapper/HabitTemplateEntityMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import com.app.tibibalance.domain.model.*

/* ---------- Dominio ➜ Entity ---------- */
fun HabitTemplate.toEntity() = HabitTemplateEntity(
    id              = id,
    name            = name,
    description     = description,
    category        = category,
    icon            = icon,

    sessionQty      = sessionQty,
    sessionUnit     = sessionUnit,

    repeatPattern   = repeatPattern,
    periodQty       = periodQty,
    periodUnit      = periodUnit,

    notifMode       = notifCfg.mode,
    notifMessage    = notifCfg.message,
    notifTimesOfDay = notifCfg.timesOfDay,
    notifDaysOfWeek = notifCfg.daysOfWeek,
    notifAdvanceMin = notifCfg.advanceMinutes,
    notifVibrate    = notifCfg.vibrate,

    scheduled       = scheduled
)

/* ---------- Entity ➜ Dominio ---------- */
fun HabitTemplateEntity.toDomain() = HabitTemplate(
    id            = id,
    name          = name,
    description   = description,
    category      = category,
    icon          = icon,

    sessionQty    = sessionQty,
    sessionUnit   = sessionUnit,

    repeatPattern = repeatPattern,
    periodQty     = periodQty,
    periodUnit    = periodUnit,

    notifCfg      = NotifConfig(
        mode           = notifMode,
        message        = notifMessage,
        timesOfDay     = notifTimesOfDay,
        daysOfWeek     = notifDaysOfWeek,
        advanceMinutes = notifAdvanceMin,
        vibrate        = notifVibrate
    ),

    scheduled     = scheduled
)
