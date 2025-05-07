package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Period(
    val qty : Int?        = null,
    val unit: PeriodUnit  = PeriodUnit.INDEFINIDO
)

enum class PeriodUnit  { INDEFINIDO, DIAS, SEMANAS, MESES }