/**
 * @file    Session.kt
 * @ingroup domain
 * @brief   Duración o cantidad asociada a la sesión de un hábito.
 *
 * @property qty  Valor numérico de la sesión; `null` indica que no se ha definido.
 * @property unit Unidad de medida empleada para la cantidad.
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val qty : Int?        = null,
    val unit: SessionUnit = SessionUnit.INDEFINIDO
)

/** Unidades admitidas para la duración de la sesión. */
enum class SessionUnit { INDEFINIDO, MINUTOS, HORAS }
