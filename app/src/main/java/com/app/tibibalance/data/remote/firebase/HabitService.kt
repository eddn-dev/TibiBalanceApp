/**
 * @file    HabitService.kt
 * @ingroup domain_service
 * @brief   Fachada de dominio que encapsula “guardar y programar”.
 *
 * @details
 *  - Persiste el hábito con [HabitRepository].
 *  - Si `nextTrigger` no es `null`, llama a [HabitAlertScheduler] para que
 *    registre la alarma exacta en el sistema.
 */
package com.app.tibibalance.domain.service

import com.app.tibibalance.core.scheduler.HabitAlertScheduler
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.domain.model.Habit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitService @Inject constructor(
    private val repo: HabitRepository,
    private val scheduler: HabitAlertScheduler
) {

    /** Guarda el hábito y, si procede, agenda la alarma. */
    /** Guarda y, si procede, agenda la alarma. */
    suspend fun add(habit: Habit) {
        val newId = repo.addHabit(habit)          // ← id que asigna Firestore
        val stored = habit.copy(id = newId)       // ← objeto con id real
        stored.nextTrigger?.let { scheduler.schedule(stored) }
    }

    /** Edición de hábito (re-programa si cambió el trigger). */
    suspend fun update(habit: Habit) {
        repo.updateHabit(habit)
        scheduler.cancel(habit.id)
        habit.nextTrigger?.let { scheduler.schedule(habit) }
    }
}
