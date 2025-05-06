// ui/screens/habits/ModalEditHbit.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputSelect
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Title

@Composable
fun ModalEditHbit(
    initialName: String,
    initialDesc: String,
    initialFreq: String,
    initialCat: String,
    initialNotify: Boolean,
    onDismissRequest: () -> Unit,
    onSave: (
        name: String,
        desc: String,
        freq: String,
        cat: String,
        notify: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc) }
    var freq by remember { mutableStateOf(initialFreq) }
    var cat by remember { mutableStateOf(initialCat) }
    var notify by remember { mutableStateOf(initialNotify) }

    ModalContainer(
        onDismissRequest = onDismissRequest,
        containerColor   = Color(0xFFAED3E3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // <-- reducido de 10.dp a 8.dp
        ) {
            // Título
            Title(
                text = "Editar hábito",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Ícono centradito
            ImageContainer(
                resId              = R.drawable.addiconimage,
                contentDescription = "Icono del hábito",
                modifier           = Modifier
                    .size(70.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Campos editables
            Text("Nombre:*", style = MaterialTheme.typography.bodyMedium)
            InputText(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Descripción:", style = MaterialTheme.typography.bodyMedium)
            InputText(
                value = desc,
                onValueChange = { desc = it },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Frecuencia:*", style = MaterialTheme.typography.bodyMedium)
            InputSelect(
                options = listOf("Diario", "Semanal", "Mensual"),
                selectedOption = freq,
                onOptionSelected = { freq = it }
            )

            Text("Categoría:*", style = MaterialTheme.typography.bodyMedium)
            InputSelect(
                options = listOf("Salud", "Productividad", "Bienestar"),
                selectedOption = cat,
                onOptionSelected = { cat = it }
            )

            // Notificación con padding vertical reducido
            Row(
                modifier             = Modifier
                    .fillMaxWidth(),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Notificación:", style = MaterialTheme.typography.bodyMedium)
                SwitchToggle(
                    checked         = notify,
                    onCheckedChange = { notify = it }
                )
                val bellRes = if (notify)
                    R.drawable.iconbellswitch_on
                else
                    R.drawable.iconbellswitch_off

                ImageContainer(
                    resId              = bellRes,
                    contentDescription = "Notificación",
                    modifier           = Modifier
                        .size(80.dp)
                        .clickable { notify = !notify }
                )
            }

            // Botones
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    text = "Guardar",
                    onClick = { onSave(name, desc, freq, cat, notify) },
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewModalEditHbit() {
    ModalEditHbit(
        initialName       = "Dormir mínimo 7 horas",
        initialDesc       = "Mi descripción",
        initialFreq       = "Diario",
        initialCat        = "Salud",
        initialNotify     = true,
        onDismissRequest  = {},
        onSave            = { _,_,_,_,_ -> }
    )
}
