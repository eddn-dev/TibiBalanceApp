// ui/screens/habits/ShowHabitsScreen.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add


@Composable
fun ShowHabitsScreen() {
    // gradiente idéntico al HomeScreen
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF3EA8FE).copy(alpha = .25f),
            Color.White
        )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // título con fondo redondeado 15dp y color #85C3DE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF85C3DE),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Title(
                    text      = "Mis hábitos",
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // sección Salud
            Subtitle(text = "Salud")
            HabitItem(
                iconRes         = R.drawable.iconwaterimage,
                label           = "Beber 2 litros \nde agua al día",
                checked         = remember { mutableStateOf(false) }.value,
                onCheckedChange = {}, // conecta tu lógica
                onEdit          = {}
            )
            HabitItem(
                iconRes         = R.drawable.iconsleepimage,
                label           = "Dormir mínimo 7 horas",
                checked         = remember { mutableStateOf(false) }.value,
                onCheckedChange = {},
                onEdit          = {}
            )

            // sección Productividad
            Subtitle(text = "Productividad")
            HabitItem(
                iconRes         = R.drawable.iconbookimage,
                label           = "Leer 20 páginas \n de un libro",
                checked         = remember { mutableStateOf(false) }.value,
                onCheckedChange = {},
                onEdit          = {}
            )

            // sección Bienestar
            Subtitle(text = "Bienestar")
            HabitItem(
                iconRes         = R.drawable.iconmeditationimage,
                label           = "Meditar 5 minutos \nal despertar",
                checked         = remember { mutableStateOf(false) }.value,
                onCheckedChange = {},
                onEdit          = {}
            )

            // botón "+" centrado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RoundedIconButton(
                    onClick            = { /* añadir hábito */ },
                    icon               = Icons.Default.Add,
                    contentDescription = "Agregar hábito",
                    backgroundColor    = Color(0xFF3EA8FE),
                    iconTint           = Color.White
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewShowHabitsScreen() {
    ShowHabitsScreen()
}
