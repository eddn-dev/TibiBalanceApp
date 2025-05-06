package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Period(
    val value: Int? = null,
    val unit : PeriodUnit? = null    // WEEKS | MONTHS | YEARS
)

enum class PeriodUnit  { INDEFINIDO, DIAS, SEMANAS, MESES }