/* data/local/mapper/NotifConverters.kt */
package com.app.tibibalance.data.local.mapper

import androidx.room.TypeConverter

/** Separador interno que concatena los valores de la lista. */
private const val SEP = "|"

/**
 * @file    NotifConverters.kt
 * @ingroup data_local
 * @brief   *TypeConverter* para listas usadas en [NotifConfig].
 *
 * Room no soporta almacenar directamente colecciones; este conversor
 * serializa y deserializa:
 * - `List<String>` de horas (`"HH:mm"`) → `String` `"08:00|20:30"`.
 * - `List<Int>` de días de la semana → `String` `"1|3|5"`.
 *
 * Se usa en la entidad [HabitTemplateEntity] para los campos
 * `notifTimesOfDay` y `notifDaysOfWeek`.
 */
class NotifConverters {

    /* ───────────── List<String> ⇆ String ───────────── */

    /**
     * @brief Serializa una lista de horas a una cadena unificada.
     *
     * @param src Lista de horas (`"HH:mm"`); `null` o vacía se convierte en `""`.
     * @return Cadena concatenada por `|`, p.ej. `"08:00|20:30"`.
     */
    @TypeConverter
    fun fromTimes(src: List<String>?): String =
        src?.joinToString(SEP).orEmpty()

    /**
     * @brief Deserializa una cadena de horas a una lista.
     *
     * @param db Cadena almacenada en BD.
     * @return Lista de horas; vacía si la entrada está vacía.
     */
    @TypeConverter
    fun toTimes(db: String): List<String> =
        db.takeIf { it.isNotEmpty() }?.split(SEP) ?: emptyList()

    /* ───────────── List<Int> ⇆ String ───────────── */

    /**
     * @brief Serializa una lista de días a cadena.
     *
     * @param src Lista de días (1‥7); `null` → `""`.
     * @return Cadena `"1|3|5"` representando los días.
     */
    @TypeConverter
    fun fromDays(src: List<Int>?): String =
        src?.joinToString(SEP).orEmpty()

    /**
     * @brief Deserializa la cadena de días a lista de enteros.
     *
     * @param db Cadena de la base de datos.
     * @return Lista de enteros; vacía si la entrada está vacía.
     */
    @TypeConverter
    fun toDays(db: String): List<Int> =
        db.takeIf { it.isNotEmpty() }?.split(SEP)?.map { it.toInt() } ?: emptyList()
}
