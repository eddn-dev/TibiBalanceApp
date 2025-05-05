// src/main/java/com/app/tibibalance/ui/screens/habits/ModalConfigNotification.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

enum class Day { L, M, MI, J, V, S, D }
enum class NotifyType { SILENT, SOUND, VIBRATE, EMERGENT }

@Composable
fun ModalConfigNotification(
    title: String,
    initialTime: String,
    initialMessage: String,
    initialDays: Set<Day>,
    initialRepeatValue: String,
    initialRepeatUnit: String,
    initialTypes: Set<NotifyType>,
    onDismissRequest: () -> Unit,
    onSave: (
        time: String,
        message: String,
        days: Set<Day>,
        repeatValue: String,
        repeatUnit: String,
        types: Set<NotifyType>
    ) -> Unit
) {
    var time by remember { mutableStateOf(initialTime) }
    var message by remember { mutableStateOf(initialMessage) }
    var days by remember { mutableStateOf(initialDays) }
    var repeatValue by remember { mutableStateOf(initialRepeatValue) }
    var repeatUnit by remember { mutableStateOf(initialRepeatUnit) }
    var types by remember { mutableStateOf(initialTypes) }

    ModalContainer(
        onDismissRequest = onDismissRequest,
        containerColor   = Color(0xFFAED3E3),
        shape            = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Título
            Title(
                text      = title,
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Hora
            Text("Hora:", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImageContainer(
                    resId              = R.drawable.iconclockimage,
                    contentDescription = "Hora",
                    modifier           = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                InputText(
                    value         = time,
                    onValueChange = { time = it },
                    placeholder   = "08:00 a.m.",
                    modifier      = Modifier.width(100.dp)
                )
            }

            // Mensaje
            Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
            InputText(
                value         = message,
                onValueChange = { message = it },
                placeholder   = "¡Hora de hacer ejercicio!",
                modifier      = Modifier.fillMaxWidth()
            )

            // Días (dos columnas igualmente espaciadas)
            Text("Día:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Day.entries.forEach { d ->
                    val checked = days.contains(d)
                    Column(
                        modifier            = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(d.name, style = MaterialTheme.typography.bodySmall)
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                days = if (it) days + d else days - d
                            }
                        )
                    }
                }
            }

            // Repetir cada
            Text("Repetir cada", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                InputText(
                    value         = repeatValue,
                    onValueChange = { repeatValue = it },
                    placeholder   = "5",
                    modifier      = Modifier.width(60.dp)
                )
                Spacer(Modifier.width(8.dp))
                InputText(
                    value         = repeatUnit,
                    onValueChange = { repeatUnit = it },
                    placeholder   = "minutos",
                    modifier      = Modifier.width(100.dp)
                )
            }

            // Tipo de notificación
            Text("Tipo de notificación", style = MaterialTheme.typography.bodyMedium)
            val groups = NotifyType.entries.chunked(2)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groups.forEach { group ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier            = Modifier.weight(1f)
                    ) {
                        group.forEach { t ->
                            val checked = types.contains(t)
                            Row(
                                verticalAlignment    = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        types = if (it) types + t else types - t
                                    }
                                )
                                Text(
                                    text = when (t) {
                                        NotifyType.SILENT   -> "Silenciosa"
                                        NotifyType.SOUND    -> "Con sonido"
                                        NotifyType.VIBRATE  -> "Con vibración"
                                        NotifyType.EMERGENT -> "Emergente"
                                    },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botones
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    text    = "Guardar",
                    onClick = { onSave(time, message, days, repeatValue, repeatUnit, types) },
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text     = "Cancelar",
                    onClick  = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewModalConfigNotification() {
    ModalConfigNotification(
        title              = "Hacer Ejercicio",
        initialTime        = "8:00 a.m.",
        initialMessage     = "¡Hora de hacer ejercicio!",
        initialDays        = setOf(Day.L, Day.MI, Day.D),
        initialRepeatValue = "5",
        initialRepeatUnit  = "minutos",
        initialTypes       = setOf(NotifyType.SOUND),
        onDismissRequest   = {},
        onSave             = { _, _, _, _, _, _ -> }
    )
}
