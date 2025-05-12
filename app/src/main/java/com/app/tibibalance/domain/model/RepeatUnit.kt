/**
 * @file    RepeatUnit.kt
 * @ingroup domain_model
 * @brief   Unidades válidas para expresar objetivos de repetición
 *          en el “modo reto”.
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RepeatUnit {
    DIAS,            // nº de días consecutivos
    SESIONES,        // nº de veces que se hace el hábito
    MINUTOS          // nº total de minutos dedicados
}
