/**
 * @file    WeekDays.kt
 * @ingroup domain
 * @brief   Wrapper de los días de la semana usados en configuraciones personalizadas.
 *
 * Representa un conjunto de enteros (1 = lunes … 7 = domingo).
 * Valida en tiempo de construcción que todos los valores estén dentro
 * del rango permitido.
 *
 * @constructor Crea una instancia con el conjunto de días indicado.
 * @param days Conjunto de enteros entre 1 y 7.
 * @throws IllegalArgumentException Si algún valor queda fuera del rango.
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class WeekDays(val days: Set<Int>) {
    init { require(days.all { it in 1..7 }) }

    /** Instancia vacía que representa “sin selección”. */
    companion object { val NONE = WeekDays(emptySet()) }
}
