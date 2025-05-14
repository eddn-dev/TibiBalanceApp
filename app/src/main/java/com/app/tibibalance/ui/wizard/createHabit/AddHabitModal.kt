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
 * del asistente ([SuggestionStep], [BasicInfoStep], [TrackingStep], [NotificationStep]).
 * - **Gestión de Estado:** Observa el [AddHabitUiState] del [AddHabitViewModel] para
 * determinar qué paso mostrar y cómo configurar los controles de navegación.
 * - **Navegación por Pasos:** Sincroniza el [PagerState] con el estado de la UI (`page`)
 * y proporciona botones ("Atrás", "Siguiente", "Guardar", etc.) en una barra inferior
 * cuyas acciones y etiquetas cambian dinámicamente según el paso actual y la validez
 * del formulario. Incluye un [PagerIndicator].
 * - **Carga de Plantillas:** Utiliza la función helper [rememberTemplates] (con Hilt EntryPoint)
 * para obtener la lista de [HabitTemplate]s para el `SuggestionStep`.
 * - **Diálogos Auxiliares:** Muestra [ModalInfoDialog]s para comunicar estados finales
 * o confirmaciones, como `Saved`, `Error`, o `ConfirmDiscard`.
 * - **Restricción de Altura:** Limita la altura máxima del modal para adaptarse a diferentes
 * tamaños de pantalla usando `LocalConfiguration`.
 *
 * @see AddHabitViewModel ViewModel que gestiona la lógica y el estado de este asistente.
 * @see AddHabitUiState Sealed interface que define los estados/pasos del asistente.
 * @see ModalContainer Componente base para el diálogo modal.
 * @see HorizontalPager Componente para la navegación entre pasos.
 * @see PagerState Estado que gestiona el Pager.
 * @see PagerIndicator Indicador visual de la página actual.
 * @see SuggestionStep, BasicInfoStep, TrackingStep, NotificationStep Composables para cada paso del asistente.
 * @see ModalInfoDialog Componente para mostrar diálogos de información/error/confirmación.
 * @see HabitTemplate Modelo de dominio para las plantillas de hábitos.
 * @see rememberTemplates Función helper para obtener las plantillas.
 * @see TemplateRepoEntryPoint Hilt EntryPoint para acceder al repositorio de plantillas.
 * @see ExperimentalFoundationApi Requerido por PagerState y HorizontalPager.
 */
@file:OptIn(ExperimentalFoundationApi::class) // Necesario para PagerState y HorizontalPager

