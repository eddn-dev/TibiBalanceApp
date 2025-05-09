/**
 * @file    FirestoreHabitTemplateMapper.kt
 * @ingroup data_remote_mapper // Grupo específico para mappers remotos
 * @brief   Función de extensión para convertir un [DocumentSnapshot] de Firestore a un [HabitTemplate].
 *
 * @details Esta función interpreta los datos de un documento recuperado de la colección
 * Firestore **`habitTemplates`** y los transforma en una instancia del modelo de dominio
 * [HabitTemplate]. Incluye lógica para manejar:
 * - **Validación de campos obligatorios**: Devuelve `null` si el campo esencial `name` falta.
 * - **Valores por defecto**: Asigna valores predeterminados para campos opcionales o enums si no están presentes
 * o no se pueden parsear (e.g., `icon`, `sessionUnit`, `repeatPreset`, `periodUnit`, `notifMode`).
 * - **Compatibilidad retroactiva**: Incluye fallbacks para nombres de campos que pueden haber cambiado
 * (e.g., `notif.daysOfWeek` vs `notif.weekDays`, `notif.advanceMinutes` vs `notif.advanceMin`).
 * - **Parseo seguro de Enums**: Utiliza `runCatching { Enum.valueOf(it) }.getOrNull()` para evitar
 * excepciones si Firestore contiene un valor de enum desconocido, asignando un valor por defecto en su lugar.
 * - **Manejo de tipos**: Convierte tipos de Firestore (como `Long` para días de la semana) a los tipos
 * esperados por el modelo de dominio (`Set<Int>` dentro de [WeekDays]).
 * - **Manejo de errores**: Un bloque `try-catch` general devuelve `null` si ocurre cualquier
 * excepción inesperada durante el proceso de mapeo, tratando el documento como inválido.
 */
package com.app.tibibalance.data.remote.mapper

// import android.util.Log // Opcional: Para loguear errores de mapeo
import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot

/**
 * @brief   Mapea un [DocumentSnapshot] de Firestore a un objeto [HabitTemplate].
 *
 * @receiver El [DocumentSnapshot] que representa un documento de la colección `habitTemplates`.
 * @return   Una instancia de [HabitTemplate] si el mapeo es exitoso y el campo `name` está presente;
 * `null` en caso contrario (documento inválido, falta `name`, o error de parseo).
 */
fun DocumentSnapshot.toHabitTemplate(): HabitTemplate? {
    return try {
        HabitTemplate(
            id          = id, // ID del documento Firestore
            // Campo obligatorio: si 'name' es null, retorna null inmediatamente
            name        = getString("name") ?: return null,
            description = getString("description") ?: "", // Default a cadena vacía si es null

            /* ── Categoría ── */
            // Convierte el String de Firestore a HabitCategory, usando default si es null/inválido
            category    = HabitCategory.fromRaw(getString("category") ?: ""),

            icon        = getString("icon") ?: "FitnessCenter", // Default a "FitnessCenter" si es null

            /* ── Sesión ── */
            // Obtiene Long? de Firestore y lo convierte a Int?
            sessionQty  = getLong("sessionQty")?.toInt(),
            // Obtiene String?, intenta parsearlo a SessionUnit (ignorando mayúsculas), default a INDEFINIDO
            sessionUnit = getString("sessionUnit")
                ?.let { runCatching { SessionUnit.valueOf(it.uppercase()) }.getOrNull() } // Usa uppercase para robustez
                ?: SessionUnit.INDEFINIDO,

            /* ── Repetición ── */
            // Obtiene String?, intenta parsearlo a RepeatPreset, default a INDEFINIDO
            repeatPreset = getString("repeatPreset")
                ?.let { runCatching { RepeatPreset.valueOf(it.uppercase()) }.getOrNull() }
                ?: RepeatPreset.INDEFINIDO,

            /* ── Periodo total ── */
            periodQty   = getLong("periodQty")?.toInt(),
            // Obtiene String?, intenta parsearlo a PeriodUnit, default a INDEFINIDO
            periodUnit  = getString("periodUnit")
                ?.let { runCatching { PeriodUnit.valueOf(it.uppercase()) }.getOrNull() }
                ?: PeriodUnit.INDEFINIDO,

            /* ── Notificación (Objeto NotifConfig anidado) ── */
            notifCfg = NotifConfig(
                // Obtiene Boolean?, default a false si es null
                enabled      = getBoolean("notif.enabled") ?: false,
                // Obtiene String?, intenta parsearlo a NotifMode, default a SILENT
                mode         = getString("notif.mode")
                    ?.let { runCatching { NotifMode.valueOf(it.uppercase()) }.getOrNull() }
                    ?: NotifMode.SILENT,
                message      = getString("notif.message") ?: "", // Default a cadena vacía
                // Obtiene List<*>?, castea seguro a List<String>, default a emptyList
                timesOfDay   = get("notif.timesOfDay") as? List<String> ?: emptyList(),
                // Maneja fallback entre nombres de campo y convierte List<Long?> a WeekDays
                weekDays     = ((get("notif.daysOfWeek") as? List<Long>) // Intenta con nombre antiguo
                    ?: get("notif.weekDays") as? List<Long> // Intenta con nombre nuevo
                        )
                    ?.mapNotNull { it?.toInt() } // Convierte cada Long? a Int?, filtrando nulos
                    ?.toSet() // Convierte la lista resultante a Set<Int>
                    ?.let(::WeekDays) // Crea WeekDays si el Set no es null
                    ?: WeekDays.NONE, // Default si no había lista o estaba vacía/contenía solo nulos
                // Maneja fallback entre nombres de campo para advanceMin
                advanceMin   = (getLong("notif.advanceMin") // Intenta con nombre nuevo
                    ?: getLong("notif.advanceMinutes") // Intenta con nombre antiguo
                    ?: 0L // Default a 0 si ambos son null
                        ).toInt(), // Convierte a Int
                // Obtiene Boolean?, si es null o false, el resultado es false. Si es true, es true.
                // Equivalente a `getBoolean("notif.vibrate") == true`, pero maneja null.
                // NOTA: El código original `!= false` hace que null resulte en true. Se mantiene esa lógica.
                vibrate      = getBoolean("notif.vibrate") != false
            ),

            // Obtiene Boolean?, default a false si es null
            scheduled   = getBoolean("scheduled") ?: false
        )
    } catch (e: Exception) {
        // Captura cualquier excepción durante el get/cast/conversión
        // Log.e("FirestoreMapper", "Error mapeando HabitTemplate ${this.id}", e) // Opcional: Loguear el error específico
        null // Devuelve null indicando que el documento es inválido o hubo un error
    }
}
