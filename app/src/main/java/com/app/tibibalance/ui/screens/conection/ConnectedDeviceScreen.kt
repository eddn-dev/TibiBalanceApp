// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceScreen.kt
package com.app.tibibalance.ui.screens.conection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.domain.model.DailyMetrics

/**
 * UI compuesta para mostrar el estado de conexión y las métricas diarias.
 *
 * @param metrics Datos de métricas diarias o null si no hay conexión.
 * @param onRefreshClick Callback para volver a comprobar la conexión y recargar métricas.
 */
@Composable
fun ConnectedDeviceScreen(
    metrics: DailyMetrics?,
    onRefreshClick: () -> Unit
) {
    // Degradado de fondo
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Tarjeta de estado y métricas
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFAED3E3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Estado de conexión
                    Title(
                        text = if (metrics != null) "Dispositivo Conectado" else "No Conectado"
                    )

                    // Muestra métricas si está conectado
                    metrics?.let {
                        Text(text = "Fecha: ${it.date}")
                        Text(text = "Pasos: ${it.steps}")
                        Text(text = "Calorías activas: ${it.activeCalories}")
                        Text(text = "Minutos de ejercicio: ${it.exerciseMinutes}")
                        Text(text = "Frecuencia cardiaca avg: ${it.avgHeartRate ?: "--"}")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = "Refrescar Estado",
                onClick = onRefreshClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp)
            )
        }
    }
}

/**
 * @brief Preview de [ConnectedDeviceScreen] mostrando métricas de ejemplo.
 */
@Preview(showBackground = true)
@Composable
fun ConnectedDeviceScreenPreview_Connected() {
    ConnectedDeviceScreen(
        metrics = DailyMetrics(
            date = "2025-05-21",
            steps = 1234,
            activeCalories = 250.0,
            exerciseMinutes = 30,
            avgHeartRate = 72.5
        ),
        onRefreshClick = {}
    )
}

/**
 * @brief Preview de [ConnectedDeviceScreen] mostrando estado no conectado.
 */
@Preview(showBackground = true)
@Composable
fun ConnectedDeviceScreenPreview_NotConnected() {
    ConnectedDeviceScreen(
        metrics = null,
        onRefreshClick = {}
    )
}
