package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class WeekDays(val days: Set<Int>) {
    init { require(days.all { it in 1..7 }) }
    companion object { val NONE = WeekDays(emptySet()) }
}