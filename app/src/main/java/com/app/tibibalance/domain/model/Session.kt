package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val qty : Int?        = null,
    val unit: SessionUnit = SessionUnit.INDEFINIDO
)

enum class SessionUnit { INDEFINIDO, MINUTOS, HORAS }
