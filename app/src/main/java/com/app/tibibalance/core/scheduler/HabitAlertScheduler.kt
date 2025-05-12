/* core/scheduler/HabitAlertScheduler.kt */
package com.app.tibibalance.core.scheduler

import com.app.tibibalance.domain.model.Habit

/**
 * Encapsula la programación y cancelación de alarmas exactas
 * asociadas a un [Habit].
 */
interface HabitAlertScheduler {
    fun schedule(habit: Habit)
    fun cancel(habitId: String)
}
