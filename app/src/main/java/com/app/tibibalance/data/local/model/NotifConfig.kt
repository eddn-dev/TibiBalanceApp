package com.app.tibibalance.data.local.model

/** Preferencias de la notificación asociada al hábito. */
data class NotifConfig(
    val message  : String,
    val mode     : NotifMode,
    val scheduled: Boolean = true      // true = habilitada
)

enum class NotifMode { SILENT, SOUND, VIBRATE }
