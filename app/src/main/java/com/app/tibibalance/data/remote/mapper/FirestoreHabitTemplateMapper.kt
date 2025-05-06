// data/remote/mapper/FirestoreHabitTemplateMapper.kt
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.HabitTemplate
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toHabitTemplate(): HabitTemplate =
    HabitTemplate(
        id          = id,
        name        = getString("name")          ?: "",
        description = getString("description")   ?: "",
        category    = getString("category")      ?: "",
        icon        = getString("icon")          ?: "Favorite",
        message     = getString("notifConfig.message") ?: "",
        repeatEvery = getLong("repeat.every")?.toInt() ?: 1,
        repeatType  = getString("repeat.type")   ?: "DAILY",
        notifMode   = getString("notifConfig.mode") ?: "SOUND",
        scheduled   = getBoolean("notifConfig.scheduled") ?: true
    )
