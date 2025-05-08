/* data/local/entity/HabitEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @file HabitEntity.kt
 * @ingroup data_local
 * @brief Entidad Room que persiste la información de un hábito en la base de datos local.
 *
 * Esta entidad se utiliza como modelo de tabla para Room.
 * Contiene el identificador único del hábito y una cadena JSON que serializa
 * el modelo de dominio correspondiente (`Habit`).
 * Al mantener el mismo `id` que en Firestore, se simplifica la estrategia de
 * sincronización _offline-first_ mediante **Last-Write-Wins (LWW)**.
 */

/**
 * @brief Representa un registro en la tabla **habits**.
 *
 * @property id   Identificador único del hábito (UUID generado al crear el documento en Firestore).
 * @property json Cadena JSON que serializa el estado completo del hábito para su reproducción local.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,  /**< Clave primaria de la tabla. */
    val json: String             /**< Cuerpo JSON con los datos del hábito. */
)
