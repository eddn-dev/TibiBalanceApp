/* ui/wizard/step/NotificationStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.NotifConfig
import com.app.tibibalance.domain.model.NotifMode
import com.app.tibibalance.ui.components.modals.ModalDatePickerDialog
import com.app.tibibalance.ui.components.SwitchToggle
import com.app.tibibalance.ui.components.inputs.InputSelect
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Title
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.datetime.toKotlinLocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationStep(
    title       : String,
    initialCfg  : NotifConfig,
    onCfgChange : (NotifConfig) -> Unit,
    onBack      : () -> Unit = {}
) {
    /* -------- estados -------- */
    var cfg by remember { mutableStateOf(initialCfg) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }   // NUEVO
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy") }

    /* Propaga cambios hacia el VM */
    LaunchedEffect(cfg) { onCfgChange(cfg) }

    /* -------- TimePicker en AlertDialog -------- */
    if (showTimePicker) {
        val tpState = rememberTimePickerState(is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title   = { Text("Selecciona hora") },
            text    = { TimePicker(state = tpState) },
            confirmButton = {
                TextButton(onClick = {
                    val hhmm = "%02d:%02d".format(tpState.hour, tpState.minute)
                    cfg = cfg.copy(timesOfDay = (cfg.timesOfDay + hhmm).distinct().sorted())
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    /* -------- DatePicker con nuestro nuevo diálogo -------- */
    ModalDatePickerDialog(
        visible     = showDatePicker,
        // convierte startsAt (kx) → java.time para el DatePicker
        initialDate = cfg.startsAt?.toJavaLocalDate() ?: LocalDate.now(),
        title       = "Fecha de inicio",
        onConfirmDate = { picked ->
            showDatePicker = false
            picked?.let { javaDate ->
                // java.time.LocalDate ➜ kotlinx.datetime.LocalDate
                val kxDate = javaDate.toKotlinLocalDate()
                cfg = cfg.copy(startsAt = kxDate)
            }
        }
    )


    /* -------- Contenido scrollable -------- */
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Title(title, Modifier.fillMaxWidth())

        /* Horas --------------------------------------------------------- */
        Text("Horas de recordatorio:", style = MaterialTheme.typography.bodyMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            cfg.timesOfDay.forEach { hhmm ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(hhmm, style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = {
                        cfg = cfg.copy(timesOfDay = cfg.timesOfDay - hhmm)
                    }) { Text("Eliminar") }
                }
            }
            OutlinedButton(onClick = { showTimePicker = true }) { Text("Añadir hora") }
        }

        /* Mensaje ------------------------------------------------------- */
        Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
        InputText(
            value         = cfg.message,
            onValueChange = { cfg = cfg.copy(message = it) },
            placeholder   = "¡Hora de completar tu hábito!",
            modifier      = Modifier.fillMaxWidth()
        )

        /* Fecha de inicio ---------------------------------------------- */
        Text("Inicia el:", style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.Event, null)
            Spacer(Modifier.width(8.dp))
            Text( cfg.startsAt?.toJavaLocalDate()?.format(dateFormatter) ?: "Seleccionar fecha" )

        }

        /* Modo (sonido / vibración) ------------------------------------ */
        InputSelect(
            label   = "Modo",
            options = listOf("Silencioso","Sonido","Vibrar"),
            selectedOption = when (cfg.mode) {
                NotifMode.SOUND   -> "Sonido"
                NotifMode.VIBRATE -> "Vibrar"
                else              -> "Silencioso"
            },
            onOptionSelected = { sel ->
                cfg = cfg.copy(
                    mode = when (sel) {
                        "Sonido" -> NotifMode.SOUND
                        "Vibrar" -> NotifMode.VIBRATE
                        else     -> NotifMode.SILENT
                    }
                )
            }
        )

        /* Vibrar solo si procede -------------------------------------- */
        AnimatedVisibility(cfg.mode == NotifMode.SOUND) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { cfg = cfg.copy(vibrate = !cfg.vibrate) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vibrar", style = MaterialTheme.typography.bodyMedium)
                SwitchToggle(
                    checked = cfg.vibrate,
                    onCheckedChange = { cfg = cfg.copy(vibrate = it) }
                )
            }
        }

        /* Antelación ---------------------------------------------------- */
        Text("Minutos de antelación:", style = MaterialTheme.typography.bodyMedium)
        InputText(
            value           = cfg.advanceMin.takeIf { it > 0 }?.toString().orEmpty(),
            onValueChange   = { cfg = cfg.copy(advanceMin = it.toIntOrNull() ?: 0) },
            placeholder     = "0",
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            modifier        = Modifier.width(120.dp)
        )
    }
}
