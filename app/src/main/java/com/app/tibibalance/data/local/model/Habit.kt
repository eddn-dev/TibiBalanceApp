package com.app.tibibalance.data.local.model

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

/** Entidad principal que representa un hábito del usuario. */
data class Habit(
    val id          : String = "",             // ID de Firestore
    val name        : String,
    val description : String,
    val session     : Session,
    val repeat      : Repeat,
    val period      : Period,
    val category    : HabitCategory,
    val icon        : String,                  // nombre del Material Icon
    val notifConfig : NotifConfig,
    val createdAt   : Instant = Clock.System.now(),
    val nextTrigger : Instant? = null          // se actualiza localmente
)
