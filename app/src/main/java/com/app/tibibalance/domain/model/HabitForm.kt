package com.app.tibibalance.domain.model

/** Modelo intermedio que la UI utiliza antes de persistir el `Habit`. */
data class HabitForm(
    /* ─── Básicos ────────────────────────────────────────── */
    val name        : String            = "",
    val desc        : String            = "",
    val category    : HabitCategory     = HabitCategory.SALUD,
    val icon        : String            = "FitnessCenter",

    /* ─── Sesión ─────────────────────────────────────────── */
    val sessionQty  : Int?              = null,                     // null si unit = INDEFINIDO
    val sessionUnit : SessionUnit       = SessionUnit.INDEFINIDO,

    /* ─── Repetición ─────────────────────────────────────── */
    val repeatPreset: RepeatPreset      = RepeatPreset.INDEFINIDO,
    val weekDays    : Set<Int>          = emptySet(),               // solo PERSONALIZADO

    /* ─── Periodo total ──────────────────────────────────── */
    val periodQty   : Int?              = null,
    val periodUnit  : PeriodUnit        = PeriodUnit.INDEFINIDO,

    /* ─── Opciones extra ─────────────────────────────────── */
    val notify      : Boolean           = false,                    // se ignora si repeat = INDEFINIDO
    val challenge   : Boolean           = false                     // modo reto
) {

    /** Crea un formulario precargado desde una plantilla remota/local. */
    fun prefillFromTemplate(t: HabitTemplate) = copy(
        name         = t.name,
        desc         = t.description,
        category     = t.category,
        icon         = t.icon,
        sessionQty   = t.sessionQty,
        sessionUnit  = t.sessionUnit,
        repeatPreset = t.repeatPreset,
        weekDays     = if (t.repeatPreset == RepeatPreset.PERSONALIZADO)
            t.notifCfg.weekDays.days
        else emptySet(),
        periodQty    = t.periodQty,
        periodUnit   = t.periodUnit,
        notify       = t.notifCfg.enabled
    )
}
