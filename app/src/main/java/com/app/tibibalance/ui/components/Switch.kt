package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme // Importar MaterialTheme si no está ya
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Un composable Switch personalizado con colores específicos y borde visible en estado apagado.
 *
 * @param checked Estado actual del switch (encendido/apagado).
 * @param onCheckedChange Lambda que se ejecuta cuando el estado cambia.
 * @param modifier Modificador a aplicar al Switch. Evita usar width/height directamente aquí.
 * @param enabled Controla si el switch está habilitado o deshabilitado.
 */
@Composable
fun SwitchToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier, // El modifier se pasa al Switch
    enabled: Boolean = true // Añadido parámetro enabled por completitud
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            // Colores personalizados para el estado 'checked' (encendido)
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF458BAE),
            checkedBorderColor = Color.Transparent, // Sin borde cuando está encendido
            checkedIconColor = Color.Transparent,

            // Colores personalizados para el estado 'unchecked' (apagado)
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFB0BEC5), // Gris claro para el riel
            // CORRECCIÓN: Se añade un color de borde visible para el estado apagado
            uncheckedBorderColor = Color(0xFF78909C), // Gris más oscuro para el borde
            uncheckedIconColor = Color.Transparent,

            // Colores para el estado deshabilitado (opcional)
            // ...
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SwitchTogglePreview() {
    MaterialTheme {
        Row(
            Modifier
                .padding(16.dp)
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var off by remember { mutableStateOf(false) }
            // El Switch apagado ahora debería mostrar el borde
            SwitchToggle(checked = off, onCheckedChange = { off = it })

            var on by remember { mutableStateOf(true) }
            // El Switch encendido no debería mostrar borde
            SwitchToggle(checked = on, onCheckedChange = { on = it })
        }
    }
}