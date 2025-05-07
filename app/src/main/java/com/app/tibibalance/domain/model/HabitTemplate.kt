package com.app.tibibalance.domain.model

/**
 * Documento `habitTemplates` en Firestore ⇄ modelo dominio.
 *
 * ▸ Los enums evitan “strings mágicos”.
 * ▸ Los campos opcionales (`Int?`, `Set<Int>`, etc.) pueden omitirse si son null / vacíos.
 */
data class HabitTemplate(

    /* ─── Identidad y aspecto ───────────────────────── */
    val id          : String         = "",
    val name        : String         = "",
    val description : String         = "",
    val category    : HabitCategory  = HabitCategory.SALUD,
    val icon        : String         = "FitnessCenter",

    /* ─── Sesión ────────────────────────────────────── */
    val sessionQty  : Int?           = null,
    val sessionUnit : SessionUnit    = SessionUnit.INDEFINIDO,

    /* ─── Repetición ────────────────────────────────── */
    val repeatPreset: RepeatPreset   = RepeatPreset.INDEFINIDO,
    val weekDays    : Set<Int>       = emptySet(),          // solo si repeatPreset = PERSONALIZADO

    /* ─── Periodo total ─────────────────────────────── */
    val periodQty   : Int?           = null,
    val periodUnit  : PeriodUnit     = PeriodUnit.INDEFINIDO,

    /* ─── Notificación avanzada ─────────────────────── */
    val notifCfg    : NotifConfig    = NotifConfig(),       // incluye enabled = true/false

    /* ─── Metadata ──────────────────────────────────── */
    val scheduled   : Boolean        = false               // true → ya fue planificado por el scheduler
)
