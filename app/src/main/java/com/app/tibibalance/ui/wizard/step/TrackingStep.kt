/**
 * @file TrackingStep.kt
 * @ingroup ui_wizard_step
 * @brief Define el Composable para el paso de "Parámetros de seguimiento" en el asistente de creación/edición de hábitos.
 *
 * @details
 * Este Composable permite al usuario configurar cómo se medirá y repetirá un hábito,
 * así como su duración total y si desea activar notificaciones o el modo reto.
 *
 * Características principales:
 * - **Duración de la sesión:** El usuario puede definir la cantidad y la unidad de tiempo
 * (Minutos, Horas) para una sesión individual del hábito, o dejarla como "Indefinido".
 * El campo de cantidad ([InputNumber]) aparece condicionalmente.
 * - **Frecuencia de repetición:** Un [InputSelect] permite elegir entre presets comunes
 * (Diario, Semanal, etc.) o un modo "Personalizado".
 * - **Días de la semana (Personalizado):** Si se elige "Personalizado", se muestra una
 * cuadrícula de [FilterChip] (usando [FlowRow] experimental) para seleccionar los días específicos
 * de la semana en los que el hábito se repetirá.
 * - **Periodo total del hábito:** Similar a la duración de la sesión, el usuario puede
 * especificar la duración total del hábito (Cantidad y Unidad: Días, Semanas, Meses)
 * o dejarlo como "Indefinido".
 * - **Toggles:**
 * - "Notificarme": Un [SwitchToggle] para activar/desactivar las notificaciones (visible
 * solo si se ha definido una frecuencia de repetición).
 * - "Modo reto": Un [SwitchToggle] para activar/desactivar el modo reto. Este modo
 * requiere que se definan la repetición y el periodo. Un [com.app.tibibalance.ui.components.buttons.IconButton] con un
 * icono de información ([Icons.Default.Info]) abre un [ModalInfoDialog]
 * explicando las condiciones del modo reto.
 *
 * El estado del formulario ([HabitForm]) se gestiona localmente con `rememberSaveable`
 * (usando [HabitFormSaver]) y los cambios se notifican al ViewModel padre mediante `onFormChange`.
 * También incluye lógica en `LaunchedEffect` para desactivar automáticamente el modo reto si
 * sus requisitos de repetición o periodo dejan de cumplirse.
 * Los errores de validación recibidos se utilizan para marcar los campos correspondientes.
 *
 * @see com.app.tibibalance.ui.wizard.AddHabitViewModel ViewModel que gestiona el estado global del asistente.
 * @see com.app.tibibalance.domain.model.HabitForm Data class que representa el estado del formulario.
 * @see com.app.tibibalance.ui.wizard.HabitFormSaver Saver para la persistencia del estado de HabitForm.
 * @see com.app.tibibalance.domain.model.SessionUnit Enum para las unidades de duración de sesión.
 * @see com.app.tibibalance.domain.model.RepeatPreset Enum para los presets de repetición.
 * @see com.app.tibibalance.domain.model.PeriodUnit Enum para las unidades del periodo total.
 * @see InputNumber, InputSelect, SwitchToggle Componentes de entrada reutilizables.
 * @see Title Componente para el título de la pantalla.
 * @see ModalInfoDialog Diálogo para mostrar información sobre el modo reto.
 * @see androidx.compose.foundation.ExperimentalFoundationApi Requerido por FlowRow.
 * @see androidx.compose.foundation.layout.FlowRow Layout experimental para la cuadrícula de días.
 */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.*
import com.app.tibibalance.ui.components.* // Importa DialogButton, ModalInfoDialog, SwitchToggle
import com.app.tibibalance.ui.components.inputs.* // Importa InputNumber, InputSelect
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.wizard.HabitFormSaver


/**
 * @brief Composable para el paso de "Parámetros de seguimiento" en el asistente de hábitos.
 *
 * @param initial El estado inicial del [HabitForm] para este paso.
 * @param errors Lista de mensajes de error de validación relevantes para este paso.
 * @param onFormChange Callback que se invoca cuando el estado local del formulario ([form]) cambia,
 * notificando al ViewModel padre con el [HabitForm] actualizado.
 * @param onBack Callback opcional para manejar la navegación hacia atrás (actualmente no se usa
 * mediante un botón explícito en esta UI).
 */
