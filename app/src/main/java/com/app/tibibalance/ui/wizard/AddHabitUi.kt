package com.app.tibibalance.ui.wizard

import com.app.tibibalance.domain.model.*

/** Flujo de pantallas del wizard de creación de hábitos */
sealed interface AddHabitUiState {
    data class Suggestions(val draft: HabitForm = HabitForm()) : AddHabitUiState
    data class BasicInfo  (val form: HabitForm, val errors: List<String> = emptyList()) : AddHabitUiState
    /* -------- AddHabitUiState.kt ------------------------------- */
    data class Tracking(
        val form : HabitForm,
        val errors : List<String> = emptyList(),
        val draftNotif : NotifConfig? = null        // ⬅ nuevo
    ) : AddHabitUiState
    data class Notification(val form: HabitForm, val cfg: NotifConfig) : AddHabitUiState
    data class ConfirmDiscard(val pendingTemplate: HabitTemplate, val previous: AddHabitUiState) : AddHabitUiState
    object Saving : AddHabitUiState
    data class Saved(val title: String, val message: String) : AddHabitUiState
    data class Error(val msg: String) : AddHabitUiState

}

