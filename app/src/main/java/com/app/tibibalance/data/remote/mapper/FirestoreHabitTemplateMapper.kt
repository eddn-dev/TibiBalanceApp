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
            /* ① conversión String → enum usando el helper del enum */
            category      = HabitCategory.fromRaw(getString("category") ?: ""),

            icon          = getString("icon") ?: "FitnessCenter",

            // ── Sesión ──
            sessionQty    = getLong("sessionQty")?.toInt(),
            sessionUnit   = getString("sessionUnit")
                ?.let { runCatching { SessionUnit.valueOf(it) }.getOrNull() }
                ?: SessionUnit.INDEFINIDO,

            // ── Repetición ──
            repeatPattern = getString("repeatPattern")
                ?.let { runCatching { RepeatPattern.valueOf(it) }.getOrNull() }
                ?: RepeatPattern.INDEFINIDO,

            // ── Periodo total ──
            periodQty     = getLong("periodQty")?.toInt(),
            periodUnit    = getString("periodUnit")
                ?.let { runCatching { PeriodUnit.valueOf(it) }.getOrNull() }
                ?: PeriodUnit.INDEFINIDO,

            // ── Notificación ──
            notifCfg      = NotifConfig(
                mode           = getString("notif.mode")
                    ?.let { runCatching { NotifMode.valueOf(it) }.getOrNull() }
                    ?: NotifMode.SILENT,
                message        = getString("notif.message") ?: "",
                timesOfDay     = get("notif.timesOfDay") as? List<String> ?: emptyList(),
                daysOfWeek     = (get("notif.daysOfWeek") as? List<Long>)?.map(Long::toInt) ?: emptyList(),
                advanceMinutes = (getLong("notif.advanceMinutes") ?: 0L).toInt(),
                vibrate        = getBoolean("notif.vibrate") != false
            ),

            scheduled     = getBoolean("scheduled") ?: false
        )
    } catch (e: Exception) {
        null   // si falla algún enum devuelve null y se ignora el documento
    }
}
