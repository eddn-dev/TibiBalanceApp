// wear/src/main/java/com/app/wear/WearAppScreen.kt
package com.app.wear

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.wear.components.PrimaryButton

/**
 * @brief Composable raíz de la pantalla de Wear OS.
 * @param onSendMetrics Callback que se invoca al pulsar “Enviar métricas”.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearAppScreen(onSendMetrics: () -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tibiowatchimage),
                contentDescription = "Icono de reloj",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            PrimaryButton(
                text = "Enviar métricas",
                onClick = onSendMetrics,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(40.dp)
            )
        }
    }
}

/** @brief Preview de [WearAppScreen] sin acción real. */
@Preview(
    showBackground = true,
    widthDp = 200, heightDp = 200
)
@Composable
fun WearAppScreenPreview() {
    WearAppScreen(onSendMetrics = { /* nada */ })
}
