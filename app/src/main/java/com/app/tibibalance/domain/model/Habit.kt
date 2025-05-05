package com.app.tibibalance.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/* data/local/model/Habit.kt */
@Serializable
data class Habit(
    val id          : String = "",
    val name        : String,
    val description : String,
    val session     : Session,
    val repeat      : Repeat,
    val period      : Period,
    val category    : HabitCategory,
    val icon        : String,
    val notifConfig : NotifConfig,
    val createdAt   : Instant = Clock.System.now(),
    val nextTrigger : Instant? = null
)

