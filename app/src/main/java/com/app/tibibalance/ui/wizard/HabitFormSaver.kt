/* ui/wizard/HabitFormSaver.kt */
package com.app.tibibalance.ui.wizard

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.app.tibibalance.domain.model.*

val HabitFormSaver: Saver<HabitForm, Any> = mapSaver(

    /* -------- SERIALIZAR -------- */
    save = { hf ->
        buildMap<String, Any?> {
            put("name",         hf.name)
            put("desc",         hf.desc)
            put("category",     hf.category.name)
            put("icon",         hf.icon)

            put("sessionQty",   hf.sessionQty)
            put("sessionUnit",  hf.sessionUnit.name)

            put("repeatPreset", hf.repeatPreset.name)
            if (hf.weekDays.isNotEmpty())
                put("weekDays", hf.weekDays.toIntArray())

            put("periodQty",    hf.periodQty)
            put("periodUnit",   hf.periodUnit.name)

            put("notify",       hf.notify)
            put("challenge",    hf.challenge)
        }
    },

    /* -------- DES-SERIALIZAR -------- */
    restore = { map ->
        HabitForm(
            name         = map["name"]  as? String  ?: "",
            desc         = map["desc"]  as? String  ?: "",
            category     = map["category"]?.let { HabitCategory.valueOf(it as String) }
                ?: HabitCategory.SALUD,
            icon         = map["icon"]  as? String  ?: "FitnessCenter",

            sessionQty   = map["sessionQty"] as? Int,
            sessionUnit  = map["sessionUnit"]
                ?.let { SessionUnit.valueOf(it as String) }
                ?: SessionUnit.INDEFINIDO,

            repeatPreset = map["repeatPreset"]
                ?.let { RepeatPreset.valueOf(it as String) }
                ?: RepeatPreset.INDEFINIDO,
            weekDays     = (map["weekDays"] as? IntArray)?.toSet() ?: emptySet(),

            periodQty    = map["periodQty"] as? Int,
            periodUnit   = map["periodUnit"]
                ?.let { PeriodUnit.valueOf(it as String) }
                ?: PeriodUnit.INDEFINIDO,

            notify       = map["notify"]   as? Boolean ?: false,
            challenge    = map["challenge"]as? Boolean ?: false
        )
    }
)
