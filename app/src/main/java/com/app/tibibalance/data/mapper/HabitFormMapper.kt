package com.app.tibibalance.data.mapper

import com.app.tibibalance.domain.model.*
import kotlinx.datetime.Clock

/** Convierte el borrador y la cfg de notificación al modelo final listo
 *  para guardar; el ID se genera en el repo.                        */
fun HabitForm.toHabit(cfg: NotifConfig): Habit =
    Habit(
        id           = "",                     // lo asignará addHabit()
        name         = name,
        description  = desc,
        category     = category,
        icon         = icon,
        session      = Session(sessionQty, sessionUnit),
        repeatPreset = repeatPreset,
        period       = Period(periodQty, periodUnit),
        notifConfig  = cfg,
        challenge    = challenge,
        createdAt    = Clock.System.now()
    )
