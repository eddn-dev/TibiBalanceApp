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

    /* ---------- state ---------- */
    private val _wizard = MutableStateFlow(WizardState())
    val     wizard: StateFlow<WizardState> = _wizard.asStateFlow()

    /* ---------- paso 0 → 1: el usuario elige una plantilla ---------- */
    fun pickTemplate(tpl: HabitTemplate) = _wizard.update {
        it.copy(
            step = 1,
            form = it.form.prefillFromTemplate(tpl),
            notif = tpl.notifCfg          // propone la configuración de la plantilla
        )
    }

    /* Ediciones directas desde los pasos */
    fun updateForm(form: HabitForm)     = _wizard.update { it.copy(form  = form ) }
    fun updateNotif(cfg: NotifConfig)   = _wizard.update { it.copy(notif = cfg  ) }

    /* ---------- paso 1: siguiente ---------- */
    fun nextFromForm(form: HabitForm) {
        if (form.notify) {
            _wizard.update { it.copy(step = 2, form = form) }   // ir al configurador de notifs
        } else {
            finish(form, NotifConfig())                         // saltar paso 2
        }
    }

    /* El usuario decide empezar sin plantilla (botón ‘Crear desde cero’) */
    fun startBlankForm() = _wizard.update {
        it.copy(step = 1, form = HabitForm(), notif = NotifConfig())
    }

    /* ---------- paso 2: FIN ---------- */
    fun finish(form: HabitForm, cfg: NotifConfig) {
        // TODO persistir hábito en repositorio + programar notificaciones
        _wizard.value = WizardState()          // reiniciar wizard
    }

    /* Navegación */
    fun back()   = _wizard.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    fun cancel() { _wizard.value = WizardState() }
}

/* ---------------- Auxiliares ---------------- */

data class WizardState(
    val step : Int          = 0,           // 0 = templates | 1 = details | 2 = notif
    val form : HabitForm    = HabitForm(),
    val notif: NotifConfig  = NotifConfig()
)
