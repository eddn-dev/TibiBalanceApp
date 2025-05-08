/**
 * @file    HabitContainer.kt
 * @ingroup ui_components
 * @brief   Tarjeta clicable que representa un hábito en listas o grids.
 *
 * @param icon     Composable que muestra el icono del hábito.
 * @param text     Nombre o descripción corta del hábito.
 * @param modifier Modificador externo para tamaño, padding o margen.
 * @param onClick  Callback invocado al pulsar la tarjeta.
 */
package com.app.tibibalance.ui.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HabitContainer(
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color(0xFFF5FBFD), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
