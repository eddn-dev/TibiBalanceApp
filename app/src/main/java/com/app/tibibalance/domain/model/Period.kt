/**
 * @file    Period.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define la duración total o el periodo de seguimiento para un hábito ([Habit]).
 *
 * @details Esta data class, junto con el enum [PeriodUnit], permite especificar
 * por cuánto tiempo se planea seguir un hábito antes de considerarlo
 * completado o antes de que expire (e.g., un reto de 30 días).
 * Es parte del modelo de dominio [Habit].
 *
 * @see Habit Modelo principal que contiene esta configuración.
 * @see PeriodUnit Enumeración de las unidades de tiempo posibles.
 * @see Serializable
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

/**
 * @brief Representa el periodo de duración total de un hábito.
 * @details Combina una cantidad numérica opcional con una unidad de tiempo ([PeriodUnit]).
 * Si la cantidad (`qty`) es `null` o la unidad (`unit`) es [PeriodUnit.INDEFINIDO],
 * se interpreta que el hábito no tiene una duración definida.
 * Está marcado como [Serializable] para permitir su persistencia.
 *
 * @property qty La cantidad numérica de unidades de tiempo que dura el periodo (e.g., 30, 4, 6).
 * Puede ser `null` si el periodo es indefinido. Por defecto `null`.
 * @property unit La unidad de tiempo ([PeriodUnit]) asociada a la cantidad `qty`
 * (e.g., DIAS, SEMANAS, MESES). Por defecto [PeriodUnit.INDEFINIDO].
 */
@Serializable // Permite serializar/deserializar esta clase
data class Period(
    val qty : Int?       = null, // Cantidad nullable, default null
    val unit: PeriodUnit = PeriodUnit.INDEFINIDO // Unidad, default INDEFINIDO
)

/**
 * @brief Enumeración que define las posibles unidades de tiempo para el periodo de un [Habit].
 * @details Especifica si la duración del hábito se mide en días, semanas, meses, o si es indefinida.
 */
enum class PeriodUnit {
    /** @brief Indica que el hábito no tiene una duración definida o límite de tiempo. */
    INDEFINIDO,
    /** @brief La duración del hábito se mide en días. */
    DIAS,
    /** @brief La duración del hábito se mide en semanas. */
    SEMANAS,
    /** @brief La duración del hábito se mide en meses. */
    MESES
}
