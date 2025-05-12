/* ChallengeConfig.kt */
package com.app.tibibalance.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * @brief  Configuración de “modo reto”.
 * @param targetCount  total de repeticiones requeridas
 * @param targetUnit   unidad (DÍAS, SESIONES, MINUTOS…)
 * @param deadline     fecha límite (opcional) para completar el reto
 */
@Serializable
data class ChallengeConfig(
    val targetCount : Int,
    val targetUnit  : RepeatUnit,
    val deadline    : Instant? = null
)
