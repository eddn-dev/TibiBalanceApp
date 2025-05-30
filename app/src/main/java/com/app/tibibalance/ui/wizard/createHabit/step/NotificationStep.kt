/**
 * @file    NotificationStep.kt
 * @ingroup ui_wizard_step
 * @brief   Define el Composable para el paso de configuración de notificaciones dentro del asistente de creación/edición de hábitos.
 *
 * @details
 * Este Composable presenta la interfaz de usuario donde el usuario puede definir cómo y cuándo
 * desea recibir recordatorios para un hábito específico. Permite configurar:
 * - **Horas de recordatorio:** El usuario puede añadir múltiples horas específicas usando un [TimePicker]
 * presentado en un [AlertDialog]. Las horas seleccionadas se muestran y pueden eliminarse.
 * - **Mensaje personalizado:** Un campo [InputText] permite definir el texto que aparecerá en la notificación.
 * - **Fecha de inicio:** Un [OutlinedButton] que, al pulsarse, muestra un [ModalDatePickerDialog]
 * para seleccionar la fecha a partir de la cual las notificaciones deben comenzar.
 * - **Modo de notificación:** Un [InputSelect] permite elegir entre Silencioso, Sonido o Vibrar ([NotifMode]).
 * - **Vibración (condicional):** Un [SwitchToggle] aparece (usando [AnimatedVisibility])
 * solo si el modo seleccionado es "Sonido", permitiendo activar/desactivar la vibración adicional.
 * - **Antelación:** Un campo [InputText] numérico permite especificar cuántos minutos antes
 * de la hora programada debe mostrarse la notificación.
 *
 * El estado de la configuración se gestiona localmente con `remember { mutableStateOf(initialCfg) }`
 * y los cambios se propagan al ViewModel padre a través del callback `onCfgChange`
 * dentro de un `LaunchedEffect`. Se utilizan diálogos modales para los selectores de hora y fecha.
 * Requiere `ExperimentalMaterial3Api` debido al uso de `TimePicker` y `rememberTimePickerState`.
 *
 * @see com.app.tibibalance.ui.wizard.createHabit.AddHabitViewModel ViewModel que gestiona el estado global del asistente.
 * @see NotifConfig Data class que representa la configuración de notificación.
 * @see ModalDatePickerDialog Diálogo personalizado para seleccionar fechas.
 * @see TimePicker Composable estándar para seleccionar la hora.
 * @see AlertDialog Contenedor para el TimePicker.
 * @see InputText Componente para la entrada de texto (mensaje, antelación).
 * @see InputSelect Componente para seleccionar el modo.
 * @see SwitchToggle Componente para el interruptor de vibración.
 * @see Title Componente para el título de la pantalla.
 * @see kotlinx.datetime.LocalDate Tipo de dato para las fechas.
 */
/* ui/wizard/step/NotificationStep.kt */
package com.app.tibibalance.ui.wizard.createHabit.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.NotifConfig
import com.app.tibibalance.domain.model.NotifMode
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.components.modals.ModalDatePickerDialog
import com.app.tibibalance.ui.components.SwitchToggle
import com.app.tibibalance.ui.components.inputs.InputSelect
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Title
import kotlinx.datetime.toJavaLocalDate // Para convertir a java.time para DatePicker
import java.time.LocalDate // Tipo usado por DatePicker
import java.time.format.DateTimeFormatter
import kotlinx.datetime.toKotlinLocalDate // Para convertir de vuelta desde java.time