@OptIn(ExperimentalFoundationApi::class)        // FlowRow aún es experimental
@Composable
fun TrackingStep(
    initial      : HabitForm,
    errors       : List<String>,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit = {} // Callback onBack definido pero no usado internamente
) {
    /* -------- Estado Local Serializable del Formulario -------- */
    // 'form' mantiene el estado actual de los campos de este paso.
    // Se inicializa con 'initial' y se guarda/restaura usando HabitFormSaver.
    var form   by rememberSaveable(stateSaver = HabitFormSaver) { mutableStateOf(initial) }

    // Efecto que propaga los cambios de 'form' al ViewModel padre.
    LaunchedEffect(form) { onFormChange(form) }

    //Estados para controlar la visibilidad de los diálogo de información
    var infoDlg by remember { mutableStateOf(false) }
    var infoRepeatDlg by remember { mutableStateOf(false) }
    var infoPeriodDlg by remember { mutableStateOf(false) }
    var infoDuracionDlg by remember { mutableStateOf(false) }


    /* Lógica para apagar el modo reto si sus requisitos dejan de cumplirse */
    // Efecto que observa cambios en la repetición y unidad de periodo.
    LaunchedEffect(form.repeatPreset, form.periodUnit) {
        if (form.challenge && // Si el modo reto está activo
            (form.repeatPreset == RepeatPreset.INDEFINIDO || // Y la repetición es indefinida
                    form.periodUnit   == PeriodUnit.INDEFINIDO) // O el periodo es indefinido
        ) {
            form = form.copy(challenge = false) // Desactiva el modo reto.
        }
    }

    /* -------- Flags de Error Derivados de la Lista 'errors' -------- */
    // Estos 'remember' optimizan la comprobación de errores, recalculando solo si 'errors' cambia.
    val sessionQtyErr = remember(errors) { errors.any { it.contains("duración", true) } }
    val periodQtyErr  = remember(errors) { errors.any { it.contains("periodo",  true) } }
    val weekDaysErr   = remember(errors) { errors.any { it.contains("día",      true) } }
    val repeatErr     = remember(errors) { errors.any { it.contains("repetición", true) } }

    // Columna principal que permite scroll y organiza las secciones de configuración.
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite desplazamiento vertical.
            .padding(horizontal = 12.dp, vertical = 16.dp), // Padding general.
        verticalArrangement = Arrangement.spacedBy(1.dp) // Espacio entre secciones.
    ) {
        Title("Parámetros de seguimiento", Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(2.dp))

        /* ------------------------------ Duración de la actividad ------------------------------ */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Duración de la actividad", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoDuracionDlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Información sobre la duración de la actividad")
            }
        }
        Row(
            Modifier.animateContentSize(), // Anima cambios de tamaño de la fila.
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre InputNumber e InputSelect.
        ) {
            // Muestra InputNumber solo si la unidad de sesión no es INDEFINIDO.
            AnimatedVisibility(visible = form.sessionUnit != SessionUnit.INDEFINIDO) {
                InputNumber(
                    value          = form.sessionQty?.toString().orEmpty(), // Valor actual o vacío.
                    onValueChange  = { form = form.copy(sessionQty = it.toIntOrNull()) }, // Actualiza 'form'.
                    placeholder    = "Cantidad",
                    modifier       = Modifier.width(120.dp), // Ancho fijo.
                    isError        = sessionQtyErr, // Indica estado de error.
                    supportingText = if (sessionQtyErr) "Requerido" else null // Mensaje de error.
                )
            }

            BoxWithConstraints(
                modifier = Modifier.weight(1f) // Solo ocupa el espacio restante
            ) {
                val isCompact = maxWidth < 180.dp // Umbral de espacio disponible

                val optionMap = if (isCompact || form.sessionQty != null) {
                    mapOf(
                        "No aplica" to SessionUnit.INDEFINIDO,
                        "min"       to SessionUnit.MINUTOS,
                        "hrs"         to SessionUnit.HORAS
                    )
                } else {
                    mapOf(
                        "No aplica"         to SessionUnit.INDEFINIDO,
                        "Ingresar minutos"  to SessionUnit.MINUTOS,
                        "Ingresar horas"    to SessionUnit.HORAS
                    )
                }

                val options = optionMap.keys.toList()
                val selectedOption = optionMap.entries.find { it.value == form.sessionUnit }?.key ?: "No aplica"

                InputSelect(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedString ->
                        val unit = optionMap[selectedString] ?: SessionUnit.INDEFINIDO
                        form = form.copy(
                            sessionUnit = unit,
                            sessionQty = form.sessionQty.takeIf { unit != SessionUnit.INDEFINIDO }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = sessionQtyErr && form.sessionUnit != SessionUnit.INDEFINIDO
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))

        /* ------------------------------ Repetición ------------------------------ */
        //Se añade una row para poder mostrar el botón de ayuda al lado del campo repetir hábito
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Repetir hábito", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoRepeatDlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Información sobre repetición")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
        InputSelect(
            label            = "", //<----
            options          = remember { listOf("No aplica","Diario","Cada 3 días",
                "Semanal","Quincenal","Mensual","Personalizado") }, // Opciones fijas.
            selectedOption   = when (form.repeatPreset) { // Mapea Enum a String.
                RepeatPreset.DIARIO       -> "Diario"
                RepeatPreset.CADA_3_DIAS   -> "Cada 3 días"
                RepeatPreset.SEMANAL       -> "Semanal"
                RepeatPreset.QUINCENAL     -> "Quincenal"
                RepeatPreset.MENSUAL       -> "Mensual"
                RepeatPreset.PERSONALIZADO -> "Personalizado"
                else                       -> "No aplica"
            },
            onOptionSelected = { selectedString -> // Callback al seleccionar.
                // Actualiza 'form', limpiando weekDays si no es "Personalizado".
                form = form.copy(
                    repeatPreset = when (selectedString) { // Mapea String de vuelta a Enum.
                        "Diario"        -> RepeatPreset.DIARIO
                        "Cada 3 días"   -> RepeatPreset.CADA_3_DIAS
                        "Semanal"       -> RepeatPreset.SEMANAL
                        "Quincenal"     -> RepeatPreset.QUINCENAL
                        "Mensual"       -> RepeatPreset.MENSUAL
                        "Personalizado" -> RepeatPreset.PERSONALIZADO
                        else            -> RepeatPreset.INDEFINIDO
                    },
                    weekDays = if (selectedString != "Personalizado") emptySet() else form.weekDays
                )
            },
            isError        = repeatErr, // Indica estado de error.
            supportingText = if (repeatErr) "Requerido para modo reto" else null, // Mensaje de error.
            modifier = Modifier.weight(1f) // Modificado para dar espacio al botón de info
        )
        }

        /* ---------- Días de la semana (rejilla para modo Personalizado) ---------- */
        // Muestra esta sección solo si la repetición es PERSONALIZADO.
        AnimatedVisibility(visible = form.repeatPreset == RepeatPreset.PERSONALIZADO) {
            Column {
                Text("Días de la semana", style = MaterialTheme.typography.bodyMedium)
                // FlowRow es experimental y permite que los elementos fluyan a la siguiente línea.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp), // Espacio horizontal entre chips.
                    verticalArrangement   = Arrangement.spacedBy(4.dp),   // Espacio vertical si hay múltiples filas.
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val labels = remember { listOf("L","M","X","J","V","S","D") } // Etiquetas para los días.
                    labels.forEachIndexed { idx, label ->
                        val day = idx + 1 // Día de la semana (1=Lunes..7=Domingo).
                        val selected = day in form.weekDays // Verifica si el día está seleccionado.
                        // Chip de filtro para cada día.
                        FilterChip(
                            selected = selected,
                            onClick  = { // Callback al pulsar el chip.
                                // Añade o quita el día del conjunto de weekDays en 'form'.
                                form = if (selected)
                                    form.copy(weekDays = form.weekDays - day)
                                else
                                    form.copy(weekDays = form.weekDays + day)
                            },
                            label    = { Text(label) } // Etiqueta del chip (L, M, X...).
                        )
                    }
                }
                // Muestra mensaje de error si aplica.
                if (weekDaysErr)
                    Text("Selecciona al menos un día",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error))
            }
        }
        Spacer(modifier = Modifier.height(1.dp))



        /* ------------------------------ Periodo total del Hábito ------------------------------ */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Periodo del hábito", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoPeriodDlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Información sobre el Periodo")
            }
        }
        Row(
            Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Campo de número
            AnimatedVisibility(visible = form.periodUnit != PeriodUnit.INDEFINIDO) {
                InputNumber(
                    value = form.periodQty?.toString().orEmpty(),
                    onValueChange = { form = form.copy(periodQty = it.toIntOrNull()) },
                    placeholder = "Cantidad",
                    modifier = Modifier.width(120.dp),
                    isError = periodQtyErr,
                    supportingText = if (periodQtyErr) "Requerido" else null
                )
            }

            // Campo de selección adaptativo
            BoxWithConstraints(
                modifier = Modifier.weight(1f) // Solo ocupa el espacio restante
            ) {
                val isCompact = maxWidth < 180.dp // Ajusta este umbral como gustes

                val optionMap = if (isCompact) {
                    mapOf(
                        "No aplica" to PeriodUnit.INDEFINIDO,
                        "días" to PeriodUnit.DIAS,
                        "semanas" to PeriodUnit.SEMANAS,
                        "meses" to PeriodUnit.MESES
                    )
                } else {
                    mapOf(
                        "No aplica" to PeriodUnit.INDEFINIDO,
                        "Ingresar días" to PeriodUnit.DIAS,
                        "Ingresar semanas" to PeriodUnit.SEMANAS,
                        "Ingresar meses" to PeriodUnit.MESES
                    )
                }

                val options = optionMap.keys.toList()
                val selectedOption = optionMap.entries.find { it.value == form.periodUnit }?.key ?: "No aplica"

                InputSelect(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedString ->
                        val unit = optionMap[selectedString] ?: PeriodUnit.INDEFINIDO
                        form = form.copy(
                            periodUnit = unit,
                            periodQty = form.periodQty.takeIf { unit != PeriodUnit.INDEFINIDO }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = periodQtyErr && form.periodUnit != PeriodUnit.INDEFINIDO
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))


        /* ---------- Toggles: Notificarme y Modo Reto ---------- */
        // Muestra el toggle "Notificarme" solo si la repetición no es "No aplica".
        if (form.repeatPreset != RepeatPreset.INDEFINIDO) {
            ToggleRow(
                label    = "Notificarme",
                checked  = form.notify, // Vinculado al estado 'notify' del formulario.
                onToggle = { form = form.copy(notify = !form.notify) } // Alterna el estado.
            )
        }
        // Toggle para "Modo reto".
        ToggleRow(
            label   = "Modo reto",
            checked = form.challenge, // Vinculado al estado 'challenge'.
            onToggle = { // Lógica para activar/desactivar modo reto.
                // Verifica si se cumplen los requisitos (repetición Y periodo definidos).
                val canEnable = form.repeatPreset != RepeatPreset.INDEFINIDO &&
                        form.periodUnit   != PeriodUnit.INDEFINIDO
                if (canEnable) { // Si se cumplen
                    form = form.copy(challenge = !form.challenge) // Alterna el estado.
                } else { // Si no se cumplen
                    infoDlg = true // Muestra el diálogo de información.
                }
            },
            trailing = { // Elemento al final de la fila: botón de información.
                IconButton(onClick = { infoDlg = true }) { // Muestra el diálogo al pulsar.
                    Icon(Icons.Default.Info, contentDescription = "Información sobre modo reto")
                }
            }
        )
    }

    /* ---------- Diálogo de Ayuda para Modo Reto ---------- */
    // Muestra el ModalInfoDialog si infoDlg es true.
    if (infoDlg) {
        ModalInfoDialog(
            visible  = true, // Controla la visibilidad del diálogo.
            icon     = Icons.Default.Info, // Icono a mostrar.
            title    = "Modo reto", // Título del diálogo.
            message  = "Cuando un hábito está en modo reto no se puede editar.\n" +
                    "Debes definir repetición y periodo obligatorios.", // Mensaje explicativo.
            primaryButton = DialogButton("Entendido") { infoDlg = false } // Botón para cerrar el diálogo.
        )
    }

    if (infoDuracionDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Duración de la actividad",
            message  = "Selecciona cuánto tiempo realizaras la actividad.",
            primaryButton = DialogButton("Entendido") { infoDuracionDlg = false }
        )
    }

    //Dialogo de ayuda para el botón de información de repetir hábito
    if (infoRepeatDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Repetir hábito",
            message  = "Indica con qué frecuencia quieres repetir este hábito para notificarte.",
            primaryButton = DialogButton("Entendido") { infoRepeatDlg = false }
        )
    }

    if (infoPeriodDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Periodo de un hábito",
            message  = "Indica durante cuánto tiempo quieres realizar un hábito y mantener su seguimiento.",
            primaryButton = DialogButton("Entendido") { infoPeriodDlg = false }
        )
    }
}


/**
 * @brief Composable helper privado para crear una fila con un texto y un [SwitchToggle].
 *
 * @param label El texto a mostrar junto al interruptor.
 * @param checked El estado actual del interruptor.
 * @param onToggle Callback invocado cuando el estado del interruptor cambia.
 * @param trailing Slot opcional para añadir un Composable (e.g., un IconButton) antes del Switch.
 */
@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    trailing: @Composable () -> Unit = {} // Slot para contenido al final, antes del Switch
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onToggle() }, // Hace toda la fila clicable para alternar el switch.
        verticalAlignment = Alignment.CenterVertically, // Centra elementos verticalmente.
        horizontalArrangement = Arrangement.SpaceBetween // Espacia el texto y el grupo switch+trailing.
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium) // Etiqueta del toggle.
        Row(verticalAlignment = Alignment.CenterVertically) { // Fila para agrupar trailing y Switch.
            trailing() // Renderiza el contenido del slot 'trailing'.
            Spacer(Modifier.width(4.dp)) // Pequeño espacio.
            SwitchToggle(checked = checked, onCheckedChange = { onToggle() }) // El interruptor.
        }
    }
}