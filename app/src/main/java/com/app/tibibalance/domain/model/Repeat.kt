package com.app.tibibalance.domain.model

import com.google.type.DayOfWeek

/** Configuración de repetición de un hábito. */
sealed interface Repeat {
    object None : Repeat

    /** Diario o cada *n* días. 1 = diario. */
    data class Daily(val every: Int = 1) : Repeat

    /** Semanal, indicando los días de la semana (DayOfWeek.MONDAY, …). */
    data class Weekly(val days: Set<DayOfWeek>) : Repeat

    /** Mensual, disparado el día *dayOfMonth* (1…31). */
    data class Monthly(val dayOfMonth: Int) : Repeat
}
