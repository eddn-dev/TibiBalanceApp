// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceScreen.kt
package com.app.tibibalance.ui.screens.conection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.domain.model.DailyMetrics

/**
 * Muestra un spinner, un mensaje de error, un aviso de desconexión,
 * invitación a recargar o las métricas según el estado.
 *
 * @param metrics Datos de métricas o null.
 * @param isConnected True si el Wear OS está disponible.
 * @param isLoading   True mientras carga.
 * @param isError     True si hubo error.
 * @param onRefreshClick Callback para refrescar conexión + métricas.
 */
@Composable
fun ConnectedDeviceScreen(
    metrics: DailyMetrics?,
    isConnected: Boolean,
    isLoading: Boolean,
    isError: Boolean,
    onRefreshClick: () -> Unit
) {
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            isError -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Title(text = "Error al cargar métricas")
                    Spacer(Modifier.height(8.dp))
                    Text("No se pudieron obtener los datos.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(text = "Reintentar", onClick = onRefreshClick)
                }
            }
            !isConnected -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Title(text = "No Conectado")
                    Spacer(Modifier.height(8.dp))
                    Text("Activa y acerca tu reloj.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(text = "Reintentar", onClick = onRefreshClick)
                }
            }
            metrics == null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Title(text = "Aún no hay métricas")
                    Spacer(Modifier.height(8.dp))
                    Text("Pulsa “Refrescar” para obtener datos.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(text = "Refrescar", onClick = onRefreshClick)
                }
            }
            else -> {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFAED3E3)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Title(text = "Dispositivo Conectado")
                        Text("Fecha: ${metrics.date}")
                        Text("Pasos: ${metrics.steps}")
                        Text("Calorías activas: ${metrics.activeCalories}")
                        Text("Minutos ejercicio: ${metrics.exerciseMinutes}")
                        Text("FC avg: ${metrics.avgHeartRate ?: "--"}")
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(text = "Refrescar", onClick = onRefreshClick)
                    }
                }
            }
        }
    }
}
