// ui/components/buttons/EmotionButton.kt
package com.app.tibibalance.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ImageContainer

/**
 * Botón que muestra una emoción (ImageContainer + texto).
 *
 * @param emotionLabel   Texto descriptivo de la emoción.
 * @param emotionRes     Drawable de la emoción.
 * @param isSelected     Si está seleccionado, se pinta un fondo semitransparente.
 * @param size           Tamaño del icono (ancho y alto).
 * @param contentPadding Padding interior alrededor del icono.
 * @param onClick        Callback al pulsar.
 * @param modifier       Modifier padre.
 */
@Composable
fun EmotionButton(
    emotionLabel: String,
    @DrawableRes emotionRes: Int,
    isSelected: Boolean = false,
    size: Dp = 48.dp,
    contentPadding: Dp = 2.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Caja circular que contiene la imagen
        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else Color.Transparent,
                    shape = CircleShape
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            ImageContainer(
                resId              = emotionRes,
                contentDescription = emotionLabel,
                modifier           = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text  = emotionLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/* — Espacio reservado para tu preview
@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
private fun PreviewEmotionButton() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    ) {
        EmotionButton(
            emotionLabel   = "Felicidad",
            emotionRes     = R.drawable.iconhappyimage,
            isSelected     = true,
            onClick        = { /* prueba */ }
        )
        EmotionButton(
            emotionLabel   = "Tristeza",
            emotionRes     = R.drawable.iconsadimage,
            isSelected     = false,
            onClick        = { /* prueba */ }
        )
    }
}
*/