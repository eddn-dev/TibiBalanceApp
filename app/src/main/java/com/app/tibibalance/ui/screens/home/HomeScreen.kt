/**
 * @file    HomeScreen.kt
 * @ingroup ui_screens_home
 * @brief   Composable principal para la pantalla de inicio (Home/Dashboard) de la aplicación.
 *
 * @details
 * Esta pantalla actúa como el panel central para el usuario después de iniciar sesión.
 * Presenta un resumen de su actividad y progreso en diferentes métricas de bienestar.
 *
 * Componentes y Secciones Principales:
 * - **Encabezado:** Muestra el título "Resumen de Hoy" y un botón secundario para acceder al "Historial".
 * - **Métricas Diarias:** Presenta tarjetas ([AchievementContainer]) para:
 *   - Pasos dados.
 *   - Minutos de ejercicio.
 *   - Calorías quemadas (Kcal).
 *   - Frecuencia cardíaca (bpm).
 *   Cada tarjeta muestra un icono, el valor numérico y una barra de progreso indicando el avance hacia una meta (implícita).
 * - **Meta de Pasos:** Sección que visualiza el progreso actual de pasos del usuario
 *   contra una meta diaria (ej. 8,000 pasos), utilizando una barra de progreso lineal personalizada.
 * - **Actividad Reciente:** Lista las últimas actividades o hábitos registrados por el usuario
 *   (e.g., "Caminata rápida", "Yoga"), mostrando detalles como duración y hora, utilizando [HabitContainer].
 *
 * La pantalla utiliza un fondo degradado consistente con otras partes de la aplicación y
 * organiza su contenido en una [Column] desplazable verticalmente. Los datos mostrados
 * se obtienen dinámicamente desde el [HomeViewModel].
 *
 * @see AchievementContainer Componente reutilizable para mostrar métricas/logros con progreso.
 * @see HabitContainer Componente reutilizable para mostrar filas de hábitos/actividades.
 * @see HomeViewModel ViewModel que provee las métricas en tiempo real.
 */
package com.app.tibibalance.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.containers.AchievementContainer
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title


/**
 * @brief Composable que muestra el panel principal con métricas y actividad reciente.
 *
 * @param viewModel Instancia de [HomeViewModel] inyectada con Hilt.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Recolecta los estados desde el ViewModel
    val steps by viewModel.steps.collectAsState()
    val calories by viewModel.calories.collectAsState()
    val exercise by viewModel.exerciseMinutes.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()

    // Gradiente de fondo
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Encabezado
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Title(text = "Resumen de Hoy", modifier = Modifier.weight(1f))
                SecondaryButton(
                    text = "Historial",
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(width = 120.dp, height = 40.dp)
                )
            }

            // Métricas diarias
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AchievementContainer(
                        icon = { ImageContainer(R.drawable.iconstepsimage, "Pasos", Modifier.size(40.dp)) },
                        title = "$steps",
                        description = "Pasos",
                        percent = (steps * 100 / 8000).toInt().coerceIn(0, 100),
                        modifier = Modifier.weight(1f)
                    )
                    AchievementContainer(
                        icon = { ImageContainer(R.drawable.iconclockimage, "Ejercicio", Modifier.size(40.dp)) },
                        title = "$exercise",
                        description = "min ejercicio",
                        percent = (exercise * 100 / 60).toInt().coerceIn(0, 100),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AchievementContainer(
                        icon = { ImageContainer(R.drawable.iconfireimage, "Calorías", Modifier.size(40.dp)) },
                        title = String.format(Locale.getDefault(), "%.0f", calories),
                        description = "Kcal",
                        percent = (calories * 100 / 2000).toInt().coerceIn(0, 100),
                        modifier = Modifier.weight(1f)
                    )
                    AchievementContainer(
                        icon = { ImageContainer(R.drawable.iconheartimage, "FC", Modifier.size(40.dp)) },
                        title = String.format(Locale.getDefault(), "%.0f", heartRate),
                        description = "bpm",
                        percent = heartRate.toInt().coerceIn(0, 100),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Meta de pasos
            Title(text = "Meta de pasos")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5FBFD),
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
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                    ) {
                        val progress = (steps / 8000f).coerceIn(0f, 1f)
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(Color(0xFF3EA8FE), RoundedCornerShape(6.dp))
                        )
                    }
                    Description(
                        text = String.format(Locale.getDefault(), "%d / %,d", steps, 8000),

                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Actividad reciente (estática)
            Title(text = "Actividad Reciente")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HabitContainer(
                    icon = { ImageContainer(R.drawable.iconwalkingimage, "Caminata", Modifier.size(32.dp)) },
                    text = "Caminata rápida • 20 min • 10 a.m.",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO */ }
                )
                HabitContainer(
                    icon = { ImageContainer(R.drawable.iconyogaimage, "Yoga", Modifier.size(32.dp)) },
                    text = "Yoga • 30 min • 7:30 a.m.",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}
