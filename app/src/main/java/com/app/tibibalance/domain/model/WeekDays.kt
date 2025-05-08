/**
 * @file    WeekDays.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define un wrapper seguro para representar un conjunto de días de la semana.
 *
 * @details Utiliza una `value class` ([JvmInline]) para envolver un `Set<Int>`
 * asegurando que solo contenga enteros válidos representando días de la semana
 * (1 para Lunes hasta 7 para Domingo). Proporciona seguridad de tipos y validación
 * en tiempo de construcción. Es [Serializable] para permitir su persistencia.
 *
 * @see NotifConfig Configuración que utiliza esta clase para días de notificación personalizados.
 * @see JvmInline
 * @see Serializable
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline // Import necesario para JvmInline

/**
 * @brief Wrapper inmutable y seguro para un conjunto de días de la semana.
 * @details Representa una selección de días utilizando un [Set] de enteros, donde
 * 1 corresponde a Lunes, 2 a Martes, ..., y 7 a Domingo.
 * La anotación [JvmInline] optimiza el uso en runtime, evitando la creación de un objeto
 * wrapper extra. La anotación [Serializable] permite su uso con Kotlinx Serialization.
 *
 * El bloque `init` valida que todos los enteros en el conjunto `days` estén dentro
 * del rango válido [1, 7], lanzando una [IllegalArgumentException] si la validación falla.
 *
 * @property days El conjunto ([Set]) inmutable de enteros que representan los días seleccionados (1-7).
 * @constructor Crea una instancia de [WeekDays].
 * @param days El conjunto inicial de días.
 * @throws IllegalArgumentException Si el conjunto `days` contiene algún entero fuera del rango 1 a 7.
 */
@JvmInline // Optimización: actúa como Set<Int> en runtime pero con seguridad de tipos en compilación
@Serializable // Permite serializar/deserializar esta clase
value class WeekDays(val days: Set<Int>) {
    // Bloque de inicialización que valida los días al crear una instancia
    init {
        // Verifica que todos los elementos en 'days' estén entre 1 y 7 inclusive.
        // Si la condición no se cumple para algún elemento, lanza IllegalArgumentException.
        require(days.all { day -> day in 1..7 }) {
            "Los días de la semana deben estar entre 1 (Lunes) y 7 (Domingo). Días inválidos encontrados: ${days.filterNot { it in 1..7 }}"
        }
    }

    /**
     * @brief Objeto compañero para [WeekDays].
     * @details Contiene constantes o métodos estáticos relacionados con WeekDays.
     */
    companion object {
        /**
         * @brief Instancia predefinida que representa ningún día seleccionado (conjunto vacío).
         * @details Útil como valor por defecto o para indicar que no aplica selección de días.
         */
        val NONE = WeekDays(emptySet())
    }
}
