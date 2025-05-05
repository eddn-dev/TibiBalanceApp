package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val value: Int? = null,          // 10, 5…
    val unit : SessionUnit? = null   // MINUTES | HOURS
)

enum class SessionUnit { MINUTES, HOURS }
