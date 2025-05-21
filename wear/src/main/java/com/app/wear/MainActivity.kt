// src/main/java/com/app/wear/MainActivity.kt
package com.app.wear

import android.os.Bundle
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

/**
 * @file    MainActivity.kt
 * @brief   Actividad principal Wear OS con UI Compose Material 3 independiente.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAppScreen()
        }
    }
}

/**
 * @brief Composable raíz de la pantalla de Wear OS.
 * @details Muestra un fondo degradado, dos textos centrados y una imagen encima.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearAppScreen() {
    // Gradiente vertical de fondo
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
                text = "¡Bienvenido Tibio!",
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
