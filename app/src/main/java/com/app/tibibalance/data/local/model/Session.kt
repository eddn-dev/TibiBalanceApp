package com.app.tibibalance.data.local.model

/** Duración de una sesión (o infinito si ambos valores son null). */
data class Session(
    val value: Int? = null,          // 10, 5…
    val unit : SessionUnit? = null   // MINUTES | HOURS
)

enum class SessionUnit { MINUTES, HOURS }
