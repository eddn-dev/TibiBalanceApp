/**
 * @file AddHabitViewModel.kt
 * @ingroup ui_wizard
 * @brief ViewModel para el asistente de creación/edición de hábitos ([AddHabitModal]).
 *
 * @details
 * Este ViewModel, gestionado por Hilt (`@HiltViewModel`), orquesta todo el flujo
 * del asistente de creación y edición de hábitos. Es responsable de:
 * - **Gestionar el Estado de la UI:** Mantiene y expone el estado actual del asistente
 * a través de `_ui` ([MutableStateFlow]) y `ui` ([StateFlow]), utilizando la
 * interfaz sellada [AddHabitUiState] para representar cada paso o estado.
 * - **Manejar la Navegación entre Pasos:** Proporciona funciones para avanzar (`nextFromBasic`,
 * `nextFromTracking`), retroceder (`back`), e iniciar diferentes flujos (desde plantilla
 * o formulario en blanco).
 * - **Procesar la Selección de Plantillas:** La función `pickTemplate` maneja la lógica
 * de aplicar una [HabitTemplate], incluyendo la confirmación si ya existe un
 * formulario modificado (`askConfirmation`, `confirmDiscard`).
 * - **Actualizar el Formulario:** Las funciones `updateBasic`, `updateTracking`, y `updateNotif`
 * reciben los datos del formulario ([HabitForm]) o configuración de notificación ([NotifConfig])
 * desde los Composables de cada paso y actualizan el estado de la UI.
 * - **Validación de Datos:** Incluye funciones de validación (`validateBasic`, `validateTracking`)
 * que se ejecutan al actualizar o intentar avanzar de paso, y cuyos resultados (lista de errores)
 * se reflejan en el [AddHabitUiState] correspondiente.
 * - **Guardar el Hábito:** La función `saveAndClose` convierte el [HabitForm] y [NotifConfig]
 * finales a un objeto [Habit] (usando el mapper `toHabit`) y lo persiste a través del
 * [HabitRepository]. Gestiona los estados de `Saving`, `Saved`, y `Error`.
 * - **Operaciones Asíncronas:** Utiliza `viewModelScope` y un [CoroutineDispatcher] de IO
 * inyectado (`@IoDispatcher`) para las operaciones de guardado en el repositorio.
 *
 * @property ui [StateFlow] que emite el estado actual del asistente ([AddHabitUiState]),
 * observado por [AddHabitModal].
 *
 * @see AddHabitModal Composable que consume este ViewModel y reacciona a sus estados.
 * @see AddHabitUiState Interfaz sellada que define los diferentes estados del asistente.
 * @see HabitRepository Repositorio para la persistencia de hábitos.
 * @see HabitForm Modelo de datos que acumula la información del hábito durante el asistente.
 * @see NotifConfig Modelo de datos para la configuración de notificaciones.
 * @see HabitTemplate Modelo de datos para las plantillas de hábitos.
 * @see IoDispatcher Calificador para el CoroutineDispatcher de IO.
 * @see HiltViewModel Anotación para la inyección de dependencias con Hilt.
 */
package com.app.tibibalance.ui.wizard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.mapper.toHabit // Mapper para convertir HabitForm a Habit
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Para actualizar MutableStateFlow de forma segura
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @brief ViewModel para el asistente de creación y edición de hábitos.
 *
 * @constructor Inyecta [HabitRepository] para la persistencia de datos y un
 * [CoroutineDispatcher] (`@IoDispatcher`) para operaciones de entrada/salida.
 * @param habitRepo Repositorio para interactuar con la capa de datos de hábitos.
 * @param io Dispatcher de corrutinas para ejecutar operaciones de IO en un hilo de background.
 */
