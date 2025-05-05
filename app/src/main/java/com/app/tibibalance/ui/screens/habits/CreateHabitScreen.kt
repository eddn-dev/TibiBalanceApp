// ui/screens/habits/CreateHabitScreen.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun CreateHabitScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    // mismo degradado que HomeScreen
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFAED3E3).copy(alpha = .45f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF85C3DE),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Crear mi propio hábito",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // placeholder de ícono (PNG) + texto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { /* elegir imagen */ },
                    contentAlignment = Alignment.Center
                ) {
                    ImageContainer(
                        resId              = R.drawable.addiconimage,
                        contentDescription = "Añadir ícono",
                            modifier           = Modifier.size(70.dp)
                        )
                }

                // campos
                var name by remember { mutableStateOf("") }
                Text("Nombre:*", style = MaterialTheme.typography.bodyMedium)
                InputText(
                    value          = name,
                    onValueChange  = { name = it },
                    placeholder    = "Escribe el nombre",
                    modifier       = Modifier.fillMaxWidth()
                )

                var desc by remember { mutableStateOf("") }
                Text("Descripción:", style = MaterialTheme.typography.bodyMedium)
                InputText(
                    value          = desc,
                    onValueChange  = { desc = it },
                    placeholder    = "Añade una descripción",
                    modifier       = Modifier.fillMaxWidth()
                )

                var freq by remember { mutableStateOf("Diario") }
                Text("Frecuencia:*", style = MaterialTheme.typography.bodyMedium)
                InputSelect(
                    options         = listOf("Diario", "Semanal", "Mensual"),
                    selectedOption  = freq,
                    onOptionSelected = { freq = it }
                )

                var cat by remember { mutableStateOf("Salud") }
                Text("Categoría:*", style = MaterialTheme.typography.bodyMedium)
                InputSelect(
                    options         = listOf("Salud", "Productividad", "Bienestar"),
                    selectedOption  = cat,
                    onOptionSelected = { cat = it }
                )

                // notificación con switch y campana PNG en dos estados
                var notify by remember { mutableStateOf(true) }
                Row(
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Notificación:", style = MaterialTheme.typography.bodyMedium)
                    SwitchToggle(
                        checked         = notify,
                        onCheckedChange = { notify = it }
                    )
                    // drawable cambia según estado
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

                // botones Guardar / Cancelar
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrimaryButton(
                        text         = "Guardar",
                        onClick      = onSave,
                        container    = Color.White,
                        contentColor = Color.Black,
                        modifier     = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text     = "Cancelar",
                        onClick  = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewCreateHabitScreen() {
    CreateHabitScreen(onSave = {}, onCancel = {})
}
