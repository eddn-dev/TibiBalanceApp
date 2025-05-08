/**
 * @file    Repeat.kt
 * @ingroup domain
 * @brief   Patrón de repetición asociado a un hábito.
 *
 * Define un conjunto sellado con las variantes de periodicidad admitidas:
 * - Sin repetición.
 * - Intervalo diario (o cada *n* días).
 * - Selección semanal por día de la semana.
 * - Ejecución mensual en un día concreto.
 */
package com.app.tibibalance.domain.model

import com.google.type.DayOfWeek

/** Patrón base de repetición. */
sealed interface Repeat {

    /** Sin repetición. */
    object None : Repeat

    /**
     * Repetición cada *n* días.
     * @param every Número de días entre ejecuciones (1 = diario).
     */
    data class Daily(val every: Int = 1) : Repeat

    /**
     * Repetición semanal.
     * @param days Conjunto de días de la semana que activan el hábito.
     */
    data class Weekly(val days: Set<DayOfWeek>) : Repeat

    /**
     * Repetición mensual.
     * @param dayOfMonth Día del mes (1‥31) que dispara el hábito.
     */
    data class Monthly(val dayOfMonth: Int) : Repeat
}
