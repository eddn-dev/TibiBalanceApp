package com.app.tibibalance.ui.wizard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.mapper.toHabit
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    /* ---- estado expuesto a la UI ---- */
    private val _ui = MutableStateFlow<AddHabitUiState>(AddHabitUiState.Suggestions())
    val ui: StateFlow<AddHabitUiState> = _ui.asStateFlow()

    /* ─────────── Plantillas ─────────── */
    fun pickTemplate(tpl: HabitTemplate) = when (val st = _ui.value) {
        is AddHabitUiState.Suggestions ->
            if (st.draft.isModified()) askConfirmation(tpl) else applyTemplate(tpl)
        is AddHabitUiState.BasicInfo,
        is AddHabitUiState.Tracking,
        is AddHabitUiState.Notification -> askConfirmation(tpl)
        else -> Unit
    }

    private fun applyTemplate(tpl: HabitTemplate): AddHabitUiState.BasicInfo {
        val form = HabitForm().prefillFromTemplate(tpl)
        return AddHabitUiState.BasicInfo(form, validateBasic(form)).also { _ui.value = it }
    }

    /* ─────────── Paso 0 ─────────── */
    fun startBlankForm() {
        val draft = (ui.value as? AddHabitUiState.Suggestions)?.draft ?: HabitForm()
        _ui.value = AddHabitUiState.BasicInfo(draft, validateBasic(draft))
    }

    /* ─────────── Paso 1 (BasicInfo) ─────────── */
    fun updateBasic(form: HabitForm) =
        _ui.update { AddHabitUiState.BasicInfo(form, validateBasic(form)) }

    fun nextFromBasic() {
        val st = _ui.value as? AddHabitUiState.BasicInfo ?: return
        if (st.errors.isEmpty())
            _ui.value = AddHabitUiState.Tracking(st.form, validateTracking(st.form))
    }

    /* ─────────── Paso 2 (Tracking) ─────────── */
    fun updateTracking(form: HabitForm) =
        _ui.update {
            when (it) {
                is AddHabitUiState.Tracking ->
                    it.copy(form = form, errors = validateTracking(form))
                else -> it
            }
        }

    fun nextFromTracking(onFinish: () -> Unit) {
        val st = _ui.value as? AddHabitUiState.Tracking ?: return
        if (st.errors.isNotEmpty()) return

        if (!st.form.notify || st.form.repeatPreset == RepeatPreset.INDEFINIDO) {
            saveAndClose(st.form, NotifConfig(enabled = false), onFinish)
        } else {
            val cfg = st.draftNotif ?: NotifConfig(enabled = true, timesOfDay = listOf("08:00"))
            _ui.value = AddHabitUiState.Notification(st.form, cfg)
        }
    }


    /* ─────────── Paso 3 (Notification) ─────────── */
    fun updateNotif(cfg: NotifConfig) =
        (_ui.value as? AddHabitUiState.Notification)?.let {
            _ui.value = it.copy(cfg = cfg)
        }

    fun finish(onFinish: () -> Unit) {
        val st = _ui.value as? AddHabitUiState.Notification ?: return
        saveAndClose(st.form, st.cfg, onFinish)
    }


    /* ─────────── Navegación atrás ─────────── */
    fun back() = when (val st = _ui.value) {
        is AddHabitUiState.BasicInfo ->
            _ui.value = AddHabitUiState.Suggestions(st.form)

        is AddHabitUiState.Tracking ->
            _ui.value = AddHabitUiState.BasicInfo(
                form    = st.form,
                errors  = validateBasic(st.form)
            )

        is AddHabitUiState.Notification ->
            _ui.value = AddHabitUiState.Tracking(
                form        = st.form,
                errors      = validateTracking(st.form),
                draftNotif  = st.cfg                       // ← guarda borrador
            )

        else -> Unit
    }

    /* ─────────── Guardado final (mock) ─────────── */
    private fun saveAndClose(form: HabitForm, cfg: NotifConfig, onFinish: () -> Unit) {
        viewModelScope.launch {
            _ui.value = AddHabitUiState.Saving

            try {
                val habit = form.toHabit(cfg)
                withContext(io) { habitRepo.addHabit(habit) }

                _ui.value = AddHabitUiState.Saved(
                    title   = "¡Hábito creado!",
                    message = "Tu nuevo hábito se añadió correctamente."
                )

                delay(1_500)
                onFinish() // ← cierra el wizard

            } catch (e: Exception) {
                Log.e("AddHabitVM", "Guardar hábito", e)
                _ui.value = AddHabitUiState.Notification(form, cfg)
            }
        }
    }



    /* ─────────── Validaciones ─────────── */
    private fun validateBasic(f: HabitForm) = buildList {
        if (f.name.isBlank()) add("El nombre es obligatorio")
    }

    private fun validateTracking(f: HabitForm) = buildList {
        if (f.sessionUnit != SessionUnit.INDEFINIDO &&
            (f.sessionQty == null || f.sessionQty <= 0)
        ) add("Indica la duración")

        if (f.periodUnit != PeriodUnit.INDEFINIDO &&
            (f.periodQty == null || f.periodQty <= 0)
        ) add("Indica el periodo")

        if (f.repeatPreset == RepeatPreset.PERSONALIZADO && f.weekDays.isEmpty())
            add("Selecciona al menos un día")

        if (f.repeatPreset == RepeatPreset.INDEFINIDO && f.notify)
            add("Activa repetición para usar notificaciones")

        if (f.challenge && f.repeatPreset == RepeatPreset.INDEFINIDO)
            add("Modo reto requiere repetir el hábito")
    }

    /* ─────────── Helpers privados ─────────── */

    private fun askConfirmation(tpl: HabitTemplate) {
        _ui.value = AddHabitUiState.ConfirmDiscard(tpl, _ui.value)
    }

    fun confirmDiscard(accept: Boolean) {
        val dialog = _ui.value as? AddHabitUiState.ConfirmDiscard ?: return
        _ui.value = if (accept) applyTemplate(dialog.pendingTemplate) else dialog.previous
    }

    /** ¿El usuario ya modificó algo? */
    private fun HabitForm.isModified() =
        name.isNotBlank() || desc.isNotBlank() || notify || challenge ||
                sessionUnit   != SessionUnit.INDEFINIDO ||
                repeatPreset  != RepeatPreset.INDEFINIDO ||
                periodUnit    != PeriodUnit.INDEFINIDO
}
