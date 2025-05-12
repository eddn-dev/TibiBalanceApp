/**
 * @file    HabitFormMapper.kt
 * @ingroup data_mapper
 * @brief   Conversión de [HabitForm] + [NotifConfig] a la entidad de dominio [Habit].
 *
 * @details
 * Incluye el cálculo de `nextTrigger` (próximo disparo de notificación) para
 * los presets DIARIO y SEMANAL.  Si el usuario desactiva las notificaciones,
 * el resultado es `null` y el Scheduler simplemente no programa nada.
 *
 * El algoritmo:
 *  1.  Toma la lista de horas definidas en `cfg.timesOfDay` (`"HH:mm"`).
 *  2.  Selecciona la próxima hora válida dentro de hoy; si ya pasó, busca mañana
 *      o el siguiente día marcado en `weekDays` cuando el preset es SEMANAL.
 *  3.  Devuelve un `Instant` en la zona horaria del dispositivo.
 */
package com.app.tibibalance.data.mapper

import com.app.tibibalance.domain.model.*
import kotlinx.datetime.*

/* ────────────────────────── API pública ────────────────────────── */

/**
 * Fusiona el [HabitForm] con la configuración [NotifConfig] y produce un
 * [Habit] listo para persistir y programar con [HabitAlertScheduler].
 *
 * @receiver   Información básica y de seguimiento procedente del wizard.
 * @param cfg  Configuración de notificaciones elegida por el usuario.
 * @return     Entidad [Habit] con `nextTrigger` calculado (o `null`).
 */
fun HabitForm.toHabit(cfg: NotifConfig): Habit {
    val zone  = TimeZone.currentSystemDefault()
    val now   = Clock.System.now().toLocalDateTime(zone)

    return Habit(
        id           = "",
        name         = name.trim(),
        description  = desc.trim(),
        category     = category,
        icon         = icon,
        session      = Session(sessionQty, sessionUnit),
        repeatPreset = repeatPreset,
        period       = Period(periodQty, periodUnit),
        notifConfig  = cfg,
        challenge    = challenge,
        createdAt    = Clock.System.now(),
        nextTrigger  = computeNextTrigger(now, cfg, repeatPreset, weekDays, zone)
    )
}

/* ───────────── Helpers privados ───────────── */

private fun Set<Int>.toDayOfWeek(): Set<DayOfWeek> =
    mapNotNull { DayOfWeek.values().getOrNull((it - 1).coerceIn(0,6)) }
        .toSet()

private fun computeNextTrigger(
    now: LocalDateTime,
    cfg: NotifConfig,
    preset: RepeatPreset,
    weekDaysInt: Set<Int>,          // ← llega como Ints
    zone: TimeZone
): Instant? {
    if (!cfg.enabled) return null

    val dayTimes = cfg.timesOfDay.map(LocalTime::parse).sorted()

    val todayNext = dayTimes.firstOrNull { it > now.time }
        ?.let { LocalDateTime(now.date, it) }

    val weekDays = weekDaysInt.toDayOfWeek()         // ← convertimos aquí

    fun nextValidDate(base: LocalDate): LocalDate {
        if (preset != RepeatPreset.SEMANAL) return base
        var d = base
        while (d.dayOfWeek !in weekDays) {
            d = d.plus(1, DateTimeUnit.DAY)
        }
        return d
    }

    val triggerLdt = todayNext ?: run {
        val date = nextValidDate(now.date.plus(1, DateTimeUnit.DAY))
        LocalDateTime(date, dayTimes.first())
    }
    return triggerLdt.toInstant(zone)
}
