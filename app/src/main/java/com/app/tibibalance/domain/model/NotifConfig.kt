package com.app.tibibalance.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class NotifConfig(
    val mode           : NotifMode = NotifMode.SILENT,
    val message        : String    = "",
    val timesOfDay     : List<String> = emptyList(),  // ["08:00", "20:30"]
    val daysOfWeek     : List<Int>    = emptyList(),  // [1,3,5] = LU-MI-VI
    val advanceMinutes : Int       = 0,
    val vibrate        : Boolean   = true
)

enum class NotifMode    { SILENT, SOUND, VIBRATE }
