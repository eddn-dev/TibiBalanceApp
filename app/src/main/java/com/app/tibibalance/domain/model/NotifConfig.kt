package com.app.tibibalance.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NotifConfig(
    /* ── Estado general ─────────────────────────── */
    val enabled     : Boolean        = false,

    /* ── Contenido y schedule ───────────────────── */
    val message     : String         = "",
    val timesOfDay  : List<String>   = emptyList(),   // ["08:00","18:30"]
    val weekDays    : WeekDays       = WeekDays.NONE, // solo PERSONALIZADO
    val advanceMin  : Int            = 0,             // 0 ⇒ puntual

    /* NUEVO → fecha inicial de los avisos */
    val startsAt    :  kotlinx.datetime.LocalDate? = null,

    /* ── Canal y estilo ─────────────────────────── */
    val mode        : NotifMode      = NotifMode.SILENT,
    val vibrate     : Boolean        = true,

    /* ── Vida útil ──────────────────────────────── */
    val expiresAt   : kotlinx.datetime.LocalDate? = null,
)

enum class NotifMode { SILENT, SOUND, VIBRATE }
