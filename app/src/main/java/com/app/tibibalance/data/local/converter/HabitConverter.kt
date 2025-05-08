/* data/local/converter/HabitConverter.kt */
package com.app.tibibalance.data.local.converter

import androidx.room.TypeConverter
import com.app.tibibalance.domain.model.Habit
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @file    HabitConverter.kt
 * @ingroup data_local
 * @brief   Conjunto de *TypeConverter* para entidades relacionadas con **Habit**.
 *
 * Room no puede almacenar directamente tipos complejos como [Habit] ni tipos de
 * fecha-hora de <kbd>kotlinx.datetime</kbd>.  Esta clase expone los métodos
 * necesarios para serializar/deserializar dichos objetos hacia/desde tipos
 * primitivos que la base de datos sí soporta.
 */
class HabitConverter {

    /* ─────────────── Habit ⇆ String (JSON) ─────────────── */

    /**
     * @brief Serializa un [Habit] a JSON.
     *
     * @param habit Instancia a convertir.
     * @return Cadena JSON que representa al hábito.
     */
    @TypeConverter
    fun toJson(habit: Habit): String =
        Json.encodeToString(habit)

    /**
     * @brief Deserializa un JSON a objeto [Habit].
     *
     * @param json Cadena en formato JSON.
     * @return Objeto [Habit] reconstruido.
     */
    @TypeConverter
    fun fromJson(json: String): Habit =
        Json.decodeFromString(json)

    /* ─────────────── Instant ⇆ Long (epoch) ─────────────── */

    /**
     * @brief Convierte un [Instant] a milisegundos desde época.
     *
     * @param i Instante a convertir.
     * @return Valor epoch en milisegundos, o `null` si *i* es `null`.
     */
    @TypeConverter
    fun fromInstant(i: Instant?): Long? =
        i?.toEpochMilliseconds()

    /**
     * @brief Convierte milisegundos epoch a [Instant].
     *
     * @param epoch Valor epoch en milisegundos.
     * @return Instante equivalente, o `null` si *epoch* es `null`.
     */
    @TypeConverter
    fun toInstant(epoch: Long?): Instant? =
        epoch?.let { Instant.fromEpochMilliseconds(it) }

    /* ─────────────── LocalDate ⇆ String (ISO-8601) ─────────────── */

    /**
     * @brief Serializa un [kotlinx.datetime.LocalDate] a texto ISO-8601.
     *
     * @param d Fecha a convertir.
     * @return Cadena `"YYYY-MM-DD"` o `null`.
     */
    @TypeConverter
    fun fromKxLocalDate(d: kotlinx.datetime.LocalDate?): String? =
        d?.toString()   // «2025-05-07»

    /**
     * @brief Deserializa texto ISO-8601 a [kotlinx.datetime.LocalDate].
     *
     * @param iso Cadena `"YYYY-MM-DD"`.
     * @return Objeto *LocalDate* o `null`.
     */
    @TypeConverter
    fun toKxLocalDate(iso: String?): kotlinx.datetime.LocalDate? =
        iso?.let { kotlinx.datetime.LocalDate.parse(it) }
}
