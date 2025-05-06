/* domain/model/HabitForm.kt */
package com.app.tibibalance.domain.model

data class HabitForm(
    val name         : String         = "",
    val desc         : String         = "",

    /* ① ahora es el enum, no String */
    val category     : HabitCategory  = HabitCategory.SALUD,

    val icon         : String         = "FitnessCenter",

    // ── Sesión ──
    val sessionQty   : Int?           = null,
    val sessionUnit  : SessionUnit    = SessionUnit.INDEFINIDO,

    // ── Frecuencia ──
    val repeatPattern: RepeatPattern  = RepeatPattern.INDEFINIDO,

    // ── Periodo total ──
    val periodQty    : Int?           = null,
    val periodUnit   : PeriodUnit     = PeriodUnit.INDEFINIDO,

    val notify       : Boolean        = false
) {
    /** Prefill desde plantilla remota */
    fun prefillFromTemplate(t: HabitTemplate) = copy(
        name          = t.name,
        desc          = t.description,
        category      = t.category,         // ya es HabitCategory
        icon          = t.icon,
        sessionQty    = t.sessionQty,
        sessionUnit   = t.sessionUnit,
        repeatPattern = t.repeatPattern,
        periodQty     = t.periodQty,
        periodUnit    = t.periodUnit,
        notify        = t.notifCfg.mode != NotifMode.SILENT
    )
}
