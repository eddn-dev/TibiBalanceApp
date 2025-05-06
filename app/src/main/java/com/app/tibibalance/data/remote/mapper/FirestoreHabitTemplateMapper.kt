/* data/remote/mapper/FirestoreHabitTemplateMapper.kt */
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toHabitTemplate(): HabitTemplate? {
    return try {
        HabitTemplate(
            id            = id,
            name          = getString("name") ?: return null,
            description   = getString("description") ?: "",
            category      = getString("category") ?: "Salud",
            icon          = getString("icon") ?: "ic_default_habit",

            sessionQty    = getLong("sessionQty")?.toInt(),
            sessionUnit   = getString("sessionUnit")?.let { SessionUnit.valueOf(it) } ?: SessionUnit.INDEFINIDO,

            repeatPattern = getString("repeatPattern")?.let { RepeatPattern.valueOf(it) } ?: RepeatPattern.INDEFINIDO,

            periodQty     = getLong("periodQty")?.toInt(),
            periodUnit    = getString("periodUnit")?.let { PeriodUnit.valueOf(it) } ?: PeriodUnit.INDEFINIDO,

            notifCfg      = NotifConfig(
                mode           = getString("notif.mode")?.let { NotifMode.valueOf(it) } ?: NotifMode.SILENT,
                message        = getString("notif.message") ?: "",
                timesOfDay     = get("notif.timesOfDay") as? List<String> ?: emptyList(),
                daysOfWeek     = (get("notif.daysOfWeek") as? List<Long>)?.map(Long::toInt) ?: emptyList(),
                advanceMinutes = (getLong("notif.advanceMinutes") ?: 0L).toInt(),
                vibrate        = getBoolean("notif.vibrate") ?: true
            ),

            scheduled     = getBoolean("scheduled") ?: false
        )
    } catch (e: Exception) {
        null                // devuelve null si algún enum no coincide
    }
}
