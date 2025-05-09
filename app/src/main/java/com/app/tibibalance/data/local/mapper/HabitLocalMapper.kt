/**
 * @file    HabitLocalMapper.kt // <-- CORREGIDO: Nombre del archivo actualizado
 * @ingroup data_local_mapper   // <-- Grupo más específico para mappers locales
 * @brief   Funciones de extensión para mapeo bidireccional entre [HabitEntity] y [Habit].
 *
 * @details Dado que Room persiste el estado completo del hábito como una cadena JSON
 * dentro de la entidad [HabitEntity], estas funciones de extensión encapsulan
 * la lógica de serialización (dominio -> entidad) y deserialización (entidad -> dominio)
 * usando `kotlinx.serialization.json.Json`. Esto mantiene el código de los
 * repositorios y DAOs más limpio y centrado en sus responsabilidades principales.
 */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitEntity
import com.app.tibibalance.domain.model.Habit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/* ─────────────── HabitEntity (Room) <---> Habit (Dominio) ─────────────── */

/**
 * @brief Convierte la entidad de Room [HabitEntity] a su modelo de dominio [Habit].
 *
 * @details Deserializa la cadena JSON almacenada en la propiedad `json` de la entidad
 * utilizando `Json.decodeFromString`. El serializador específico para [Habit]
 * se infiere automáticamente por Kotlinx Serialization.
 *
 * @receiver La instancia de [HabitEntity] recuperada de la base de datos Room.
 * @return La instancia reconstruida del modelo de dominio [Habit].
 * @throws kotlinx.serialization.SerializationException Si la cadena JSON está mal formada o
 * no corresponde al esquema esperado de [Habit].
 */
fun HabitEntity.toDomain(): Habit =
    Json.decodeFromString<Habit>(json) // El serializador se infiere del tipo genérico <Habit>

/**
 * @brief Convierte un modelo de dominio [Habit] a su entidad [HabitEntity] para persistencia en Room.
 *
 * @details Serializa el objeto [Habit] completo a una cadena JSON usando `Json.encodeToString`.
 * El `id` del hábito de dominio se utiliza directamente como clave primaria para la entidad.
 *
 * @receiver La instancia del modelo de dominio [Habit] que se desea almacenar.
 * @return Una instancia de [HabitEntity] lista para ser insertada o actualizada en la base de datos Room.
 */
fun Habit.toEntity(): HabitEntity =
    HabitEntity(id = this.id, json = Json.encodeToString(this)) // Serializa el objeto Habit completo a JSON
