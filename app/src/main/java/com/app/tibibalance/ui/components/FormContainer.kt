// ui/components/FormContainer.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Contenedor genérico para formularios (inputs, botones…).
 *
 * @param backgroundColor  Color de la tarjeta (azul pálido por defecto).
 * @param cornerRadiusDp   Radio de las esquinas.
 * @param tonalElevation   Elevación (sombra) de la tarjeta.
 * @param contentPadding   Relleno interno.
 */
@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFC8DEFA),    // ← azul suave
    cornerRadiusDp: Int = 16,
    tonalElevation: Dp = 3.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadiusDp.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = tonalElevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
