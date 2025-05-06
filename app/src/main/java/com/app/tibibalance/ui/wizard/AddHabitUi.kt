/* ui/wizard/AddHabitUi.kt */
package com.app.tibibalance.ui.wizard

import com.app.tibibalance.domain.model.HabitForm
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.domain.model.NotifConfig

/** Flujo de pantalla */
sealed interface AddHabitUiState {
    /** Paso 0: plantilla sugerida o “crear en blanco” */
    data class Suggestions(
        /** formulario en curso (vacío o ya modificado) */
        val draft : HabitForm = HabitForm()
    ) : AddHabitUiState

    /** Paso 1: formulario de detalles */
    data class Details(
        val form       : HabitForm,
        val errors     : List<String> = emptyList(), // mensajes de validación
        val canProceed : Boolean = errors.isEmpty()
    ) : AddHabitUiState

    /** Paso 2: configurador de notificaciones */
    data class Notification(
        val form  : HabitForm,
        val cfg   : NotifConfig
    ) : AddHabitUiState

    /** Diálogo emergente para confirmar pérdida de avance */
    data class ConfirmDiscard(
        val pendingTemplate: HabitTemplate,
        val previous       : AddHabitUiState          // ← NUEVO
    ) : AddHabitUiState

    /** Estado terminal tras guardar (spinner / mensaje) */
    object Saving : AddHabitUiState
}

