package com.app.tibibalance.domain.model

import com.app.tibibalance.data.serialization.InstantEpochMillisSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable



@Serializable
data class Habit(
    val id            : String = "",
    val name          : String,
    val description   : String,
    val category      : HabitCategory,
    val icon          : String,

    /* ── Configuración de seguimiento ───────────────────────── */
    val session       : Session      = Session(),        // opcional
    val repeatPreset  : RepeatPreset = RepeatPreset.INDEFINIDO,
    val period        : Period       = Period(),

    /* ── Notificaciones (calculadas en base a repeat/period) ── */
    val notifConfig   : NotifConfig  = NotifConfig(),

    /* ── Modo reto ──────────────────────────────────────────── */
    val challenge     : Boolean      = false,            // true ⇒ config inmutable
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeFrom : Instant?     = null,             // fecha de inicio del reto
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeTo   : Instant?     = null,             // fecha fin (si hay Period)

    /* ── Sistema ────────────────────────────────────────────── */
    @Serializable(with = InstantEpochMillisSerializer::class)
    val createdAt   : Instant = Clock.System.now(),
    val nextTrigger   : Instant?     = null              // calculado por el scheduler
)


