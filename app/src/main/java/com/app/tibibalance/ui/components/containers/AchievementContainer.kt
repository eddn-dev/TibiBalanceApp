/**
 * @file    AchievementContainer.kt
 * @ingroup ui_components
 * @brief   Tarjeta que muestra el progreso de un logro.
 *
 * @param icon        Composable que dibuja el icono representativo.
 * @param title       Título breve del logro.
 * @param description Descripción o meta a alcanzar.
 * @param percent     Avance en rango 0‒100 que alimenta la barra de progreso.
 * @param modifier    Modificador externo para tamaño, padding o clics.
 */
package com.app.tibibalance.ui.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.ProgressBar

@Composable
fun AchievementContainer(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    percent: Int,
    isUnlocked: Boolean = true,
    modifier: Modifier = Modifier
) {
    val alphaValue = if (isUnlocked) 1f else 0.4f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5FBFD), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .alpha(alphaValue), // Aplica opacidad general si está bloqueado
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            ProgressBar(
                percent = percent,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
