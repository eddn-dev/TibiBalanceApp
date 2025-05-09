/**
 * @file    NotifConverters.kt
 * @ingroup data_local_converter // Grupo específico para TypeConverters de Room
 * @brief   Conjunto de [TypeConverter] para serializar/deserializar listas usadas en [com.app.tibibalance.domain.model.NotifConfig].
 *
 * @details Room no puede almacenar directamente tipos de colección como `List<String>` o `List<Int>`.
 * Esta clase proporciona los métodos necesarios, anotados con `@TypeConverter`, para
 * convertir estas listas a y desde una representación `String` simple, utilizando un
 * separador interno (`|`).
 *
 * Se aplica específicamente a los campos `notifTimesOfDay` (Lista de horas "HH:mm") y
 * `notifDaysOfWeek` (Lista de días de la semana como enteros 1-7) dentro de la entidad
 * [com.app.tibibalance.data.local.entity.HabitTemplateEntity].
 */
package com.app.tibibalance.data.local.mapper // El paquete sigue siendo mapper, está bien

import androidx.room.TypeConverter
import com.app.tibibalance.domain.model.Habit // Import no usado directamente, pero relevante por NotifConfig
import kotlinx.datetime.Instant // Import no usado directamente aquí
import kotlinx.serialization.encodeToString // Import no usado directamente aquí
import kotlinx.serialization.json.Json // Import no usado directamente aquí

/** Separador interno utilizado para concatenar los valores de la lista en un String. */
private const val SEP = "|"

/**
 * @brief Clase que contiene los métodos [TypeConverter] para Room.
 * @details Proporciona la lógica para convertir listas de Strings (horas) y listas de Ints (días)
 * a/desde una representación String única para almacenamiento en la base de datos.
 */
class NotifConverters {

    /* ───────────── List<String> (Horas HH:mm) ⇆ String ───────────── */

    /**
     * @brief Serializa una lista de horas (Strings "HH:mm") a una cadena única separada por '|'.
     *
     * @param src Lista de horas en formato `"HH:mm"`. Si es `null` o vacía, devuelve una cadena vacía.
     * @return Cadena concatenada por `|` (e.g., `"08:00|20:30"`), o `""` si la lista es nula o vacía.
     */
    @TypeConverter
    fun fromTimes(src: List<String>?): String =
        src?.joinToString(SEP).orEmpty() // Usa orEmpty() para manejar null de forma segura

    /**
     * @brief Deserializa una cadena separada por '|' a una lista de horas (Strings "HH:mm").
     *
     * @param db La cadena almacenada en la base de datos (e.g., `"08:00|20:30"`).
     * @return Lista de horas (`List<String>`). Devuelve una lista vacía si la cadena de entrada es vacía o nula.
     */
    @TypeConverter
    fun toTimes(db: String?): List<String> = // Acepta String? por seguridad
        db?.takeIf { it.isNotEmpty() }?.split(SEP) ?: emptyList()

    /* ───────────── List<Int> (Días 1-7) ⇆ String ───────────── */

    /**
     * @brief Serializa una lista de días (enteros 1-7) a una cadena única separada por '|'.
     *
     * @param src Lista de días de la semana como enteros (1=Lunes...7=Domingo). Si es `null` o vacía, devuelve `""`.
     * @return Cadena concatenada por `|` (e.g., `"1|3|5"`), o `""` si la lista es nula o vacía.
     */
    @TypeConverter
    fun fromDays(src: List<Int>?): String =
        src?.joinToString(SEP).orEmpty()

    /**
     * @brief Deserializa una cadena separada por '|' a una lista de días (enteros).
     *
     * @param db La cadena almacenada en la base de datos (e.g., `"1|3|5"`).
     * @return Lista de días (`List<Int>`). Devuelve una lista vacía si la cadena de entrada es vacía o nula.
     * @throws NumberFormatException Si alguno de los segmentos separados por '|' no es un entero válido.
     */
    @TypeConverter
    fun toDays(db: String?): List<Int> = // Acepta String? por seguridad
        db?.takeIf { it.isNotEmpty() }?.split(SEP)?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    // Usamos mapNotNull y toIntOrNull para ser más robustos ante datos malformados
}