package com.app.tibibalance.ui.wizard.createHabit

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
import com.app.tibibalance.ui.components.* // Importa componentes generales como ModalContainer, ModalInfoDialog, etc.
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.wizard.createHabit.step.BasicInfoStep
import com.app.tibibalance.ui.wizard.createHabit.step.NotificationStep
import com.app.tibibalance.ui.wizard.createHabit.step.SuggestionStep
import com.app.tibibalance.ui.wizard.createHabit.step.TrackingStep
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
            // Para otros estados (Saving, Saved, Error, ConfirmDiscard), permanece en la página 0 o la última válida.
            // Podría ajustarse si se quisiera que el Pager refleje esos estados. Aquí se mantiene simple.
            else                            -> 0 // O podríamos mantener la página anterior si tuviéramos acceso a ella.
        }
    }
    // Estado del Pager, sincronizado con el 'page' derivado del uiState.
    val pager = rememberPagerState(initialPage = page) { 4 } // 4 páginas en total.
    // Efecto para animar el Pager a la página correcta cuando 'page' cambia.
    LaunchedEffect(page) {
        if (pager.currentPage != page) { // Evita animar si ya está en la página correcta
            pager.animateScrollToPage(page)
        }
    }

    // Calcula la altura máxima del modal como un 85% de la altura de la pantalla.
    val maxH = LocalConfiguration.current.screenHeightDp.dp * .85f

    // Contenedor modal base.
    ModalContainer(
        onDismissRequest = onDismissRequest,
        // Limita la altura máxima del modal.
        modifier           = Modifier.heightIn(max = maxH),
        closeButtonEnabled = true // Muestra el botón 'X' para cerrar.
    ) {
        // Columna principal que organiza el Pager y la barra inferior.
        Column(Modifier.fillMaxSize()) {

            /* -------- Contenido Paginado -------- */
            HorizontalPager(
                state             = pager, // Vinculado al estado del Pager.
                userScrollEnabled = false, // Deshabilita el desplazamiento manual entre páginas.
                modifier          = Modifier
                    .weight(1f) // Ocupa el espacio vertical restante.
                    .fillMaxWidth(),
                pageSize          = PageSize.Fill // Cada página ocupa todo el ancho.
            ) { pageIndex -> // El índice de la página a renderizar.
                // Renderiza el Composable del paso correspondiente al índice.
                when (pageIndex) {
                    0 -> SuggestionStep(
                        templates = rememberTemplates(), // Obtiene las plantillas.
                        onSuggestion = vm::pickTemplate // Callback para seleccionar plantilla.
                    )
                    1 -> (ui as? AddHabitUiState.BasicInfo)?.let { currentState -> // Muestra si el estado es BasicInfo.
                        BasicInfoStep(
                            initial = currentState.form, // Pasa el formulario actual.
                            errors = currentState.errors, // Pasa los errores de validación.
                            onFormChange = vm::updateBasic, // Callback para actualizar el form en el VM.
                            onBack = vm::back // Callback para ir atrás.
                        )
                    }
                    2 -> (ui as? AddHabitUiState.Tracking)?.let { currentState -> // Muestra si el estado es Tracking.
                        TrackingStep(
                            initial = currentState.form,
                            errors = currentState.errors,
                            onFormChange = vm::updateTracking,
                            onBack = vm::back
                        )
                    }
                    3 -> (ui as? AddHabitUiState.Notification)?.let { currentState -> // Muestra si el estado es Notification.
                        NotificationStep(
                            // Usa el nombre del hábito como título, o "Notificación" si está vacío.
                            title = currentState.form.name.ifBlank { "Notificación" },
                            initialCfg = currentState.cfg, // Pasa la configuración de notificación.
                            onCfgChange = vm::updateNotif, // Callback para actualizar la config.
                            onBack = vm::back
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            /* -------- Barra Inferior con Botones y PagerIndicator -------- */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp), // Padding de la barra.
                horizontalArrangement = Arrangement.SpaceBetween // Espacia los botones.
            ) {
                // Botón "Atrás" (visible solo si no estamos en la primera página).
                if (page > 0) {
                    SecondaryButton("Atrás", onClick = vm::back)
                }


                Spacer(Modifier.width(8.dp)) // Espacio entre botones.

                // Botón principal (derecha), cuya acción y texto dependen del estado/paso actual.
                when (val currentState = ui) { // Evalúa el estado actual de la UI.
                    is AddHabitUiState.Suggestions -> PrimaryButton(
                        text     = "Crear mi propio hábito",
                        modifier = Modifier
                            .fillMaxWidth()   // o .weight(1f)
                            .heightIn(min = 48.dp),
                        onClick  = vm::startBlankForm
                    )

                    is AddHabitUiState.BasicInfo -> PrimaryButton(
                        text     = "Siguiente",
                        // Habilita el botón solo si no hay errores de validación.
                        enabled  = currentState.errors.isEmpty(),
                        onClick  = vm::nextFromBasic // Avanza al siguiente paso.
                    )

                    is AddHabitUiState.Tracking -> PrimaryButton(
                        // El texto cambia a "Guardar" si no se configuran notificaciones.
                        text     = if (currentState.form.notify && currentState.form.repeatPreset != RepeatPreset.INDEFINIDO)
                            "Siguiente" else "Guardar",
                        enabled  = currentState.errors.isEmpty(), // Habilitado si no hay errores.
                        // Llama a la función que decide si ir a Notificaciones o guardar directamente.
                        onClick  = { vm.nextFromTracking(onDismissRequest) }
                    )

                    is AddHabitUiState.Notification -> PrimaryButton(
                        text    = "Guardar",
                        // Llama a la función final para guardar el hábito y cerrar el modal.
                        onClick = { vm.finish(onDismissRequest) }
                    )

                    // No muestra botón principal en otros estados (Saving, Saved, Error, etc.).
                    else -> Unit
                }
            }

            // Indicador de página actual.
            PagerIndicator(
                pagerState = pager, // Vinculado al estado del Pager.
                pageCount  = 4, // Número total de páginas/pasos.
                modifier   = Modifier
                    .align(Alignment.CenterHorizontally) // Centrado horizontalmente.
                    .padding(bottom = 4.dp) // Padding inferior.
            )
        }
    }

    /* -------- Diálogos Auxiliares (Informativos/Confirmación) -------- */
    // Muestra diálogos modales basados en el estado de la UI.
    when (val currentState = ui) {
        // Muestra diálogo de éxito al guardar.
        is AddHabitUiState.Saved -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Check,
            title = currentState.title,
            message = currentState.message
            // Se cierra automáticamente por el flujo del ViewModel (delay + onFinish).
        )

        // Muestra diálogo de error general.
        is AddHabitUiState.Error -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Error,
            iconColor = MaterialTheme.colorScheme.error,
            title = "Ups…",
            message = currentState.msg,
            // Botón para volver al inicio del formulario vacío.
            primaryButton = DialogButton("Entendido") { vm.startBlankForm() }
        )

        // Muestra diálogo para confirmar si se descartan cambios al seleccionar una plantilla.
        is AddHabitUiState.ConfirmDiscard -> ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Warning,
            iconColor = MaterialTheme.colorScheme.primary,
            title = "¿Sobrescribir cambios?",
            message = "Ya comenzaste a crear un hábito. ¿Quieres reemplazarlo con la plantilla seleccionada?",
            // Botones para confirmar o cancelar el descarte.
            primaryButton = DialogButton("Continuar") { vm.confirmDiscard(true) },
            secondaryButton = DialogButton("Cancelar") { vm.confirmDiscard(false) }
        )

        // No muestra diálogos adicionales en otros estados.
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
    // Obtiene el repositorio a través del EntryPoint de Hilt.
    val repo = EntryPointAccessors.fromApplication(
        LocalContext.current.applicationContext,
        TemplateRepoEntryPoint::class.java
    ).templateRepo()
    // Colecta el flujo de plantillas como estado de Compose.
    return repo.templates.collectAsState(initial = emptyList()).value
}

/**
 * @brief Interfaz Hilt EntryPoint para acceder al [HabitTemplateRepository] desde Composables.
 * @details Define un punto de entrada para que Hilt proporcione la instancia singleton
 * del repositorio. Necesario porque `rememberTemplates` no puede usar inyección de constructor.
 * Se instala en [SingletonComponent] para acceder a la instancia a nivel de aplicación.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface TemplateRepoEntryPoint {
    /** @brief Provee la instancia del repositorio de plantillas. */
    fun templateRepo(): HabitTemplateRepository
}