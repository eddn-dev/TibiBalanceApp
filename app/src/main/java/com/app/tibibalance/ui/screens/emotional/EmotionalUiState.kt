// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionalUiState.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.annotation.DrawableRes
import java.time.LocalDate

/**
 * @file    EmotionalUiState.kt
 * @ingroup ui_screens_emotional
 * @brief   Modelos de datos sellados para la pantalla de calendario emocional.
 *
 * @details
 * - [EmotionDayUi] es el DTO de un día (número + posible icono) que la UI dibuja.
 * - [EmotionalUiState] expone los distintos estados de la pantalla:
 *     - `Loading` mientras carga los registros,
 *     - `Empty` si no hay ningún registro de emociones,
 *     - `Loaded(days)` con la lista de [EmotionDayUi],
 *     - `Error(msg)` para cualquier fallo.
 * - [EmotionalEvent] recoge eventos one-shot como el clic en un día para abrir el modal.
 */

/**
 * Información mínima para pintar un día en el calendario:
 * - `day`: número (1–31).
 * - `iconRes`: drawable opcional con la emoción.
 * - `isRegistered`: si ya existe un registro para ese día.
 */
data class EmotionDayUi(
    val day          : Int,
    @DrawableRes val iconRes     : Int?,
    val isRegistered : Boolean
)

/**
 * Estados posibles de la pantalla de calendario emocional.
 */
sealed interface EmotionalUiState {
    /** Mientras se cargan los registros de emociones. */
    object Loading : EmotionalUiState

    /** No se encontró ningún registro en el repositorio. */
    object Empty   : EmotionalUiState

    /**
     * Se cargaron con éxito los datos.
     * @param days Lista completa de días con su estado.
     */
    data class Loaded(val days: List<EmotionDayUi>) : EmotionalUiState

    /**
     * Ocurrió un error durante la carga.
     * @param msg Mensaje descriptivo del error.
     */
    data class Error(val msg: String) : EmotionalUiState
}

/**
 * Eventos one-shot que el ViewModel emite para la UI.
 * En este caso, solo registramos el clic en un día para abrir el modal.
 */
sealed class EmotionalEvent {
    /** Se abrió el modal de registro para la fecha indicada. */
    data class RegisterClicked(val date: LocalDate) : EmotionalEvent()
    data class ErrorOccurred(val message: String)   : EmotionalEvent()
}
