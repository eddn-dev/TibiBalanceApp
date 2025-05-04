package com.app.tibibalance.data.local.model

/** Periodo límite de un hábito (o infinito si ambos valores son null). */
data class Period(
    val value: Int? = null,
    val unit : PeriodUnit? = null    // WEEKS | MONTHS | YEARS
)

enum class PeriodUnit { WEEKS, MONTHS, YEARS }
