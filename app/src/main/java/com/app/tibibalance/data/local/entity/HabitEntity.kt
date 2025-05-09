/**
 * @file    HabitEntity.kt
 * @ingroup data_local
 * @brief   Entidad Room que persiste la información de un hábito en la base de datos local.
 *
 * @details Esta entidad se utiliza como modelo para la tabla `habits` en la base de datos Room.
 * Almacena el identificador único del hábito como clave primaria y el estado completo
 * del hábito (serializado como una cadena JSON) en la columna `json`.
 *
 * Este enfoque de serializar el objeto de dominio [com.app.tibibalance.domain.model.Habit]
 * permite flexibilidad en el esquema del hábito sin requerir migraciones complejas de Room
 * para cada cambio en el modelo de dominio. Además, al usar el mismo `id` que el documento
 * correspondiente en Firestore, se facilita la sincronización _offline-first_
 * mediante la estrategia **Last-Write-Wins (LWW)**.
 */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @brief Representa un registro en la tabla **habits** de la base de datos local.
 *
 * @property id Clave primaria de la tabla. Corresponde al identificador único del hábito,
 * generalmente asignado por Firestore al crear el documento remoto o generado localmente
 * si se crea offline primero.
 * @property json Cadena JSON que contiene la representación serializada del objeto de dominio
 * [com.app.tibibalance.domain.model.Habit], permitiendo almacenar toda su información
 * en una única columna de texto.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,  /**< Identificador único del hábito (PK). */
    val json: String             /**< Estado completo del hábito serializado en formato JSON. */
)
