/* ui/wizard/HabitFormSaver.kt */
package com.app.tibibalance.ui.wizard

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.app.tibibalance.domain.model.*

val HabitFormSaver: Saver<HabitForm, Any> = mapSaver(
    save = { hf ->
        mapOf(
            "name"           to hf.name,
            "desc"           to hf.desc,
            "category"       to hf.category.name,   // enum → String
            "icon"           to hf.icon,
            "sessionQty"     to hf.sessionQty,
            "sessionUnit"    to hf.sessionUnit.name,
            "repeatPattern"  to hf.repeatPattern.name,
            "periodQty"      to hf.periodQty,
            "periodUnit"     to hf.periodUnit.name,
            "notify"         to hf.notify
        )
    },
    restore = { map ->
        HabitForm(
            name          = map["name"]        as String,
            desc          = map["desc"]        as String,
            category      = HabitCategory.valueOf(map["category"] as String),
            icon          = map["icon"]        as String,
            sessionQty    = map["sessionQty"]  as Int?,
            sessionUnit   = SessionUnit.valueOf(map["sessionUnit"] as String),
            repeatPattern = RepeatPattern.valueOf(map["repeatPattern"] as String),
            periodQty     = map["periodQty"]   as Int?,
            periodUnit    = PeriodUnit.valueOf(map["periodUnit"] as String),
            notify        = map["notify"]      as Boolean
        )
    }
)
