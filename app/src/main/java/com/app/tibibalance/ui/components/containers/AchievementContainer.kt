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


import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource


@Composable
fun AchievementContainer(
    iconRes: Int,
    title: String,
    description: String,
    percent: Int,
    isUnlocked: Boolean = true,
    modifier: Modifier = Modifier
) {
    val alphaValue = if (isUnlocked) 1f else 0.4f
    val colorMatrix = if (isUnlocked) ColorMatrix() else ColorMatrix().apply { setToSaturation(0f) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5FBFD), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp) // reduce padding
            .alpha(alphaValue),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp), // más pequeño
            colorFilter = ColorFilter.colorMatrix(colorMatrix)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp) // espacio compacto entre elementos
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                maxLines = 1
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2
            )

            ProgressBar(
                percent = percent,
                modifier = Modifier.padding(top = 4.dp)
            )

            /*if (!isUnlocked) {
                Text(
                    text = "No desbloqueado",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }*/
        }
    }
}

