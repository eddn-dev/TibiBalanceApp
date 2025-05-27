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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.HealthStats
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.containers.AchievementContainer
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    // Inyectamos el ViewModel intradía
    val vm: IntradayStatsViewModel = hiltViewModel()
    val stats by vm.stats.collectAsState()

    // Inyectamos el ViewModel de historial para el modal
    val historyVm: StatsHistoryViewModel = hiltViewModel()
    val weekly by historyVm.weeklySeries.collectAsState()
    val monthly by historyVm.monthlySeries.collectAsState()

    var showModal by remember { mutableStateOf(false) }
    if (showModal) BackHandler { showModal = false }

    // Extraer valores seguros para el intradía
    val stepsToday    = stats.steps
    val caloriesToday = stats.calories.roundToInt()
    val bpmToday      = stats.heartRate.roundToInt()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Title(
                    text = "Resumen de Hoy",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                SecondaryButton(
                    text = "Historial",
                    onClick = { /* TODO: Navegar */ },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp)
                )
            }

            // Métricas intradía
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pasos
                    AchievementContainer(
                        iconRes        = R.drawable.racha, //imagen del logro
                        title       = "$stepsToday",
                        description = "Pasos",
                        percent     = ((stepsToday * 100L) / 10_000L).toInt().coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                    // Calorías
                    AchievementContainer(
                        iconRes      = R.drawable.racha,   //imagen del logro
                        title       = "$caloriesToday",
                        description = "Kcal",
                        percent     = (caloriesToday * 100 / 3000).coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Minutos de ejercicio
                    AchievementContainer(
                        iconRes        = R.drawable.racha, //imagen del logro
                        title       = "${stats.exerciseMinutes} min",
                        description = "Ejercicio",
                        percent     = ((stats.exerciseMinutes * 100) / 60).coerceIn(0, 100),
                        modifier    = Modifier.weight(1f)
                    )
                    // BPM: sólo como valor, sin barra de progreso
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$bpmToday",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "bpm",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Meta de pasos (ej. 10,000 pasos)
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
                    // Track de progreso
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                    ) {
                        // Ancho proporcional a stepsToday / meta (10,000)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((stepsToday / 10_000f).coerceIn(0f, 1f))
                                .background(Color(0xFF3EA8FE), RoundedCornerShape(6.dp))
                        )
                    }
                    Description(
                        text      = "$stepsToday / 10,000",
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Actividad reciente de ejemplo
            Title(text = "Actividad Reciente")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HabitContainer(
                    icon = { /* Icono habit */ },
                    text = "Caminata rápida • 20 min",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                )
                HabitContainer(
                    icon = { /* Icono habit */ },
                    text = "Yoga • 30 min",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                )
            }

            PrimaryButton(
                text = "MostrarModal",
                onClick = { showModal = true },
                container = Color(0xFF4285F4),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Modal de estadísticas semanales/mensuales
        if (showModal) {
            HealthSummaryModal(
                weekly = weekly,
                monthly = monthly,
                onDismissRequest = { showModal = false }
            )
        }
    }
}
