// src/main/java/com/app/wear/MainActivity.kt
package com.app.wear

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.wear.model.DailyMetrics
import com.google.android.gms.wearable.Wearable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

/**
 * @file    MainActivity.kt
 * @brief   Actividad principal Wear OS con UI Compose Material 3 independiente.
 *
 * @details
 * - Muestra la pantalla de bienvenida en el reloj.
 * - Incluye botón para enviar métricas diarias al teléfono emparejado.
 * - El envío se dispara solo cuando el usuario pulsa el botón.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Monta la UI y pasa la función onSendMetrics al composable
        setContent {
            WearAppScreen(
                onSendMetrics = {
                    // Lógica de creación y envío de métricas
                    val today = LocalDate.now().toString()
                    val metrics = DailyMetrics(
                        date = today,
                        steps = 0L,              // TODO: sustituir por lectura real
                        activeCalories = 0.0,    // TODO: sustituir por lectura real
                        exerciseMinutes = 0L,    // TODO: sustituir por lectura real
                        avgHeartRate = null      // TODO: sustituir por lectura real
                    )
                    sendMetricsToPhone(metrics)
                }
            )
        }
    }

    /**
     * @brief Envía las métricas diarias desde el módulo Wear OS al teléfono.
     * @param metrics Objeto DailyMetrics con los datos de salud del día.
     *
     * - Serializa el objeto a JSON.
     * - Obtiene los nodos conectados.
     * - Envía un mensaje a cada nodo emparejado en el path "/daily-metrics".
     */
    private fun sendMetricsToPhone(metrics: DailyMetrics) {
        val payload = Json.encodeToString(metrics).toByteArray()

        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    Log.w("WearOS", "No hay nodos conectados; no se envía nada")
                    return@addOnSuccessListener
                }
                for (node in nodes) {
                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, "/daily-metrics", payload)
                        .addOnSuccessListener {
                            Log.i("WearOS", "Métricas enviadas a ${node.displayName}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("WearOS", "Error al enviar métricas a ${node.displayName}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("WearOS", "Error al obtener nodos conectados", e)
            }
    }
}
