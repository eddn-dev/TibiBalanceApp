// ui/screens/home/HomeScreen.kt
package com.app.tibibalance.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAED3E3).copy(alpha = 0.45f))  // AED3E3 @45%
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ─── Encabezado ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment   = Alignment.CenterVertically
        ) {
            Title(
                text     = "Resumen de Hoy",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            SecondaryButton(
                text     = "Historial",
                onClick  = { /*…*/ },
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            )
        }

        // ─── Métricas ─────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementContainer(
                    icon = {
                        ImageContainer(
                            resId              = R.drawable.iconstepsimage,
                            contentDescription = "Pasos",
                            modifier           = Modifier.size(40.dp)
                        )
                    },
                    title       = "5,230",
                    description = "Pasos",
                    percent     = 65,
                    modifier    = Modifier.weight(1f)
                )
                AchievementContainer(
                    icon = {
                        ImageContainer(
                            resId              = R.drawable.iconclockimage,
                            contentDescription = "Minutos de ejercicio",
                            modifier           = Modifier.size(40.dp)
                        )
                    },
                    title       = "35",
                    description = "min de ejercicio",
                    percent     = 70,
                    modifier    = Modifier.weight(1f)
                )
            }
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementContainer(
                    icon = {
                        ImageContainer(
                            resId              = R.drawable.iconfireimage,
                            contentDescription = "Calorías quemadas",
                            modifier           = Modifier.size(40.dp)
                        )
                    },
                    title       = "450",
                    description = "Kcal quemadas",
                    percent     = 55,
                    modifier    = Modifier.weight(1f)
                )
                AchievementContainer(
                    icon = {
                        ImageContainer(
                            resId              = R.drawable.iconheartimage,
                            contentDescription = "Frecuencia cardiaca",
                            modifier           = Modifier.size(40.dp)
                        )
                    },
                    title       = "78",
                    description = "bpm",
                    percent     = 78,
                    modifier    = Modifier.weight(1f)
                )
            }
        }

        // ─── Meta de pasos ────────────────────────────────────────
        Title(text = "Meta de pasos")
        Surface(
            shape    = RoundedCornerShape(16.dp),
            color    = Color(0xFFF5FBFD),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(5230f / 8000f)
                            .background(
                                color = Color(0xFF3EA8FE),
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
                Description(
                    text      = "5,230 / 8,000",
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // ─── Actividad Reciente ──────────────────────────────────
        Title(text = "Actividad Reciente")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitContainer(
                icon = {
                    ImageContainer(
                        resId              = R.drawable.iconwalkingimage,
                        contentDescription = "Caminata rápida",
                        modifier           = Modifier.size(32.dp)
                    )
                },
                text     = "Caminata rápida • 20 min • 10 a.m.",
                modifier = Modifier.fillMaxWidth(),
                onClick  = { /*…*/ }
            )
            HabitContainer(
                icon = {
                    ImageContainer(
                        resId              = R.drawable.iconyogaimage,
                        contentDescription = "Yoga",
                        modifier           = Modifier.size(32.dp)
                    )
                },
                text     = "Yoga • 30 min • 7:30 a.m.",
                modifier = Modifier.fillMaxWidth(),
                onClick  = { /*…*/ }
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp  = 412,
    heightDp = 775,
    name     = "HomeScreen 412x775"
)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
