@file:OptIn(ExperimentalFoundationApi::class)

package com.app.tibibalance.ui.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
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
import com.app.tibibalance.domain.model.NotifConfig          // ← nuevo import
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.wizard.step.HabitDetailsStep
import com.app.tibibalance.ui.wizard.step.NotificationStep    // asegúrate de que use NotifConfig
import com.app.tibibalance.ui.wizard.step.SuggestionStep
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch

/*─────────────────────────   M O D A L   ─────────────────────────*/
@Composable
fun AddHabitModal(
    onDismissRequest: () -> Unit,
    vm: AddHabitViewModel = hiltViewModel()
) {
    /* ---------- estado global del wizard ---------- */
    val st    by vm.wizard.collectAsState()
    val scope = rememberCoroutineScope()

    /* ---------- estado del pager interno ---------- */
    val pager = rememberPagerState(initialPage = st.step, pageCount = { 3 })
    LaunchedEffect(st.step) { pager.animateScrollToPage(st.step) }

    /* alto máximo = 85 % de la pantalla */
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp * .85f

    /* -----------------------   D I A L O G   ---------------------- */
    ModalContainer(
        onDismissRequest   = { vm.cancel(); onDismissRequest() },
        modifier           = Modifier.heightIn(max = maxHeight),
        closeButtonEnabled = true
    ) {

        /* ---------- layout base del wizard ---------- */
        Column(Modifier.fillMaxSize()) {

            /* ---------- CONTENIDO / PÁGINAS ---------- */
            HorizontalPager(
                state             = pager,
                userScrollEnabled = false,
                modifier          = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                pageSize          = PageSize.Fill
            ) { page ->
                when (page) {
                    /* Paso 0 ─ Plantillas sugeridas */
                    0 -> SuggestionStep(
                        templates    = rememberTemplates(),
                        onSuggestion = vm::pickTemplate
                    )

                    /* Paso 1 ─ Detalles */
                    1 -> HabitDetailsStep(
                        initial      = st.form,
                        onFormChange = vm::updateForm,
                        onBack       = vm::back
                    )

                    /* Paso 2 ─ Notificación */
                    2 -> NotificationStep(
                        title       = st.form.name.ifBlank { "Notificación" },
                        initialCfg  = st.notif,     // ← NotifConfig
                        onCfgChange = vm::updateNotif,
                        onBack      = vm::back
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            /* ---------- BARRA DE ACCIONES ---------- */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                /* botón ATRÁS */
                if (st.step > 0)
                    SecondaryButton("Atrás", onClick = vm::back)

                Spacer(Modifier.width(8.dp))

                /* botón principal contextual */
                when (st.step) {
                    0 -> PrimaryButton(
                        text    = "Crear mi propio hábito",
                        onClick = vm::startBlankForm
                    )

                    1 -> PrimaryButton(
                        text    = if (st.form.notify) "Siguiente" else "Guardar",
                        onClick = { vm.nextFromForm(st.form) }
                    )

                    2 -> PrimaryButton(
                        text    = "Guardar",
                        onClick = {
                            vm.finish(st.form, st.notif)   // ← NotifConfig
                            onDismissRequest()
                        }
                    )
                }
            }

            /* ---------- INDICADOR ---------- */
            PagerIndicator(
                pagerState = pager,
                pageCount  = 3,
                modifier   = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

/*──────── helper: plantillas cacheadas en Room (Flow → Compose) ───────*/
@Composable
private fun rememberTemplates(): List<HabitTemplate> {
    val repo = EntryPointAccessors
        .fromApplication(
            LocalContext.current.applicationContext,
            TemplateRepoEntryPoint::class.java
        )
        .templateRepo()
    return repo.templates.collectAsState(initial = emptyList()).value
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TemplateRepoEntryPoint {
    fun templateRepo(): HabitTemplateRepository
}
