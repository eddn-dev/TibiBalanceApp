/* domain/model/HabitForm.kt */
package com.app.tibibalance.domain.model

data class HabitForm(
    val name         : String        = "",
    val desc         : String        = "",
    val category     : String        = "Salud",

    val sessionQty   : Int?          = null,
    val sessionUnit  : SessionUnit   = SessionUnit.INDEFINIDO,

    val repeatPattern: RepeatPattern = RepeatPattern.INDEFINIDO,

    val periodQty    : Int?          = null,
    val periodUnit   : PeriodUnit    = PeriodUnit.INDEFINIDO,

    val notify       : Boolean       = false
) {
    /** Rellena el formulario a partir de una plantilla de Firestore. */
    fun prefillFromTemplate(t: HabitTemplate) = copy(
        name          = t.name,
        desc          = t.description,
        category      = t.category,
        sessionQty    = t.sessionQty,
        sessionUnit   = t.sessionUnit,
        repeatPattern = t.repeatPattern,
        periodQty     = t.periodQty,
        periodUnit    = t.periodUnit,
        notify        = t.notifCfg.mode != NotifMode.SILENT   // ← cambio clave
    )
}
