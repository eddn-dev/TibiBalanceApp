// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceScreen.kt
package com.app.tibibalance.ui.screens.conection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.texts.Title

/**
 * UI pura para mostrar estado de conexión y permitir refrescar.
 *
 * @param isConnected    Estado booleano de si el dispositivo Wear/Bluetooth está conectado.
 * @param onRefreshClick Callback para volver a comprobar la conexión.
 */
@Composable
fun ConnectedDeviceScreen(
    isConnected: Boolean,
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFAED3E3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Title(
                        text = if (isConnected)
                            "Dispositivo Conectado"
                        else
                            "No Conectado"
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = onRefreshClick) {
                Text(text = "Refrescar Estado")
            }
        }
    }
}
