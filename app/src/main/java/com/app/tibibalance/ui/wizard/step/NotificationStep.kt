package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.*
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.screens.habits.Day
import com.app.tibibalance.ui.screens.habits.NotifyType

/**
 * Paso 2 — Configuración de notificaciones.
 * Propaga `NotifConfig` en caliente a la VM.
 */
@Composable
fun NotificationStep(
    title       : String,
    initialCfg  : NotifConfig,
    onCfgChange : (NotifConfig) -> Unit,
    onBack      : () -> Unit
) {
    /* Sincroniza cambios descendentes (p. ej. pickTemplate) */
    var cfg by remember(initialCfg) { mutableStateOf(initialCfg) }

    /* Propaga a la VM cada modificación local */
    LaunchedEffect(cfg) { onCfgChange(cfg) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Title(title, Modifier.fillMaxWidth())

        /* ---------- Hora ---------- */
        Text("Hora:", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageContainer(R.drawable.iconclockimage, "Hora", Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            InputText(
                value = cfg.timesOfDay.firstOrNull().orEmpty(),
                onValueChange = { hhmm ->
                    cfg = cfg.copy(timesOfDay = if (hhmm.isBlank()) emptyList() else listOf(hhmm))
                },
                placeholder = "08:00",
                modifier = Modifier.width(100.dp)
            )
        }

        /* ---------- Mensaje ---------- */
        Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
        InputText(
            value = cfg.message,
            onValueChange = { cfg = cfg.copy(message = it) },
            placeholder = "¡Hora de hacer ejercicio!",
            modifier = Modifier.fillMaxWidth()
        )

        /* ---------- Días de la semana ---------- */
        Text("Días:", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Day.entries.forEach { d ->
                val checked = cfg.daysOfWeek.contains(d.ordinal + 1)   // DayOfWeek ord = 1..7
                Column(
                    modifier            = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(d.name.take(3), style = MaterialTheme.typography.bodySmall)
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            cfg = if (isChecked)
                                cfg.copy(daysOfWeek = cfg.daysOfWeek + (d.ordinal + 1))
                            else
                                cfg.copy(daysOfWeek = cfg.daysOfWeek - (d.ordinal + 1))
                        }
                    )
                }
            }
        }

        /* ---------- Antelación (min) ---------- */
        Text("Avisar con antelación (min):", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            InputText(
                value = cfg.advanceMinutes.toString(),
                onValueChange = { txt ->
                    val v = txt.toIntOrNull() ?: 0
                    cfg = cfg.copy(advanceMinutes = v.coerceAtLeast(0))
                },
                placeholder = "0",
                modifier = Modifier.width(80.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("minutos antes", style = MaterialTheme.typography.bodySmall)
        }

        /* ---------- Tipo de notificación ---------- */
        Text("Tipo de notificación:", style = MaterialTheme.typography.bodyMedium)
        NotifyType.entries.chunked(2).forEach { group ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                group.forEach { t ->
                    val checked = when (t) {
                        NotifyType.SILENT  -> cfg.mode == NotifMode.SILENT
                        NotifyType.SOUND   -> cfg.mode == NotifMode.SOUND
                        NotifyType.VIBRATE -> cfg.vibrate
                        NotifyType.EMERGENT -> false         // aún no soportado
                    }
                    Row(
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                cfg = when (t) {
                                    NotifyType.SILENT, NotifyType.SOUND -> {
                                        val newMode = if (isChecked)
                                            if (t == NotifyType.SILENT) NotifMode.SILENT
                                            else                       NotifMode.SOUND
                                        else NotifMode.SILENT          // fallback
                                        cfg.copy(mode = newMode)
                                    }
                                    NotifyType.VIBRATE -> cfg.copy(vibrate = isChecked)
                                    NotifyType.EMERGENT -> cfg          // placeholder
                                }
                            }
                        )
                        Text(
                            text = when (t) {
                                NotifyType.SILENT   -> "Silenciosa"
                                NotifyType.SOUND    -> "Con sonido"
                                NotifyType.VIBRATE  -> "Vibración"
                                NotifyType.EMERGENT -> "Emergente"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/* ----------- Preview ----------- */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewNotificationStep() {
    NotificationStep(
        title       = "Ejercicio",
        initialCfg  = NotifConfig(),
        onCfgChange = {},
        onBack      = {}
    )
}
