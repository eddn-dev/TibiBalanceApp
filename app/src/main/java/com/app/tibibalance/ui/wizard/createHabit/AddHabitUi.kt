/**
 * @file AddHabitUiState.kt
 * @ingroup ui_wizard
 * @brief Define los diferentes estados de la interfaz de usuario para el asistente de creación/edición de hábitos.
 *
 * @details
 * Esta interfaz sellada (`sealed interface`) encapsula todos los posibles estados por los que
 * puede pasar la UI del asistente de hábitos (`AddHabitModal`) mientras el usuario
 * interactúa con él. Cada estado representa una pantalla o un momento específico
 * dentro del flujo del asistente, gestionado por [AddHabitViewModel].
 *
 * Los estados cubren desde la sugerencia inicial de plantillas, pasando por la
 * entrada de información básica, configuración de seguimiento y notificaciones,
 * hasta estados intermedios como guardado, confirmaciones, y estados finales
 * como éxito o error.
 *
 * @see AddHabitModal Composable principal del asistente que observa estos estados.
 * @see AddHabitViewModel ViewModel que gestiona la lógica y emite estos estados.
 * @see HabitForm Modelo de datos para el formulario del hábito.
 * @see NotifConfig Modelo de datos para la configuración de notificaciones.
 * @see HabitTemplate Modelo de datos para las plantillas de hábitos.
 */
package com.app.tibibalance.ui.wizard.createHabit

import com.app.tibibalance.domain.model.*

/**
 * @brief Representa los diferentes estados/pantallas del flujo del asistente de creación/edición de hábitos.
 *
 * Cada implementación de esta interfaz corresponde a un paso o estado específico
 * dentro del [AddHabitModal].
 */
sealed interface AddHabitUiState {
    /**
     * @brief Estado inicial donde se muestran sugerencias de hábitos ([HabitTemplate]) al usuario.
     *
     * El usuario puede seleccionar una plantilla o proceder a crear un hábito desde cero.
     * @property draft Un [HabitForm] vacío o parcialmente rellenado que se puede usar si el usuario
     * decide no usar una plantilla y crear un hábito desde cero. Por defecto, es un [HabitForm] nuevo.
     */
    data class Suggestions(val draft: HabitForm = HabitForm()) : AddHabitUiState

    /**
     * @brief Estado para la entrada de información básica del hábito (nombre, descripción, icono, categoría).
     *
     * @property form El [HabitForm] actual que contiene los datos ingresados por el usuario para este paso.
     * @property errors Lista de [String] que contiene mensajes de error de validación
     * para los campos de este paso. Vacía si no hay errores.
     */
    data class BasicInfo(
        val form: HabitForm,
        val errors: List<BasicError> = emptyList(),
        val nameTouched: Boolean = false      // ⬅ NUEVO
    ) : AddHabitUiState

    /**
     * @brief Estado para la configuración de los parámetros de seguimiento del hábito
     * (duración de sesión, frecuencia de repetición, periodo total, modo reto, etc.).
     *
     * @property form El [HabitForm] actual que contiene los datos ingresados por el usuario.
     * @property errors Lista de [String] con mensajes de error de validación para este paso.
     * @property draftNotif Configuración de notificación ([NotifConfig]) opcional que se puede
     * estar arrastrando o pre-configurando desde una plantilla o estado anterior,
     * antes de llegar explícitamente al paso de notificación.
     * Puede ser `null` si no hay una configuración de notificación preexistente.
     */
    /* -------- AddHabitUiState.kt ------------------------------- */
    data class Tracking(
        val form : HabitForm,
        val errors : List<String> = emptyList(),
        val draftNotif : NotifConfig? = null        // ⬅ Borrador de configuración de notificación
    ) : AddHabitUiState

    /**
     * @brief Estado para la configuración de las notificaciones del hábito.
     *
     * @property form El [HabitForm] completo con los datos de los pasos anteriores.
     * @property cfg La [NotifConfig] actual para la configuración de las notificaciones.
     */
    data class Notification(val form: HabitForm, val cfg: NotifConfig) : AddHabitUiState

    /**
     * @brief Estado intermedio para confirmar si el usuario desea descartar los cambios
     * actuales en el formulario al seleccionar una nueva plantilla de hábito.
     *
     * @property pendingTemplate La [HabitTemplate] que el usuario ha intentado seleccionar y que
     * podría sobrescribir el formulario actual.
     * @property previous El estado [AddHabitUiState] en el que se encontraba el usuario
     * (probablemente `BasicInfo` o `Tracking`) antes de intentar seleccionar la plantilla.
     * Se usa para poder volver a ese estado si el usuario cancela el descarte.
     */
    data class ConfirmDiscard(val pendingTemplate: HabitTemplate, val previous: AddHabitUiState) : AddHabitUiState

    /**
     * @brief Estado que indica que el hábito se está guardando.
     *
     * La UI típicamente mostrará un indicador de progreso.
     */
    object Saving : AddHabitUiState

    /**
     * @brief Estado que indica que el hábito se ha guardado exitosamente.
     *
     * @property title Título del mensaje de éxito (e.g., "¡Hábito Guardado!").
     * @property message Mensaje detallado del éxito.
     */
    data class Saved(val title: String, val message: String) : AddHabitUiState

    /**
     * @brief Estado que indica que ha ocurrido un error durante el proceso del asistente
     * (e.g., error al guardar, error de validación no recuperable).
     *
     * @property msg Mensaje descriptivo del error.
     */
    data class Error(val msg: String) : AddHabitUiState
}

sealed interface BasicError { data object NameRequired : BasicError }