@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    /* ---- Estado expuesto a la UI ---- */
    /** @brief Flujo mutable interno que mantiene el estado actual del asistente. Se inicializa en [AddHabitUiState.Suggestions]. */
    private val _ui = MutableStateFlow<AddHabitUiState>(AddHabitUiState.Suggestions())
    /** @brief Flujo de estado inmutable expuesto a la UI ([AddHabitModal]) para observar los cambios de estado. */
    val ui: StateFlow<AddHabitUiState> = _ui.asStateFlow()

    /* ─────────── Lógica de Selección de Plantillas ─────────── */

    /**
     * @brief Maneja la selección de una plantilla de hábito ([HabitTemplate]).
     *
     * Si el asistente está en el estado [AddHabitUiState.Suggestions] y el borrador
     * ([HabitForm.draft]) no ha sido modificado, aplica la plantilla directamente.
     * En cualquier otro caso (o si el borrador está modificado), pide confirmación
     * al usuario antes de aplicar la plantilla ([askConfirmation]).
     *
     * @param tpl La [HabitTemplate] seleccionada por el usuario.
     */
    fun pickTemplate(tpl: HabitTemplate) = when (val st = _ui.value) {
        // Si estamos en la pantalla de sugerencias:
        is AddHabitUiState.Suggestions ->
            if (st.draft.isModified()) { // Si el borrador por defecto ya fue modificado por el usuario
                askConfirmation(tpl) // Pide confirmación antes de sobrescribir
            } else {
                applyTemplate(tpl) // Aplica la plantilla directamente
            }
        // Si estamos en cualquier otro paso del formulario (BasicInfo, Tracking, Notification):
        is AddHabitUiState.BasicInfo,
        is AddHabitUiState.Tracking,
        is AddHabitUiState.Notification -> askConfirmation(tpl) // Siempre pide confirmación
        // En otros estados (Saving, Saved, Error), no hace nada.
        else -> Unit
    }

    /**
     * @brief Aplica una [HabitTemplate] al formulario, creando un nuevo [HabitForm]
     * pre-rellenado y transicionando al estado [AddHabitUiState.BasicInfo].
     *
     * @param tpl La [HabitTemplate] a aplicar.
     * @return El nuevo estado [AddHabitUiState.BasicInfo] con el formulario pre-rellenado y validado.
     */
    private fun applyTemplate(tpl: HabitTemplate): AddHabitUiState.BasicInfo {
        val form = HabitForm().prefillFromTemplate(tpl) // Crea un nuevo formulario y lo pre-rellena.
        // Crea el nuevo estado BasicInfo, valida el formulario y actualiza la UI.
        return AddHabitUiState.BasicInfo(form, validateBasic(form)).also { _ui.value = it }
    }

    /* ─────────── Paso 0: Pantalla de Sugerencias (Suggestions) ─────────── */

    /**
     * @brief Inicia el flujo de creación de un hábito desde un formulario en blanco.
     *
     * Transiciona desde [AddHabitUiState.Suggestions] a [AddHabitUiState.BasicInfo],
     * utilizando el `draft` actual del estado `Suggestions` (o un nuevo `HabitForm` si no existe)
     * y validándolo.
     */
    fun startBlankForm() {
        // Obtiene el borrador actual o crea uno nuevo si el estado no es Suggestions.
        val draft = (ui.value as? AddHabitUiState.Suggestions)?.draft ?: HabitForm()
        // Cambia al estado BasicInfo con el formulario borrador y su validación.
        _ui.value = AddHabitUiState.BasicInfo(draft, validateBasic(draft))
    }

    /* ─────────── Paso 1: Información Básica (BasicInfo) ─────────── */

    /**
     * @brief Actualiza el estado de la UI con el [HabitForm] modificado desde el paso `BasicInfo`.
     *
     * Cada vez que el usuario modifica un campo en [com.app.tibibalance.ui.wizard.step.BasicInfoStep],
     * este método es llamado. Revalida el formulario y actualiza el estado.
     *
     * @param form El [HabitForm] con los últimos cambios del usuario.
     */
    fun updateBasic(form: HabitForm) =
        _ui.update { // Actualiza el StateFlow de forma segura.
            // Crea un nuevo estado BasicInfo con el formulario actualizado y los errores de validación.
            AddHabitUiState.BasicInfo(form, validateBasic(form))
        }

    /**
     * @brief Avanza desde el paso `BasicInfo` al paso `Tracking`.
     *
     * Solo permite avanzar si el estado actual es [AddHabitUiState.BasicInfo] y no hay
     * errores de validación en el formulario actual.
     */
    fun nextFromBasic() {
        val st = _ui.value as? AddHabitUiState.BasicInfo ?: return // Sale si el estado no es BasicInfo.
        if (st.errors.isEmpty()) { // Si no hay errores de validación:
            // Transiciona al estado Tracking, pasando el formulario actual y validándolo para el nuevo paso.
            _ui.value = AddHabitUiState.Tracking(st.form, validateTracking(st.form))
        }
    }

    /* ─────────── Paso 2: Parámetros de Seguimiento (Tracking) ─────────── */

    /**
     * @brief Actualiza el estado de la UI con el [HabitForm] modificado desde el paso `Tracking`.
     *
     * Se invoca cuando el usuario modifica campos en [com.app.tibibalance.ui.wizard.step.TrackingStep].
     * Revalida el formulario para el paso de seguimiento.
     *
     * @param form El [HabitForm] con los últimos cambios.
     */
    fun updateTracking(form: HabitForm) =
        _ui.update { currentState -> // Actualiza el StateFlow.
            when (currentState) {
                // Si el estado actual es Tracking, crea una copia con el nuevo formulario y errores.
                is AddHabitUiState.Tracking ->
                    currentState.copy(form = form, errors = validateTracking(form))
                // Si no, mantiene el estado actual (esto no debería ocurrir en un flujo normal).
                else -> currentState
            }
        }

    /**
     * @brief Avanza desde el paso `Tracking`.
     *
     * Si no hay errores de validación:
     * - Si las notificaciones no están activadas (`form.notify` es `false`) o la repetición
     * es indefinida, guarda el hábito directamente y cierra el asistente.
     * - De lo contrario, transiciona al paso `Notification`.
     *
     * @param onFinish Callback que se invoca para cerrar el asistente si se guarda directamente.
     */
    fun nextFromTracking(onFinish: () -> Unit) {
        val st = _ui.value as? AddHabitUiState.Tracking ?: return // Sale si el estado no es Tracking.
        if (st.errors.isNotEmpty()) return // Sale si hay errores de validación.

        // Si no se requieren notificaciones o la repetición es indefinida:
        if (!st.form.notify || st.form.repeatPreset == RepeatPreset.INDEFINIDO) {
            // Guarda el hábito con notificaciones deshabilitadas y cierra el modal.
            saveAndClose(st.form, NotifConfig(enabled = false), onFinish)
        } else { // Si se requieren notificaciones y hay repetición:
            // Prepara la configuración de notificación (usa el borrador si existe, o crea uno por defecto).
            val cfg = st.draftNotif ?: NotifConfig(enabled = true, timesOfDay = listOf("08:00"))
            // Transiciona al estado Notification.
            _ui.value = AddHabitUiState.Notification(st.form, cfg)
        }
    }


    /* ─────────── Paso 3: Configuración de Notificaciones (Notification) ─────────── */

    /**
     * @brief Actualiza el estado de la UI con la [NotifConfig] modificada desde el paso `Notification`.
     *
     * Se invoca cuando el usuario modifica campos en [com.app.tibibalance.ui.wizard.step.NotificationStep].
     *
     * @param cfg La [NotifConfig] con los últimos cambios.
     */
    fun updateNotif(cfg: NotifConfig) =
        (_ui.value as? AddHabitUiState.Notification)?.let { // Solo actualiza si el estado es Notification.
            _ui.value = it.copy(cfg = cfg) // Crea una copia del estado con la nueva configuración.
        }

    /**
     * @brief Finaliza el asistente desde el paso `Notification`, guardando el hábito.
     *
     * @param onFinish Callback que se invoca para cerrar el asistente después de guardar.
     */
    fun finish(onFinish: () -> Unit) {
        val st = _ui.value as? AddHabitUiState.Notification ?: return // Sale si el estado no es Notification.
        // Guarda el hábito con el formulario y la configuración de notificación actuales.
        saveAndClose(st.form, st.cfg, onFinish)
    }


    /* ─────────── Navegación Hacia Atrás entre Pasos ─────────── */

    /**
     * @brief Retrocede al paso anterior en el asistente.
     *
     * La lógica de transición depende del estado actual:
     * - Desde `BasicInfo` -> `Suggestions` (preservando el `HabitForm` actual como `draft`).
     * - Desde `Tracking` -> `BasicInfo` (revalidando el `HabitForm` para `BasicInfo`).
     * - Desde `Notification` -> `Tracking` (preservando la `NotifConfig` actual como `draftNotif`
     * y revalidando el `HabitForm` para `Tracking`).
     */
    fun back() = when (val st = _ui.value) { // Evalúa el estado actual.
        is AddHabitUiState.BasicInfo -> // Si estamos en BasicInfo:
            // Vuelve a Suggestions, pasando el formulario actual como borrador.
            _ui.value = AddHabitUiState.Suggestions(st.form)

        is AddHabitUiState.Tracking -> // Si estamos en Tracking:
            // Vuelve a BasicInfo, pasando el formulario actual y validándolo para ese paso.
            _ui.value = AddHabitUiState.BasicInfo(
                form    = st.form,
                errors  = validateBasic(st.form)
            )

        is AddHabitUiState.Notification -> // Si estamos en Notification:
            // Vuelve a Tracking, pasando el formulario, validándolo, y guardando la config. de notificación actual como borrador.
            _ui.value = AddHabitUiState.Tracking(
                form        = st.form,
                errors      = validateTracking(st.form),
                draftNotif  = st.cfg // Guarda la configuración de notificación actual como borrador.
            )

        // En otros estados (Suggestions, Saving, etc.), no hace nada o la navegación atrás es manejada por el modal.
        else -> Unit
    }

    /* ─────────── Guardado Final del Hábito ─────────── */

    /**
     * @brief Guarda el hábito configurado en el repositorio y cierra el asistente.
     *
     * Transiciona al estado `Saving`, luego intenta guardar el hábito.
     * Si tiene éxito, transiciona a `Saved`, espera un breve momento y llama a `onFinish`.
     * Si falla, registra el error y vuelve al estado `Notification` (o el último estado de edición)
     * para que el usuario pueda reintentar o corregir.
     *
     * @param form El [HabitForm] finalizado.
     * @param cfg La [NotifConfig] finalizada.
     * @param onFinish Callback para cerrar el asistente.
     */
    private fun saveAndClose(form: HabitForm, cfg: NotifConfig, onFinish: () -> Unit) {
        viewModelScope.launch { // Lanza la corrutina en el scope del ViewModel.
            _ui.value = AddHabitUiState.Saving // Transiciona a estado de guardado.

            try {
                val habit = form.toHabit(cfg) // Convierte el formulario y config a un objeto Habit.
                // Ejecuta la operación de repositorio en el dispatcher de IO.
                withContext(io) { habitRepo.addHabit(habit) }

                // Transiciona a estado de éxito.
                _ui.value = AddHabitUiState.Saved(
                    title   = "¡Hábito creado!",
                    message = "Tu nuevo hábito se añadió correctamente."
                )

                delay(1_500) // Espera 1.5 segundos para que el usuario vea el mensaje de éxito.
                onFinish() // Llama al callback para cerrar el wizard.

            } catch (e: Exception) { // Si ocurre una excepción al guardar:
                Log.e("AddHabitVM", "Error al guardar hábito", e) // Registra el error.
                // Vuelve al estado de notificación (o el paso anterior si esto se llamara desde Tracking)
                // permitiendo al usuario reintentar. Se podría considerar un estado de Error más persistente aquí.
                _ui.value = AddHabitUiState.Notification(form, cfg) // O AddHabitUiState.Error(e.message)
            }
        }
    }


    /* ─────────── Funciones de Validación ─────────── */

    /**
     * @brief Valida los campos del paso de "Información Básica".
     * @param f El [HabitForm] a validar.
     * @return Una lista de [String] con los mensajes de error. Vacía si no hay errores.
     */
    private fun validateBasic(f: HabitForm) = buildList {
        if (f.name.isBlank()) add("El nombre es obligatorio")
        // Se podrían añadir más validaciones aquí (e.g., longitud máxima del nombre).
    }

    /**
     * @brief Valida los campos del paso de "Parámetros de Seguimiento".
     * @param f El [HabitForm] a validar.
     * @return Una lista de [String] con los mensajes de error. Vacía si no hay errores.
     */
    private fun validateTracking(f: HabitForm) = buildList {
        // Valida duración de la sesión si la unidad no es indefinida.
        if (f.sessionUnit != SessionUnit.INDEFINIDO &&
            (f.sessionQty == null || f.sessionQty <= 0)
        ) add("Indica la duración de la actividad")

        // Valida periodo total si la unidad no es indefinida.
        if (f.periodUnit != PeriodUnit.INDEFINIDO &&
            (f.periodQty == null || f.periodQty <= 0)
        ) add("Indica el periodo total del hábito")

        // Valida días de la semana si la repetición es personalizada.
        if (f.repeatPreset == RepeatPreset.PERSONALIZADO && f.weekDays.isEmpty())
            add("Selecciona al menos un día para la repetición personalizada")

        // Valida que haya repetición si las notificaciones están activadas.
        if (f.repeatPreset == RepeatPreset.INDEFINIDO && f.notify)
            add("Activa la repetición del hábito para poder usar notificaciones")

        // Valida que haya repetición si el modo reto está activado.
        if (f.challenge && f.repeatPreset == RepeatPreset.INDEFINIDO)
            add("El modo reto requiere que se defina la frecuencia de repetición del hábito")
        // Podría añadirse validación para el periodo en modo reto también.
    }

    /* ─────────── Funciones Helper Privadas ─────────── */

    /**
     * @brief Transiciona al estado [AddHabitUiState.ConfirmDiscard] para pedir confirmación al usuario.
     *
     * Se usa cuando el usuario intenta aplicar una plantilla y ya tiene un formulario modificado.
     * @param tpl La [HabitTemplate] que se está intentando aplicar.
     */
    private fun askConfirmation(tpl: HabitTemplate) {
        // Cambia al estado ConfirmDiscard, pasando la plantilla pendiente y el estado UI actual.
        _ui.value = AddHabitUiState.ConfirmDiscard(tpl, _ui.value)
    }

    /**
     * @brief Procesa la respuesta del usuario al diálogo de confirmación de descarte.
     *
     * Si `accept` es `true`, aplica la plantilla pendiente.
     * Si `accept` es `false`, vuelve al estado anterior al diálogo.
     *
     * @param accept `true` si el usuario confirma descartar los cambios y aplicar la plantilla,
     * `false` si cancela.
     */
    fun confirmDiscard(accept: Boolean) {
        // Obtiene el estado actual, asegurándose de que sea ConfirmDiscard.
        val dialogState = _ui.value as? AddHabitUiState.ConfirmDiscard ?: return
        // Si el usuario aceptó, aplica la plantilla. Si no, vuelve al estado previo.
        _ui.value = if (accept) applyTemplate(dialogState.pendingTemplate) else dialogState.previous
    }

    /**
     * @brief Función de extensión para [HabitForm] que determina si el usuario ha modificado
     * alguno de sus campos principales.
     *
     * Se usa para decidir si se debe pedir confirmación antes de sobrescribir el formulario
     * con una plantilla.
     * @receiver El [HabitForm] a verificar.
     * @return `true` si algún campo relevante ha sido modificado desde su estado por defecto, `false` en caso contrario.
     */
    private fun HabitForm.isModified() =
        name.isNotBlank() || desc.isNotBlank() || notify || challenge ||
                sessionUnit   != SessionUnit.INDEFINIDO || // Si la unidad de sesión ya no es indefinida
                repeatPreset  != RepeatPreset.INDEFINIDO || // Si el preset de repetición ya no es indefinido
                periodUnit    != PeriodUnit.INDEFINIDO    // Si la unidad de periodo ya no es indefinida
    // Considerar también weekDays.isNotEmpty() si RepeatPreset.INDEFINIDO es el único que tiene weekDays vacío por defecto.
}
