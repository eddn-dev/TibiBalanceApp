// src/main/java/com/app/wear/MainActivity.kt
package com.app.wear

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.DailyMetrics
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.MessageClient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @file    MainActivity.kt
 * @brief   Actividad principal Wear OS con UI Compose Material 3 independiente.
 *
 * @details
 * - Muestra la pantalla de bienvenida en el reloj.
 * - Incluye método para enviar métricas diarias al teléfono emparejado.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAppScreen()
        }

        // Ejemplo de envío automático (puedes llamarlo cuando tengas tus métricas)
        // val sampleMetrics = DailyMetrics("2025-05-21", 1000L, 200.0, 30L, 72.5)
        // sendMetricsToPhone(sampleMetrics)
    }

    /**
     * @brief Envía las métricas diarias desde el módulo Wear OS al teléfono.
     * @param metrics Objeto DailyMetrics con los datos de salud del día.
     *
     * - Serializa el objeto a JSON.
     * - Obtiene los nodos emparejados.
     * - Envía un mensaje a cada nodo por el path "/daily-metrics".
     */
    private fun sendMetricsToPhone(metrics: DailyMetrics) {
        val payload = Json.encodeToString(metrics).toByteArray()

        Wearable.getNodeClient(this).nodes
            .addOnSuccessListener { nodes ->
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
                Log.e("WearOS", "No se pudieron obtener nodos Wear", e)
            }
    }
}

/**
 * @brief Composable raíz de la pantalla de Wear OS.
 * @details Muestra un fondo degradado, texto de bienvenida e icono.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearAppScreen() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3EA8FE).copy(alpha = 0.45f),
            Color.White
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "¡Bienvenido a \n TibiBalance!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Image(
                painter = painterResource(id = R.drawable.tibiowatchimage),
                contentDescription = "Icono de reloj",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * @brief Preview de WearAppScreen en un lienzo circular de 200×200 dp.
 */
@Preview(
    name            = "Wear Round Preview",
    showBackground  = true,
    backgroundColor = 0xFFFFFFFF,
    device          = "spec:width=200dp,height=200dp,dpi=320"
)
@Composable
fun WearAppScreenPreview() {
    WearAppScreen()
}
