/**
 * @file    CloseIconButton.kt
 * @ingroup ui_components
 * @brief   Botón de icono que cierra el diálogo o pantalla actual.
 *
 * @param onClick  Callback ejecutado al pulsar el botón.
 * @param modifier Opcional para ajustar tamaño o padding desde la llamada.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar",
            tint = Color.Black
        )
    }
}

/** Vista previa aislada del componente. */
@Preview(showBackground = true)
@Composable
fun CloseIconButtonPreview() {
    CloseIconButton(onClick = { })
}
