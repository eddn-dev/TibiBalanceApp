/**
 * @file    Session.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define la duración o cantidad asociada a una única sesión de un hábito ([Habit]).
 *
 * @details Esta data class, junto con el enum [SessionUnit], permite especificar
 * cuánto dura o qué cantidad implica realizar el hábito una vez
 * (e.g., "10 minutos", "1 hora"). Es parte del modelo de dominio [Habit].
 *
 * @see Habit Modelo principal que contiene esta configuración.
 * @see SessionUnit Enumeración de las unidades de tiempo/cantidad posibles.
 * @see Serializable
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

/**
 * @brief Representa la configuración de una sesión individual de un hábito.
 * @details Combina una cantidad numérica opcional (`qty`) con una unidad ([SessionUnit]).
 * Si la cantidad es `null` o la unidad es [SessionUnit.INDEFINIDO], se interpreta
 * que la sesión no tiene una duración o cantidad medible específica.
 * Está marcado como [Serializable] para permitir su persistencia.
 *
 * @property qty La cantidad numérica asociada a la sesión (e.g., 10, 1).
 * Puede ser `null` si la sesión es indefinida en cantidad o duración. Por defecto `null`.
 * @property unit La unidad de medida ([SessionUnit]) para la cantidad `qty`
 * (e.g., MINUTOS, HORAS). Por defecto [SessionUnit.INDEFINIDO].
 */
@Serializable // Permite serializar/deserializar esta clase
data class Session(
    val qty : Int?        = null, // Cantidad nullable, default null
    val unit: SessionUnit = SessionUnit.INDEFINIDO // Unidad, default INDEFINIDO
)

/**
 * @brief Enumeración que define las posibles unidades de medida para una sesión de hábito ([Session]).
 * @details Especifica si la sesión se mide en minutos, horas, o si no tiene una unidad definida.
 */
enum class SessionUnit {
    /** @brief Indica que la sesión no tiene una duración o cantidad específica medible (e.g., "Beber agua"). */
    INDEFINIDO,
    /** @brief La sesión se mide en minutos (e.g., "Meditar por 10 minutos"). */
    MINUTOS,
    /** @brief La sesión se mide en horas (e.g., "Estudiar durante 1 hora"). */
    HORAS
    // Se podrían añadir otras unidades si fueran necesarias, como VECES, PAGINAS, etc.
}