/**
 * @brief Composable para el paso de configuración de notificaciones del asistente de hábitos.
 *
 * @param title El título a mostrar en la parte superior, usualmente el nombre del hábito.
 * @param initialCfg La configuración inicial ([NotifConfig]) para este paso.
 * @param onCfgChange Callback invocado cada vez que la configuración local ([cfg]) cambia.
 * @param onBack Callback opcional para manejar la navegación hacia atrás (no usado por UI interna aquí).
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para TimePicker y rememberTimePickerState
@Composable
fun NotificationStep(
    title       : String,
    initialCfg  : NotifConfig,
    onCfgChange : (NotifConfig) -> Unit,
    onBack      : () -> Unit = {} // Callback onBack definido pero no usado internamente
) {
    /* -------- Estados Locales -------- */
    // Estado mutable para la configuración de notificación, inicializado con los datos recibidos.
    var cfg by remember { mutableStateOf(initialCfg) }

    // Estados para controlar la visibilidad de los cuadros de diálogo.
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Formateador para mostrar la fecha seleccionada en el botón.
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy") }

    /* Propaga cambios hacia el ViewModel padre */
    // Efecto que observa cambios en 'cfg' y llama a onCfgChange para notificar al ViewModel.
    LaunchedEffect(cfg) { onCfgChange(cfg) }

    //Para el botón de ayuda al lado de añadir hora
    var infoTimeDlg by remember { mutableStateOf(false) }

    var infoMsgDlg by remember { mutableStateOf(false) }

    var infoMinsADlg by remember { mutableStateOf(false) }

    /* ---------------------------------- Contenido Principal (Scrollable) ---------------------------------- */
    // Columna que permite desplazamiento vertical y define el padding y espaciado.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()) // Habilita scroll.
            .padding(horizontal = 12.dp, vertical = 16.dp), // Padding externo.
        verticalArrangement = Arrangement.spacedBy(10.dp) // Espacio vertical entre secciones.
    ) {
        // Título de la "pantalla".
        Title("Notificaciones", Modifier.align(Alignment.CenterHorizontally))
        // Subtítulo con el hábito que se esta ingresando
        /*Subtitle(
            text = title,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )*/

        /* ---------------------------------- Sección: Horas de Recordatorio ---------------------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text("Horas de recordatorio:", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoTimeDlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Ayuda sobre añadir hora")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Espacio entre horas y botón añadir.
            // Muestra cada hora añadida con un botón para eliminarla.
            cfg.timesOfDay.forEach { hhmm ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Alinea texto a la izq, botón a la der.
                ) {
                    Text(hhmm, style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = { // Botón Eliminar
                        // Actualiza 'cfg' eliminando la hora correspondiente de la lista.
                        cfg = cfg.copy(timesOfDay = cfg.timesOfDay - hhmm)
                    }) { Text("Eliminar") }
                }
            }
            OutlinedButton(onClick = { showTimePicker = true }) { // Botón para abrir el diálogo TimePicker. (reloj elegir hora)
                Text("Añadir hora")
            }
        }

        /* -------------------------------- Sección: Mensaje de Notificación -------------------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoMsgDlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Ayuda sobre mensaje de notificación")
            }
        }
        InputText(
            value = cfg.message, // Valor actual del mensaje.
            onValueChange = { newMsg -> cfg = cfg.copy(message = newMsg) }, // Actualiza 'cfg'.
            placeholder = "¡Hora de completar tu hábito!", // Placeholder.
            modifier = Modifier.fillMaxWidth()
        )


        /* -------------------------------- Sección: Fecha de Inicio -------------------------------------- */
        Text("Fecha de inicio del hábito:", style = MaterialTheme.typography.bodyMedium)
        // Botón que muestra la fecha actual y abre el DatePickerDialog.
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.align(Alignment.Start) // Alinea el botón a la izquierda.
        ) {
            Icon(Icons.Default.Event, contentDescription = null) // Icono de calendario.
            Spacer(Modifier.width(8.dp))
            // Muestra la fecha formateada o "Seleccionar fecha" si es null.
            Text( cfg.startsAt?.toJavaLocalDate()?.format(dateFormatter) ?: "Seleccionar fecha" )
        }

        /* -------- Sección: Modo de Notificación (Sonido/Vibración/Silencio) -------- */

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Modo de Notificación:", style = MaterialTheme.typography.bodyMedium)
        }
        InputSelect(
            options = remember { listOf("Silencioso","Sonido","Vibrar") }, // Opciones fijas.
            selectedOption = when (cfg.mode) { // Mapea el Enum a String para mostrar.
                NotifMode.SOUND   -> "Sonido"
                NotifMode.VIBRATE -> "Vibrar"
                else              -> "Silencioso"
            },
            onOptionSelected = { selectedString -> // Callback cuando se selecciona una opción.
                // Mapea el String seleccionado de vuelta al Enum y actualiza 'cfg'.
                cfg = cfg.copy(
                    mode = when (selectedString) {
                        "Sonido" -> NotifMode.SOUND
                        "Vibrar" -> NotifMode.VIBRATE
                        else     -> NotifMode.SILENT
                    }
                )
            }
        )

        /* -------- Sección: Interruptor de Vibración (Condicional) ------------------- */
        // Muestra esta fila solo si el modo es 'SOUND'.
        AnimatedVisibility(visible = cfg.mode == NotifMode.SOUND) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { cfg = cfg.copy(vibrate = !cfg.vibrate) }, // Click en la fila alterna el switch.
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Texto a la izq, switch a la der.
            ) {
                Text("Vibrar", style = MaterialTheme.typography.bodyMedium)
                SwitchToggle(
                    checked = cfg.vibrate, // Estado actual de vibración.
                    onCheckedChange = { isChecked -> cfg = cfg.copy(vibrate = isChecked) } // Actualiza 'cfg'.
                )
            }
        }

        /* -------------------------- Sección: Minutos de Antelación ---------------------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Minutos de antelación:", style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { infoMinsADlg = true }) {
                Icon(Icons.Default.Info, contentDescription = "Ayuda sobre minutos de antelación")
            }
        }
        InputText(
            // Muestra el valor si es > 0, sino cadena vacía.
            value           = cfg.advanceMin.takeIf { it > 0 }?.toString().orEmpty(),
            onValueChange   = { // Callback al cambiar el texto.
                // Convierte a Int o usa 0 si la conversión falla o es vacío. Actualiza 'cfg'.
                cfg = cfg.copy(advanceMin = it.toIntOrNull() ?: 0)
            },
            placeholder     = "0", // Placeholder para indicar 0 minutos.
            // Configura el teclado numérico.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier        = Modifier.width(120.dp) // Ancho fijo para el campo.
        )
    }

    //Botón de ayuda al lado del botón añadir hora
    if (infoTimeDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Horas de notificación",
            message  = "Puedes ingresar más de una notificación al día.",
            primaryButton = DialogButton("Entendido") {
                infoTimeDlg = false
            }
        )
    }

    if (infoMsgDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Mensaje de notificación",
            message  = "Este mensaje se mostrará en la notificación del hábito.",
            primaryButton = DialogButton("Entendido") {
                infoMsgDlg = false
            }
        )
    }


    if (infoMinsADlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Minutos de antelación",
            message  = "Si necesitas tiempo para prepararte antes de iniciar este hábito, ingresa cuantos minutos necesitas.",
            primaryButton = DialogButton("Entendido") {
                infoMinsADlg = false
            }
        )
    }

    /* -------- Diálogo TimePicker en AlertDialog -------- */
    // Muestra el AlertDialog con el TimePicker si showTimePicker es true.
    if (showTimePicker) {
        val tpState = rememberTimePickerState(is24Hour = true) // Estado para el TimePicker (formato 24h).
        AlertDialog(
            onDismissRequest = { showTimePicker = false }, // Cierra el diálogo si se pulsa fuera o botón atrás.
            title   = { Text("Selecciona hora") },
            text    = { TimePicker(state = tpState) }, // El selector de hora.
            confirmButton = { // Botón Aceptar
                TextButton(onClick = {
                    // Formatea la hora seleccionada como "HH:mm".
                    val hhmm = "%02d:%02d".format(tpState.hour, tpState.minute)
                    // Actualiza el estado 'cfg', añadiendo la nueva hora a la lista (evitando duplicados y ordenando).
                    cfg = cfg.copy(timesOfDay = (cfg.timesOfDay + hhmm).distinct().sorted())
                    showTimePicker = false // Cierra el diálogo.
                }) { Text("Aceptar") }
            },
            dismissButton = { // Botón Cancelar
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    /* -------- Diálogo DatePicker (Personalizado) -------- Seleccionar la fecha de inicio del hábito */
    // Muestra nuestro ModalDatePickerDialog si showDatePicker es true.
    ModalDatePickerDialog(
        visible     = showDatePicker,
        // Convierte la fecha 'startsAt' (kotlinx.datetime) a java.time.LocalDate para el DatePicker.
        // Si 'startsAt' es null, usa la fecha actual como inicial.
        initialDate = cfg.startsAt?.toJavaLocalDate() ?: LocalDate.now(),
        title       = "Fecha de inicio",
        onConfirmDate = { pickedJavaDate -> // Callback cuando se confirma o cancela el DatePicker.
            showDatePicker = false // Cierra el diálogo en cualquier caso.
            pickedJavaDate?.let { javaDate -> // Si se seleccionó una fecha (no se canceló)
                // Convierte la fecha seleccionada (java.time) de vuelta a kotlinx.datetime.
                val kxDate = javaDate.toKotlinLocalDate()
                // Actualiza el estado 'cfg' con la nueva fecha de inicio.
                cfg = cfg.copy(startsAt = kxDate)
            }
        },
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayEpochMillis = LocalDate.now().toEpochDay() * 86_400_000L
                return utcTimeMillis >= todayEpochMillis
            }
        }
    )
}