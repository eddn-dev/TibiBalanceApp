/* data/remote/mapper/FirestoreHabitTemplateMapper.kt */
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toHabitTemplate(): HabitTemplate? {
    return try {
        HabitTemplate(
            id          = id,
            name        = getString("name") ?: return null,
            description = getString("description") ?: "",

            /* ── Categoría ── */
            category    = HabitCategory.fromRaw(getString("category") ?: ""),

            icon        = getString("icon") ?: "FitnessCenter",

            /* ── Sesión ── */
            sessionQty  = getLong("sessionQty")?.toInt(),
            sessionUnit = getString("sessionUnit")
                ?.let { runCatching { SessionUnit.valueOf(it) }.getOrNull() }
                ?: SessionUnit.INDEFINIDO,

            /* ── Repetición ── */
            repeatPreset = getString("repeatPreset")        // 👈 nombre nuevo
                ?.let { runCatching { RepeatPreset.valueOf(it) }.getOrNull() }
                ?: RepeatPreset.INDEFINIDO,

            /* ── Periodo total ── */
            periodQty   = getLong("periodQty")?.toInt(),
            periodUnit  = getString("periodUnit")
                ?.let { runCatching { PeriodUnit.valueOf(it) }.getOrNull() }
                ?: PeriodUnit.INDEFINIDO,

            /* ── Notificación ── */
            notifCfg = NotifConfig(
                enabled      = getBoolean("notif.enabled") ?: false,
                mode         = getString("notif.mode")
                    ?.let { runCatching { NotifMode.valueOf(it) }.getOrNull() }
                    ?: NotifMode.SILENT,
                message      = getString("notif.message") ?: "",
                timesOfDay   = get("notif.timesOfDay") as? List<String> ?: emptyList(),
                weekDays     = ((get("notif.daysOfWeek") as? List<Long>)
                    ?: get("notif.weekDays") as? List<Long>      // fallback por si existía con otro nombre
                        )
                    ?.map(Long::toInt)
                    ?.toSet()
                    ?.let(::WeekDays)
                    ?: WeekDays.NONE,
                advanceMin   = (getLong("notif.advanceMin")
                    ?: getLong("notif.advanceMinutes")          // fallback nombre viejo
                    ?: 0L
                        ).toInt(),
                vibrate      = getBoolean("notif.vibrate") != false
            ),

            scheduled   = getBoolean("scheduled") ?: false
        )
    } catch (e: Exception) {
        null      // Documento inválido: se ignora
    }
}
