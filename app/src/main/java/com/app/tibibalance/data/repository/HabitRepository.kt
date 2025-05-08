/* data/repository/HabitRepository.kt */
package com.app.tibibalance.data.repository

import com.app.tibibalance.domain.model.Habit
import kotlinx.coroutines.flow.Flow

/**
 * @file    HabitRepository.kt
 * @ingroup data_repository
 * @brief   Contrato de alto nivel para la gestión de hábitos.
 *
 * Define las operaciones que la capa de presentación puede invocar sin
 * conocer detalles de infraestructura (Room, Firestore, etc.).
 * Implementaciones típicas:
 * - [FirebaseHabitRepository] (Room + Firestore, offline-first)
 */
interface HabitRepository {

    /**
     * @brief   Observa la lista de hábitos del usuario.
     *
     * @details El flujo emite inmediatamente la caché local (Room) y vuelve
     *          a emitir cuando se detectan cambios, ya sea por acciones
     *          locales o por sincronización remota.
     *
     * @return  [Flow] que emite listas de [Habit] en orden indefinido.
     */
    fun observeHabits(): Flow<List<Habit>>

    /**
     * @brief   Inserta un nuevo hábito y lo sincroniza con el backend.
     *
     * @param habit Modelo de dominio a guardar.
     * @return      Identificador asignado (docId Firestore o UUID local).
     */
    suspend fun addHabit(habit: Habit): String

    /**
     * @brief   Actualiza los campos editables de un hábito existente.
     *
     * @param habit Instancia completa con los valores actualizados.
     */
    suspend fun updateHabit(habit: Habit)

    /**
     * @brief   Elimina el hábito tanto local como remotamente.
     *
     * @param id Identificador único del hábito a borrar.
     */
    suspend fun deleteHabit(id: String)

    /**
     * @brief   Marca o desmarca el hábito como realizado en la fecha actual.
     *
     * @param id      Identificador del hábito.
     * @param checked `true` para marcar como hecho; `false` para desmarcar.
     */
    suspend fun setCheckedToday(id: String, checked: Boolean)
}
