// src/main/java/com/app/tibibalance/ui/screens/habits/ModalConfigNotification.kt
package com.app.tibibalance.ui.screens.habits

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.app.tibibalance.domain.model.NotifMode
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputSelect
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.domain.model.RepeatPreset
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api

enum class Day { L,M,MI,J,V,S,D}
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModalConfigNotification(
    initialTime: String,
    initialMessage: String,
    initialDays: Set<Day>,
    initialMode: NotifMode,
    initialRepeatPreset: RepeatPreset,
    onDismissRequest: () -> Unit,
    onSave: (
        time: String,
        message: String,
        days: Set<Day>,
        mode: NotifMode,
        vibrate: Boolean,
        repeatPreset: RepeatPreset
    ) -> Unit
) {


    var timesOfDay by remember { mutableStateOf(listOf(initialTime)) } // puedes ajustar si ya manejas una lista

    var message by remember { mutableStateOf(initialMessage) }
    var days by remember { mutableStateOf(initialDays) }
    var showTimePicker by remember { mutableStateOf(false) }
    var vibrate by remember { mutableStateOf(initialMode == NotifMode.SOUND) }


    LaunchedEffect(Unit) {
        Log.d("Modal", "Initial Days recibidos: $initialDays")
    }

    var mode by remember { mutableStateOf(initialMode) }

    var repeatPreset by remember {
        mutableStateOf(
            when (initialRepeatPreset) {
                RepeatPreset.DIARIO       -> "Diario"
                RepeatPreset.CADA_3_DIAS  -> "Cada 3 días"
                RepeatPreset.SEMANAL      -> "Semanal"
                RepeatPreset.QUINCENAL    -> "Quincenal"
                RepeatPreset.MENSUAL      -> "Mensual"
                RepeatPreset.PERSONALIZADO-> "Personalizado"
                else                      -> "No aplica"
            }
        )
    }



    ModalContainer(
        onDismissRequest = onDismissRequest,
        containerColor   = Color(0xFFDDEDF3),
        shape            = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)

        ) {
            // Título
            FormContainer(
                backgroundColor = Color(0xFFAED3E3),
                modifier = Modifier
                    .padding(bottom = 15.dp)
            ) {
                Text(
                    text = "Configurar notificaciones",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, // Ajusta el tamaño
                    maxLines = 1
                )
            }

            Text("Horas de recordatorio:", style = MaterialTheme.typography.bodyMedium)
            /**************************************************************************************************/


            timesOfDay.forEach { hhmm ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(hhmm, style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = { timesOfDay = timesOfDay - hhmm }) {
                        Text("Eliminar")
                    }
                }
            }
            OutlinedButton(onClick = { showTimePicker = true }) {
                Text("Añadir hora")
            }


            /*************************************************************************************************/
            // Mensaje
            Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
            InputText(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Días (dos columnas igualmente espaciadas)
            Text("Repetir hábito:", style = MaterialTheme.typography.bodyMedium)

            /*****************************************************************************************************/
            InputSelect(
                options = listOf("No aplica", "Diario", "Cada 3 días", "Semanal", "Quincenal", "Mensual", "Personalizado"),
                selectedOption = repeatPreset,
                onOptionSelected = { selected ->
                    repeatPreset = selected
                    if (selected != "Personalizado") days = emptySet()
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (repeatPreset == "Personalizado") {
                Column {
                    Text("Días de la semana", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val labels = listOf("L","M","X","J","V","S","D")
                        labels.forEachIndexed { idx, label ->
                            val day = Day.entries[idx] // Day.L, Day.M, ..., Day.D
                            val selected = days.contains(day)

                            FilterChip(
                                selected = selected,
                                onClick = {
                                    days = if (selected) days - day else days + day
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
            //if personalizado

            /******************************************************************************************/

            // Tipo de notificación
            Text("Modo de notificación:", style = MaterialTheme.typography.bodyMedium)
            InputSelect(
                options = listOf("Silencioso", "Sonido", "Vibrar"),
                selectedOption = when (mode) {
                    NotifMode.SOUND   -> "Sonido"
                    NotifMode.VIBRATE -> "Vibrar"
                    else              -> "Silencioso"
                },
                onOptionSelected = { selected ->
                    mode = when (selected) {
                        "Sonido" -> NotifMode.SOUND
                        "Vibrar" -> NotifMode.VIBRATE
                        else     -> NotifMode.SILENT
                    }
                }
            )

            if (mode == NotifMode.SOUND) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibrar", style = MaterialTheme.typography.bodyMedium)
                    SwitchToggle(
                        checked = vibrate,
                        onCheckedChange = { vibrate = it }
                    )
                }
            }


            //Spacer(Modifier.height(8.dp))

            // Botones
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    text = "Guardar",
                    onClick = {
                        onSave(
                            timesOfDay.firstOrNull() ?: "08:00",
                            message,
                            days,
                            mode,
                            vibrate,
                            when (repeatPreset) {
                                "Diario"        -> RepeatPreset.DIARIO
                                "Cada 3 días"   -> RepeatPreset.CADA_3_DIAS
                                "Semanal"       -> RepeatPreset.SEMANAL
                                "Quincenal"     -> RepeatPreset.QUINCENAL
                                "Mensual"       -> RepeatPreset.MENSUAL
                                "Personalizado" -> RepeatPreset.PERSONALIZADO
                                else            -> RepeatPreset.INDEFINIDO
                            }
                        )
                    },
                    container = Color(0xFF4285F4),
                    modifier = Modifier.weight(1f)

                )
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }//modal

    if (showTimePicker) {
        val tpState = rememberTimePickerState(is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    Log.d("DEBUG", "Guardando config con modo: $mode")
                    val hhmm = "%02d:%02d".format(tpState.hour, tpState.minute)
                    timesOfDay = (timesOfDay + hhmm).distinct().sorted()
                    showTimePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Selecciona hora") },
            text = {
                TimePicker(state = tpState)
            }
        )
    }
}





@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewModalConfigNotification() {
    ModalConfigNotification(
        initialTime         = "8:00 a.m.",
        initialMessage      = "¡Hora de hacer ejercicio!",
        initialDays         = setOf(Day.L, Day.M, Day.D),
        initialMode         = NotifMode.SOUND,
        initialRepeatPreset = RepeatPreset.PERSONALIZADO,
        onDismissRequest    = {},
        onSave              = { _, _, _, _ ,_,_-> }
    )
}



fun convertTo24hFormat(time12h: String): String {
    val lower = time12h.trim().lowercase()
    val isPM = lower.contains("p.m.")
    val isAM = lower.contains("a.m.")

    val time = lower.replace("a.m.", "").replace("p.m.", "").trim()
    val parts = time.split(":")
    if (parts.size != 2) return time12h // Retorna original si no es formato válido

    val hour = parts[0].toIntOrNull() ?: return time12h
    val minute = parts[1].toIntOrNull() ?: return time12h

    val hour24 = when {
        isPM && hour < 12 -> hour + 12
        isAM && hour == 12 -> 0
        else -> hour
    }

    return "%02d:%02d".format(hour24, minute)
}