/**
 * @file AddHabitModal.kt
 * @ingroup ui_wizard
 * @brief Define el [Composable] principal para el diálogo modal del asistente de creación/edición de hábitos.
 *
 * @details
 * Este archivo contiene el Composable `AddHabitModal`, que actúa como el contenedor
 * y orquestador principal para el flujo de creación o edición de hábitos en un
 * diálogo modal. Utiliza `ModalContainer` como base visual.
 *
 * Funcionalidades Clave:
 * - **Paginación:** Emplea un [HorizontalPager] para presentar los diferentes pasos
 *   del asistente ([SuggestionStep], [BasicInfoStep], [TrackingStep], [NotificationStep]).
 * - **Gestión de Estado:** Observa el [AddHabitUiState] del [AddHabitViewModel] para
 *   determinar qué paso mostrar y cómo configurar los controles de navegación.
 * - **Navegación por Pasos:** Sincroniza el [PagerState] con el estado de la UI (`page`)
 *   y proporciona botones ("Atrás", "Siguiente", "Guardar", etc.) en una barra inferior
 *   cuyas acciones y etiquetas cambian dinámicamente según el paso actual y la validez
 *   del formulario. Incluye un [PagerIndicator].
 * - **Carga de Plantillas:** Utiliza la función helper [rememberTemplates] (con Hilt EntryPoint)
 *   para obtener la lista de [HabitTemplate]s para el `SuggestionStep`.
 * - **Diálogos Auxiliares:** Muestra [com.app.tibibalance.ui.components.dialogs.ModalInfoDialog]s para comunicar estados finales
 *   o confirmaciones, como `Saved`, `Error`, o `ConfirmDiscard`.
 * - **Restricción de Altura:** Limita la altura máxima del modal para adaptarse a diferentes
 *   tamaños de pantalla usando `LocalConfiguration`.
 *
 * @see AddHabitViewModel ViewModel que gestiona la lógica y el estado de este asistente.
 * @see AddHabitUiState Sealed interface que define los estados/pasos del asistente.
 * @see ModalContainer Componente base para el diálogo modal.
 * @see HorizontalPager Componente para la navegación entre pasos.
 * @see PagerState Estado que gestiona el Pager.
 * @see PagerIndicator Indicador visual de la página actual.
 * @see SuggestionStep, BasicInfoStep, TrackingStep, NotificationStep Composables para cada paso del asistente.
 * @see com.app.tibibalance.ui.components.dialogs.ModalInfoDialog Componente para mostrar diálogos de información/error/confirmación.
 * @see HabitTemplate Modelo de dominio para las plantillas de hábitos.
 * @see rememberTemplates Función helper para obtener las plantillas.
 * @see TemplateRepoEntryPoint Hilt EntryPoint para acceder al repositorio de plantillas.
 * @see ExperimentalFoundationApi Requerido por PagerState y HorizontalPager.
 */
@file:OptIn(ExperimentalFoundationApi::class) // Necesario para PagerState y HorizontalPager

package com.app.tibibalance.ui.wizard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
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
import com.app.tibibalance.ui.components.* // Importa componentes generales como ModalContainer, ModalInfoDialog, etc.
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.wizard.step.* // Importa los Composables de cada paso
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * @brief Composable que presenta el flujo completo del asistente de creación/edición de hábitos en un diálogo modal.
 *
 * @param onDismissRequest Lambda que se invoca cuando el usuario solicita cerrar el modal
 * (e.g., pulsando el botón 'X' o fuera del diálogo si está permitido).
 * @param vm Instancia de [AddHabitViewModel] (obtenida por defecto mediante Hilt) que gestiona
 * el estado ([AddHabitUiState]) y la lógica del asistente.
 */
