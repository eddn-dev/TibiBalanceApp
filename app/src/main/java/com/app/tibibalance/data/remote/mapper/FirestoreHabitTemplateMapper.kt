/* data/remote/mapper/FirestoreHabitTemplateMapper.kt */
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot

/**
 * @file    FirestoreHabitTemplateMapper.kt
 * @ingroup data_remote
 * @brief   Conversión de [DocumentSnapshot] → [HabitTemplate].
 *
 * Esta función de extensión interpreta el documento de la colección
 * **`habitTemplates`** y lo transforma en el modelo de dominio
 * [HabitTemplate].
 *
 * - Los campos obligatorios (`name`, `category`) se validan; si faltan o
 *   contiene valores inválidos, la función devuelve **`null`** para
 *   indicar un documento inconsistente que debe ignorarse.
 * - Se aplica *fallback* a nombres de campo anteriores para mantener
 *   compatibilidad retroactiva (p.ej. `notif.daysOfWeek` → `notif.weekDays`).
 * - Las enumeraciones (`SessionUnit`, `RepeatPreset`, …) se parsean de
 *   forma segura con `runCatching { … }.getOrNull()`, evitando excepciones
 *   en caso de valores desconocidos.
 */

/**
 * @brief   Mapea un documento Firestore a [HabitTemplate].
 *
 * @receiver Documento Firestore recuperado de la colección `habitTemplates`.
 * @return   Instancia de [HabitTemplate] o `null` si el documento es inválido.
 */
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
