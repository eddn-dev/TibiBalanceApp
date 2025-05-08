/* data/local/mapper/HabitMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.domain.model.Habit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @file    HabitMapper.kt
 * @ingroup data_local
 * @brief   Funciones de mapeo entre [HabitEntity] y [Habit].
 *
 * Room persiste los hábitos como JSON bruto en la columna **json** de
 * [HabitEntity].  Estas extensiones encapsulan la lógica de serialización /
 * deserialización para mantener el código de llamada más limpio.
 */

/* ─────────────── HabitEntity ⇆ Habit (dominio) ─────────────── */

/**
 * @brief Convierte un [HabitEntity] a su equivalente de dominio [Habit].
 *
 * El *serializer* se infiere automáticamente a partir del tipo genérico.
 *
 * @receiver HabitEntity que contiene el JSON serializado.
 * @return   Instancia de [Habit] reconstruida.
 */
fun HabitEntity.toDomain(): Habit =
    Json.decodeFromString<Habit>(json)  // serializer se infiere

/**
 * @brief Convierte un modelo de dominio [Habit] a [HabitEntity] para persistencia.
 *
 * @receiver Objeto [Habit] que se quiere almacenar.
 * @return   [HabitEntity] con el mismo `id` y su representación JSON.
 */
fun Habit.toEntity(): HabitEntity =
    HabitEntity(id, Json.encodeToString(this))  // idem