@Composable
fun AddHabitModal(
    onDismissRequest: () -> Unit,
    vm: AddHabitViewModel = hiltViewModel()
) {
    // Observa el estado de la UI emitido por el ViewModel.
    val ui by vm.ui.collectAsState()

    /* ---- Sincronización Estado UI ↔︎ Página del Pager ---- */
    // Determina el índice de la página actual basado en el tipo de AddHabitUiState.
    val page = remember(ui) { // Recalcula 'page' solo si 'ui' cambia
        when (ui) {
            is AddHabitUiState.Suggestions  -> 0
            is AddHabitUiState.BasicInfo    -> 1
            is AddHabitUiState.Tracking     -> 2
            is AddHabitUiState.Notification -> 3
            else                            -> 0 // o podríamos mantener la página anterior
        }
    }
    // Estado del Pager, sincronizado con el 'page'.
    val pager = rememberPagerState(initialPage = page) { 4 }
    // Efecto para animar el Pager cuando cambia la página.
    LaunchedEffect(page) {
        if (pager.currentPage != page) {
            pager.animateScrollToPage(page)
        }
    }

    // Calcula la altura máxima del modal como un 85% de la altura de la pantalla.
    val maxH = LocalConfiguration.current.screenHeightDp.dp * .85f

    ModalContainer(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.heightIn(max = maxH),
        closeButtonEnabled = true
    ) {
        Column(Modifier.fillMaxSize()) {

            /* -------- Contenido Paginado -------- */
            HorizontalPager(
                state = pager,
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                pageSize = PageSize.Fill
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> SuggestionStep(
                        templates = rememberTemplates(),
                        onSuggestion = vm::pickTemplate
                    )
                    1 -> (ui as? AddHabitUiState.BasicInfo)?.let { currentState ->
                        BasicInfoStep(
                            initial = currentState.form,
                            errors = currentState.errors,
                            onFormChange = vm::updateBasic,
                            onBack = vm::back
                        )
                    }
                    2 -> (ui as? AddHabitUiState.Tracking)?.let { currentState ->
                        TrackingStep(
                            initial = currentState.form,
                            errors = currentState.errors,
                            onFormChange = vm::updateTracking,
                            onBack = vm::back
                        )
                    }
                    3 -> (ui as? AddHabitUiState.Notification)?.let { currentState ->
                        NotificationStep(
                            title = currentState.form.name.ifBlank { "Notificación" },
                            initialCfg = currentState.cfg,
                            onCfgChange = vm::updateNotif,
                            onBack = vm::back
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            /* -------- Barra Inferior -------- */
            if (ui is AddHabitUiState.Suggestions) {
                // Opción de centrar el botón de sugerencias
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = "Crear mi propio hábito",
                        onClick = vm::startBlankForm,
                        modifier = Modifier
                            .width(200.dp)
                            .height(48.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                // Barra original para los demás pasos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (page > 0) {
                        SecondaryButton("Atrás", onClick = vm::back)
                    } else {
                        Spacer(Modifier.width(120.dp))
                    }

                    Spacer(Modifier.width(8.dp))

                    when (val currentState = ui) {
                        is AddHabitUiState.BasicInfo -> PrimaryButton(
                            text = "Siguiente",
                            enabled = currentState.errors.isEmpty(),
                            onClick = vm::nextFromBasic
                        )
                        is AddHabitUiState.Tracking -> PrimaryButton(
                            text = if (currentState.form.notify && currentState.form.repeatPreset != RepeatPreset.INDEFINIDO)
                                "Siguiente" else "Guardar",
                            enabled = currentState.errors.isEmpty(),
                            onClick = { vm.nextFromTracking(onDismissRequest) }
                        )
                        is AddHabitUiState.Notification -> PrimaryButton(
                            text = "Guardar",
                            onClick = { vm.finish(onDismissRequest) }
                        )
                        else -> Unit
                    }
                }
            }

            PagerIndicator(
                pagerState = pager,
                pageCount = 4,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp)
            )
        }
    }

    /* -------- Diálogos Auxiliares -------- */
    when (val currentState = ui) {
        is AddHabitUiState.Saved -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Check,
            title = currentState.title,
            message = currentState.message
        )
        is AddHabitUiState.Error -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Error,
            iconColor = MaterialTheme.colorScheme.error,
            title = "Ups…",
            message = currentState.msg,
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

/**
 * @brief Composable helper que obtiene y observa la lista de plantillas de hábitos.
 * @details Utiliza [EntryPointAccessors] para obtener una instancia del [HabitTemplateRepository]
 * (inyectado por Hilt a nivel de aplicación) desde un Composable. Luego, recolecta
 * el flujo `templates` del repositorio para obtener la lista actualizada.
 *
 * @return La lista actual de [HabitTemplate] observada desde el repositorio.
 */
@Composable
private fun rememberTemplates(): List<HabitTemplate> {
    val repo = EntryPointAccessors.fromApplication(
        LocalContext.current.applicationContext,
        TemplateRepoEntryPoint::class.java
    ).templateRepo()
    return repo.templates.collectAsState(initial = emptyList()).value
}

/**
 * @brief Interfaz Hilt EntryPoint para acceder al [HabitTemplateRepository] desde Composables.
 * @details Defiene un punto de entrada para que Hilt proporcione la instancia singleton
 * del repositorio. Necesario porque `rememberTemplates` no puede usar inyección de constructor.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface TemplateRepoEntryPoint {
    /** @brief Provee la instancia del repositorio de plantillas. */
    fun templateRepo(): HabitTemplateRepository
}