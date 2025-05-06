package com.app.tibibalance.ui.wizard

import androidx.lifecycle.ViewModel
import com.app.tibibalance.domain.model.HabitTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddHabitViewModel @Inject constructor() : ViewModel() {

    /* Datos del wizard */
    private val _wizard = MutableStateFlow(WizardState())
    val  wizard: StateFlow<WizardState> = _wizard.asStateFlow()

    /* ---------- paso 0 → 1 (plantilla elegida) ---------- */
    fun pickTemplate(t: HabitTemplate) {
        _wizard.update {
            it.copy(step = 1, form = it.form.prefillFromTemplate(t))
        }
    }

    fun updateForm(form: HabitForm)       = _wizard.update { it.copy(form  = form) }
    fun updateNotif(cfg: NotificationCfg) = _wizard.update { it.copy(notif = cfg ) }

    /* ---------- paso 1: guardar y decidir si hay paso 2 ---------- */
    fun nextFromForm(form: HabitForm) {
        if (form.notify) {
            _wizard.update { it.copy(step = 2, form = form) }
        } else {
            finish(form, NotificationCfg())          // salta pantalla 2
        }
    }
    /* ───────── new public intent ───────── */
    fun startBlankForm() = _wizard.update {
        it.copy(step = 1, form = HabitForm())        // notify = false by default
    }


    /* ---------- paso 2: guardar definitivamente ---------- */
    fun finish(form: HabitForm, cfg: NotificationCfg) {
        // TODO: persistir hábito + notificación
        _wizard.value = WizardState()                // reset al inicio
    }

    fun back()   = _wizard.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    fun cancel() { _wizard.value = WizardState() }
}

/* ------ modelos auxiliares ------ */

data class WizardState(
    val step : Int          = 0,          // 0-1-2
    val form : HabitForm    = HabitForm(),
    val notif: NotificationCfg = NotificationCfg()
)

data class HabitForm(
    val name    : String = "",
    val desc    : String = "",
    val freq    : String = "Diario",
    val category: String = "Salud",
    val notify  : Boolean = false
) {
    fun prefillFromTemplate(t: HabitTemplate) = copy(
        name     = t.name,
        desc     = t.description,
        category = t.category,
        notify   = t.notifMode != "SILENT"
    )
}
