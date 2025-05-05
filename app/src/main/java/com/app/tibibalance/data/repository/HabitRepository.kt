package com.app.tibibalance.data.repository

import com.app.tibibalance.domain.model.Habit
import kotlinx.coroutines.flow.Flow

/* data/repository/HabitRepository.kt */
interface HabitRepository {
    /** Stream reactivo; emite inmediatamente valores desde Room. */
    fun observeHabits(): Flow<List<Habit>>

    /** Inserta un nuevo hábito y devuelve el ID generado. */
    suspend fun addHabit(habit: Habit): String

    /** Actualiza campos mutables (nombre, icono, etc.). */
    suspend fun updateHabit(habit: Habit)

    /** Borra el documento local + remoto. */
    suspend fun deleteHabit(id: String)

    /** Marca o desmarca el hábito como realizado hoy. */
    suspend fun setCheckedToday(id: String, checked: Boolean)
}
