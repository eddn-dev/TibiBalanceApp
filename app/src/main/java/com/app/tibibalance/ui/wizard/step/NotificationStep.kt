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
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.screens.habits.Day
import com.app.tibibalance.ui.screens.habits.NotifyType
import com.app.tibibalance.ui.wizard.NotificationCfg

/**
 * Paso 2 – Configuración de notificaciones.
 * Actualiza la VM en caliente; botones de navegación los controla el wizard.
 */
@Composable
fun NotificationStep(
    title       : String,
    initialCfg  : NotificationCfg,
    onCfgChange : (NotificationCfg) -> Unit,   // VM recibe la versión viva
    onBack      : () -> Unit
) {
    var cfg by remember { mutableStateOf(initialCfg) }

    /* avisa a la VM cuando cambie algo */
    LaunchedEffect(cfg) { onCfgChange(cfg) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Title(title, Modifier.fillMaxWidth())

        /* Hora */
        Text("Hora:", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageContainer(R.drawable.iconclockimage, "Hora", Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            InputText(
                value         = cfg.time,
                onValueChange = { cfg = cfg.copy(time = it) },
                placeholder   = "08:00",
                modifier      = Modifier.width(100.dp)
            )
        }

        /* Mensaje */
        Text("Mensaje:", style = MaterialTheme.typography.bodyMedium)
        InputText(
            value         = cfg.message,
            onValueChange = { cfg = cfg.copy(message = it) },
            placeholder   = "¡Hora de hacer ejercicio!",
            modifier      = Modifier.fillMaxWidth()
        )

        /* Días de la semana */
        Text("Días:", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Day.entries.forEach { d ->
                val checked = cfg.days.contains(d)
                Column(
                    modifier            = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(d.name, style = MaterialTheme.typography.bodySmall)
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            cfg = if (it) cfg.copy(days = cfg.days + d)
                            else       cfg.copy(days = cfg.days - d)
                        }
                    )
                }
            }
        }

        /* Repetición */
        Text("Repetir cada:", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            InputText(
                value         = cfg.repeatValue,
                onValueChange = { cfg = cfg.copy(repeatValue = it) },
                placeholder   = "5",
                modifier      = Modifier.width(60.dp)
            )
            Spacer(Modifier.width(8.dp))
            InputText(
                value         = cfg.repeatUnit,
                onValueChange = { cfg = cfg.copy(repeatUnit = it) },
                placeholder   = "minutos",
                modifier      = Modifier.width(100.dp)
            )
        }

        /* Tipo de notificación */
        Text("Tipo de notificación:", style = MaterialTheme.typography.bodyMedium)
        NotifyType.entries.chunked(2).forEach { group ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                group.forEach { t ->
                    val checked = cfg.types.contains(t)
                    Row(
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                cfg = if (it) cfg.copy(types = cfg.types + t)
                                else       cfg.copy(types = cfg.types - t)
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
}

/* vista previa opcional */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewNotificationStep() {
    NotificationStep(
        title       = "Ejercicio",
        initialCfg  = NotificationCfg(),
        onCfgChange = {},
        onBack      = {}
    )
}
