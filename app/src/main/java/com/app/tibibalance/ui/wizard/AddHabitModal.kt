@file:OptIn(ExperimentalFoundationApi::class)

package com.app.tibibalance.ui.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.data.repository.HabitTemplateRepository
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.domain.model.RepeatPreset
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.wizard.step.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@Composable
fun AddHabitModal(
    onDismissRequest: () -> Unit,
    vm: AddHabitViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    /* ---- estado ↔︎ página ---- */
    val page = when (ui) {
        is AddHabitUiState.Suggestions  -> 0
        is AddHabitUiState.BasicInfo    -> 1
        is AddHabitUiState.Tracking     -> 2
        is AddHabitUiState.Notification -> 3
        else                            -> 0
    }
    val pager = rememberPagerState(initialPage = page, pageCount = { 4 })
    LaunchedEffect(page) { pager.animateScrollToPage(page) }

    val maxH = LocalConfiguration.current.screenHeightDp.dp * .85f
    ModalContainer(
        onDismissRequest   = { vm.finish(); onDismissRequest() },
        modifier           = Modifier.heightIn(max = maxH),
        closeButtonEnabled = true
    ) {
        Column(Modifier.fillMaxSize()) {

            /* -------- páginas -------- */
            HorizontalPager(
                state             = pager,
                userScrollEnabled = false,
                modifier          = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                pageSize          = PageSize.Fill
            ) { idx ->
                when (idx) {
                    0 -> SuggestionStep(
                        templates    = rememberTemplates(),
                        onSuggestion = vm::pickTemplate
                    )
                    1 -> (ui as? AddHabitUiState.BasicInfo)?.let { st ->
                        BasicInfoStep(
                            initial      = st.form,
                            errors       = st.errors,
                            onFormChange = vm::updateBasic,
                            onBack       = vm::back
                        )
                    }
                    2 -> (ui as? AddHabitUiState.Tracking)?.let { st ->
                        TrackingStep(
                            initial      = st.form,
                            errors       = st.errors,
                            onFormChange = vm::updateTracking,
                            onBack       = vm::back
                        )
                    }
                    3 -> (ui as? AddHabitUiState.Notification)?.let { st ->
                        NotificationStep(
                            title       = st.form.name.ifBlank { "Notificación" },
                            initialCfg  = st.cfg,
                            onCfgChange = vm::updateNotif,
                            onBack      = vm::back
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            /* -------- barra inferior -------- */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (page > 0)
                    SecondaryButton("Atrás", onClick = vm::back)

                Spacer(Modifier.width(8.dp))

                when (val st = ui) {
                    is AddHabitUiState.Suggestions -> PrimaryButton(
                        text     = "Crear mi propio hábito",
                        onClick  = vm::startBlankForm
                    )

                    is AddHabitUiState.BasicInfo -> PrimaryButton(
                        text     = "Siguiente",
                        enabled  = st.errors.isEmpty(),
                        onClick  = vm::nextFromBasic
                    )

                    is AddHabitUiState.Tracking -> PrimaryButton(
                        text     = if (st.form.notify && st.form.repeatPreset != RepeatPreset.INDEFINIDO)
                            "Siguiente" else "Guardar",
                        enabled  = st.errors.isEmpty(),
                        onClick  = vm::nextFromTracking
                    )

                    is AddHabitUiState.Notification -> PrimaryButton(
                        text    = "Guardar",
                        onClick = vm::finish
                    )

                    else -> Unit
                }
            }

            PagerIndicator(
                pagerState = pager,
                pageCount  = 4,
                modifier   = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp)
            )
        }
    }

    /* -------- diálogos auxiliares -------- */
    when (val st = ui) {
        is AddHabitUiState.Saved -> ModalInfoDialog(
            visible = true,
            icon    = Icons.Default.Check,
            title   = st.title,
            message = st.message
        )

        is AddHabitUiState.Error -> ModalInfoDialog(
            visible = true,
            icon    = Icons.Default.Error,
            iconColor = MaterialTheme.colorScheme.error,
            title   = "Ups…",
            message = st.msg,
            primaryButton = DialogButton("Entendido") { vm.startBlankForm() }
        )

        is AddHabitUiState.ConfirmDiscard -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Warning,
            iconColor = MaterialTheme.colorScheme.primary,
            title = "¿Sobrescribir cambios?",
            message = "Ya comenzaste a crear un hábito. ¿Quieres reemplazarlo con la plantilla seleccionada?",
            primaryButton = DialogButton("Continuar") { vm.confirmDiscard(true) },
            secondaryButton = DialogButton("Cancelar") { vm.confirmDiscard(false) }
        )

        else -> Unit
    }
}

/* -- helper Room→Compose -- */
@Composable
private fun rememberTemplates(): List<HabitTemplate> {
    val repo = EntryPointAccessors.fromApplication(
        LocalContext.current.applicationContext,
        TemplateRepoEntryPoint::class.java
    ).templateRepo()
    return repo.templates.collectAsState(initial = emptyList()).value
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TemplateRepoEntryPoint { fun templateRepo(): HabitTemplateRepository }
