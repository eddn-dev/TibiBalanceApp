@file:OptIn(ExperimentalFoundationApi::class)

package com.app.tibibalance.ui.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.data.repository.HabitTemplateRepository
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.wizard.step.HabitDetailsStep
import com.app.tibibalance.ui.wizard.step.NotificationStep
import com.app.tibibalance.ui.wizard.step.SuggestionStep
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

    /* página actual ↔︎ estado */
    val page = when (ui) {
        is AddHabitUiState.Suggestions  -> 0
        is AddHabitUiState.Details      -> 1
        is AddHabitUiState.Notification -> 2
        else                            -> 0
    }
    val pager = rememberPagerState(initialPage = page, pageCount = { 3 })
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
                    1 -> (ui as? AddHabitUiState.Details)?.let { st ->
                        HabitDetailsStep(
                            initial      = st.form,
                            errors       = st.errors,
                            onFormChange = vm::updateForm,
                            onBack       = vm::back
                        )
                    }
                    2 -> (ui as? AddHabitUiState.Notification)?.let { st ->
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
                        text = "Crear mi propio hábito",
                        onClick = vm::startBlankForm
                    )

                    is AddHabitUiState.Details -> PrimaryButton(
                        text = if (st.form.notify) "Siguiente" else "Guardar",
                        enabled = st.canProceed,
                        onClick = vm::nextFromDetails
                    )

                    is AddHabitUiState.Notification -> PrimaryButton(
                        text = "Guardar",
                        onClick = vm::finish
                    )

                    else -> Unit
                }
            }

            PagerIndicator(
                pagerState = pager,
                pageCount  = 3,
                modifier   = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp)
            )
        }
    }

    /* -------- diálogos auxiliares -------- */
    when (val st = ui) {
        is AddHabitUiState.ConfirmDiscard -> ModalInfoDialog(
            visible = true,
            icon    = Icons.Default.Warning,
            title   = "Reemplazar el formulario",
            message = "Perderás los datos ingresados. ¿Continuar?",
            primaryButton   = DialogButton("Sí")  { vm.confirmDiscard(true) },
            secondaryButton = DialogButton("No")  { vm.confirmDiscard(false) }
        )
        is AddHabitUiState.Saving -> ModalInfoDialog(visible = true, loading = true)
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
