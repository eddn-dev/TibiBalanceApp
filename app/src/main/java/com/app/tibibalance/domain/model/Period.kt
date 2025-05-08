/**
 * @file    Period.kt
 * @ingroup domain
 * @brief   Límite de tiempo total aplicado a un hábito.
 *
 * El periodo establece cuántas unidades de tiempo ―días, semanas o meses―
 * dura el seguimiento antes de considerarse completado o caducado.
 */
package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

/**
 * @param qty  Cantidad del periodo; `null` si no se ha definido.
 * @param unit Unidad de medida asociada.
 */
@Serializable
data class Period(
    val qty : Int?       = null,
    val unit: PeriodUnit = PeriodUnit.INDEFINIDO
)

/** Unidades válidas para el campo *period*. */
enum class PeriodUnit { INDEFINIDO, DIAS, SEMANAS, MESES }
