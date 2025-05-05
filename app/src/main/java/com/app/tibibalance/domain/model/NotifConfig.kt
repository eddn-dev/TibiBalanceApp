package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotifConfig(
    val message  : String,
    val mode     : NotifMode,
    val scheduled: Boolean = true      // true = habilitada
)

enum class NotifMode { SILENT, SOUND, VIBRATE }
