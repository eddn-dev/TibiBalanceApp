// app/src/main/java/com/app/tibibalance/ui/screens/home/HomeScreen.kt
package com.app.tibibalance.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.containers.AchievementContainer
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    // 1) Inyectamos el ViewModel de historial
    val vm: StatsHistoryViewModel = hiltViewModel()
    val weekly  by vm.weeklySeries.collectAsState()
    val monthly by vm.monthlySeries.collectAsState()

    // 2) Estado para mostrar modal
    var showModal by remember { mutableStateOf(false) }
    if (showModal) BackHandler { showModal = false }

    // 3) Obtener los datos de "hoy" para la sección principal
    val todaySteps    = weekly.steps.lastOrNull()?.toLong() ?: 0L
    val todayCals     = weekly.cals.lastOrNull()?.roundToInt() ?: 0
    val todayHR       = weekly.hr.lastOrNull()?.roundToInt() ?: 0
    val todayExercise = weekly.hr.average().roundToInt()  // como ejemplo

    // 4) Gradiente de fondo
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier             = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement   = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Title(
                    text      = "Resumen de Hoy",
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                SecondaryButton(
                    text     = "Historial",
                    onClick  = { /* TODO */ },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp)
                )
            }

            // Métricas diarias
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AchievementContainer(
                        icon        = { /* icono pasos */ },
                        title       = "$todaySteps",
                        description = "Pasos",
                        percent     = ((todaySteps * 100L) / 10_000L).toInt(),
                        modifier    = Modifier.weight(1f)
                    )
                    AchievementContainer(
                        icon        = { /* icono ejercicio */ },
                        title       = "$todayExercise min",
                        description = "Ejercicio",
                        percent     = (todayExercise * 100 / 60).coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AchievementContainer(
                        icon        = { /* icono calorías */ },
                        title       = "$todayCals",
                        description = "Kcal",
                        percent     = (todayCals * 100 / 3000).coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                    AchievementContainer(
                        icon        = { /* icono FC */ },
                        title       = "$todayHR",
                        description = "bpm",
                        percent     = (todayHR * 100 / 180).coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                }
            }

            // Meta de pasos
            Title(text = "Meta de pasos")
            Surface(
                shape    = RoundedCornerShape(16.dp),
                color    = Color(0xFFF5FBFD),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Column(
                    modifier             = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((todaySteps / 10_000f).coerceIn(0f,1f))
                                .background(Color(0xFF3EA8FE), RoundedCornerShape(6.dp))
                        )
                    }
                    Description(
                        text      = "$todaySteps / 10,000",
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Actividad reciente
            Title(text = "Actividad Reciente")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HabitContainer(
                    icon     = { /* icono */ },
                    text     = "Caminata rápida • 20 min",
                    modifier = Modifier.fillMaxWidth(),
                    onClick  = {}
                )
                HabitContainer(
                    icon     = { /* icono */ },
                    text     = "Yoga • 30 min",
                    modifier = Modifier.fillMaxWidth(),
                    onClick  = {}
                )
            }

            // Botón para mostrar modal
            PrimaryButton(
                text      = "MostrarModal",
                onClick   = { showModal = true },
                container = Color(0xFF4285F4),
                modifier  = Modifier.fillMaxWidth()
            )
        }

        // Modal
        if (showModal) {
            HealthSummaryModal(
                weekly           = weekly,
                monthly          = monthly,
                onDismissRequest = { showModal = false }
            )
        }
    }
}
