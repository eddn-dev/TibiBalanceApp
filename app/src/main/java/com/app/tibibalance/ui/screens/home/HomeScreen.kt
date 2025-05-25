/**
 * @file    HomeScreen.kt
 * @ingroup ui_screens_home
 * @brief   Composable principal para la pantalla de inicio (Home/Dashboard) de la aplicación.
 *
 * @details
 * Esta pantalla representa el panel central del usuario, mostrando métricas, consejos,
 * actividades recientes y estados relacionados con el uso del reloj inteligente.
 *
 * Comportamiento Condicional:
 * - Si **no hay conexión con el reloj**, se muestra una sección informativa (`HomeTipsSection`)
 *   invitando al usuario a vincularlo, junto con un consejo diario.
 * - Si **sí hay conexión con el reloj**, se muestra el resumen completo con métricas, progreso
 *   hacia metas y actividades recientes.
 *
 * @param isWatchConnected Booleano que indica si el reloj ya está vinculado.
 * @param onNavigateToConnectedDevices Callback que redirige a la pantalla de conexión de dispositivos.
 * @param onDismissTipsModal Callback opcional para futuras mejoras (modal legacy).
 *
 * @see HomeTipsSection Componente con tips y botón para conectar el reloj.
 * @see AchievementContainer Tarjeta para mostrar métricas y progreso.
 * @see HabitContainer Contenedor para mostrar hábitos o actividades recientes.
 * @see Title, Description, Subtitle Componentes tipográficos reutilizables.
 * @see SecondaryButton Botón utilizado para acceder al historial.
 * @see ImageContainer Contenedor de íconos usado en tarjetas.
 */
package com.app.tibibalance.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.HomeTipsSection
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.containers.AchievementContainer
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title

@Composable
fun HomeScreen(
    isWatchConnected: Boolean,
    onNavigateToConnectedDevices: () -> Unit,
    onDismissTipsModal: () -> Unit = {}
) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 🟡 Mostrar sección informativa si el reloj no está vinculado
            if (!isWatchConnected) {
                HomeTipsSection(
                    onConnectClick = onNavigateToConnectedDevices
                )
            } else {
                /* ---------- Encabezado ---------- */
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
                        onClick = { /* TODO: Implementar navegación al historial */ },
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp)
                    )
                }

                /* ---------- Sección de métricas ---------- */
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AchievementContainer(
                            icon = {
                                ImageContainer(
                                    resId = R.drawable.iconstepsimage,
                                    contentDescription = "Pasos",
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            title = "5,230",
                            description = "Pasos",
                            percent = 65,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementContainer(
                            icon = {
                                ImageContainer(
                                    resId = R.drawable.iconclockimage,
                                    contentDescription = "Minutos de ejercicio",
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            title = "35",
                            description = "min de ejercicio",
                            percent = 70,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AchievementContainer(
                            icon = {
                                ImageContainer(
                                    resId = R.drawable.iconfireimage,
                                    contentDescription = "Calorías quemadas",
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            title = "450",
                            description = "Kcal quemadas",
                            percent = 55,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementContainer(
                            icon = {
                                ImageContainer(
                                    resId = R.drawable.iconheartimage,
                                    contentDescription = "Frecuencia cardiaca",
                                    modifier = Modifier.size(40.dp)
                                )
                            },
                            title = "78",
                            description = "bpm",
                            percent = 78,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                /* ---------- Meta de pasos ---------- */
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
                            text = "5,230 / 8,000",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                /* ---------- Actividad reciente ---------- */
                Title(text = "Actividad Reciente")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitContainer(
                        icon = {
                            ImageContainer(
                                resId = R.drawable.iconwalkingimage,
                                contentDescription = "Caminata rápida",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        text = "Caminata rápida • 20 min • 10 a.m.",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* TODO: Detalle de actividad */ }
                    )
                    HabitContainer(
                        icon = {
                            ImageContainer(
                                resId = R.drawable.iconyogaimage,
                                contentDescription = "Yoga",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        text = "Yoga • 30 min • 7:30 a.m.",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* TODO: Detalle de actividad */ }
                    )
                }
            }
        }
    }
}
