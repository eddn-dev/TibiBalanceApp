/* ui/wizard/AddHabitViewModel.kt */
package com.app.tibibalance.ui.wizard

import androidx.lifecycle.ViewModel
import com.app.tibibalance.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddHabitViewModel @Inject constructor() : ViewModel() {

    /* ---------- UI-State ---------- */
    private val _ui = MutableStateFlow<AddHabitUiState>(
        AddHabitUiState.Suggestions()
    )
    val ui: StateFlow<AddHabitUiState> = _ui.asStateFlow()

    /* ---------- Plantillas ---------- */
    fun pickTemplate(tpl: HabitTemplate) = when (val st = _ui.value) {
        is AddHabitUiState.Suggestions ->
            if (st.draft.isModified()) askConfirmation(tpl) else applyTemplate(tpl)

        is AddHabitUiState.Details,
        is AddHabitUiState.Notification -> askConfirmation(tpl)

        else -> Unit
    }

    private fun askConfirmation(tpl: HabitTemplate) {
        _ui.value = AddHabitUiState.ConfirmDiscard(
            pendingTemplate = tpl,
            previous        = _ui.value
        )
    }

    fun confirmDiscard(accept: Boolean) {
        val confirm = _ui.value as? AddHabitUiState.ConfirmDiscard ?: return
        _ui.value = if (accept) applyTemplate(confirm.pendingTemplate)
        else        confirm.previous
    }

    private fun applyTemplate(tpl: HabitTemplate): AddHabitUiState.Details {
        val form = HabitForm().prefillFromTemplate(tpl)
        val details = AddHabitUiState.Details(
            form   = form,
            errors = validate(form)
        )
        _ui.value = details
        return details
    }

    /* ---------- Paso 0: iniciar formulario en blanco ---------- */
    fun startBlankForm() {
        val draft = (ui.value as? AddHabitUiState.Suggestions)?.draft ?: HabitForm()
        _ui.value = AddHabitUiState.Details(
            form   = draft,
            errors = validate(draft)
        )
    }


    /* ---------- Detalles ---------- */
    fun updateForm(form: HabitForm) {
        _ui.update {
            AddHabitUiState.Details(
                form   = form,
                errors = validate(form)
            )
        }
    }

    fun nextFromDetails() {
        val st = _ui.value as? AddHabitUiState.Details ?: return
        if (st.errors.isNotEmpty()) return            // bloquea avance

        if (st.form.notify) {
            _ui.value = AddHabitUiState.Notification(
                form = st.form,
                cfg  = NotifConfig()
            )
        } else saveAndClose(st.form, NotifConfig())
    }

    /* ---------- Notificaciones ---------- */
    fun updateNotif(cfg: NotifConfig) {
        val st = _ui.value as? AddHabitUiState.Notification ?: return
        _ui.value = st.copy(cfg = cfg)
    }

    fun finish() {
        when (val st = _ui.value) {
            is AddHabitUiState.Notification -> saveAndClose(st.form, st.cfg)
            is AddHabitUiState.Details      -> saveAndClose(st.form, NotifConfig())
            else -> Unit
        }
    }

    /* ---------- Navegación atrás ---------- */
    fun back() = when (val st = _ui.value) {
        is AddHabitUiState.Details      -> _ui.value = AddHabitUiState.Suggestions(st.form)
        is AddHabitUiState.Notification -> _ui.value = AddHabitUiState.Details(
            form   = st.form,
            errors = validate(st.form)
        )
        else -> Unit
    }

    /* ---------- Guardado ---------- */
    private fun saveAndClose(form: HabitForm, cfg: NotifConfig) {
        _ui.value = AddHabitUiState.Saving
        // TODO: suspender → repositorio + notificaciones
        _ui.value = AddHabitUiState.Suggestions()     // reset
    }

    /* ---------- Helpers ---------- */
    /**
     * Sólo el **nombre** (y opcionalmente la categoría) se consideran obligatorios.
     * Los campos de duración, repetición y periodo se vuelven opcionales.
     */
    /* ui/wizard/AddHabitViewModel.kt */
    private fun validate(f: HabitForm): List<String> = buildList {
        if (f.name.isBlank()) add("El nombre es obligatorio")

        // — Duración de la actividad —
        if (f.sessionUnit != SessionUnit.INDEFINIDO &&
            (f.sessionQty == null || f.sessionQty <= 0)
        ) add("Indica la cantidad para la duración de la actividad")

        // — Periodo del hábito —
        if (f.periodUnit != PeriodUnit.INDEFINIDO &&
            (f.periodQty == null || f.periodQty <= 0)
        ) add("Indica la cantidad para el periodo del hábito")
    }


    private fun HabitForm.isModified() =
        name.isNotBlank() || desc.isNotBlank() || notify ||
                sessionUnit   != SessionUnit.INDEFINIDO ||
                repeatPattern != RepeatPattern.INDEFINIDO ||
                periodUnit    != PeriodUnit.INDEFINIDO
}
