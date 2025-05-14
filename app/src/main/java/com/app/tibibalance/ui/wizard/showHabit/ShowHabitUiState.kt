/* ui/wizard/showHabit/ShowHabitUiState.kt */
package com.app.tibibalance.ui.wizard.showHabit

import com.app.tibibalance.domain.model.Habit

/** Estados del wizard de visualización / edición */
sealed interface ShowHabitUiState {
    data object Loading               : ShowHabitUiState
    data class Info(val habit: Habit) : ShowHabitUiState
    data class Error(val msg: String) : ShowHabitUiState
}
