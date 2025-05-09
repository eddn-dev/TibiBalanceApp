/**
 * @file    SwitchToggle.kt
 * @ingroup ui_component_input // Grupo para componentes de entrada o controles
 * @brief   Define un [Composable] para un interruptor (Switch) con una apariencia personalizada.
 *
 * @details Este archivo contiene el [Composable] `SwitchToggle`, que es una personalización
 * del componente [Switch] de Material 3. Se le aplican colores específicos para
 * los estados de encendido (`checked`) y apagado (`unchecked`), incluyendo un borde
 * visible en el estado apagado para mejorar la claridad visual.
 *
 * La intención es proporcionar un interruptor con una estética coherente a través
 * de la aplicación, diferenciándose sutilmente del `Switch` estándar de Material 3
 * si fuera necesario, o simplemente para encapsular una configuración de colores
 * preferida.
 *
 * @see androidx.compose.material3.Switch Componente base de Material 3 utilizado.
 * @see androidx.compose.material3.SwitchDefaults Usado para configurar los colores del Switch.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme // Importar MaterialTheme si no está ya
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que renderiza un [Switch] de Material 3 con colores personalizados.
 *
 * @details Este interruptor utiliza [SwitchDefaults.colors] para definir una apariencia
 * específica tanto para el estado encendido (`checked`) como para el apagado (`unchecked`).
 * Notablemente, en el estado apagado, se configura un `uncheckedBorderColor` visible
 * para mejorar la distinción visual del componente.
 *
 * @param checked El estado booleano actual del interruptor (`true` si está encendido, `false` si está apagado).
 * @param onCheckedChange Una función lambda que se invoca cuando el usuario interactúa con el
 * interruptor, cambiando su estado. Recibe el nuevo estado booleano como parámetro.
 * @param modifier Un [Modifier] opcional para aplicar al componente [Switch].
 * @param enabled Un [Boolean] que controla si el interruptor está habilitado para la interacción.
 * Por defecto es `true`.
 */
@Composable
fun SwitchToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier, // El modifier se pasa al Switch
    enabled: Boolean = true // Habilitado por defecto
) {
    Switch(
        checked = checked, // Estado actual del interruptor
        onCheckedChange = onCheckedChange, // Callback para cambios de estado
        modifier = modifier, // Aplica el modificador externo
        enabled = enabled, // Controla si es interactivo
        colors = SwitchDefaults.colors( // Configuración de colores personalizados
            // Colores para el estado 'checked' (encendido)
            checkedThumbColor = Color.White, // Color del círculo (pulgar)
            checkedTrackColor = Color(0xFF458BAE), // Color del riel (pista)
            checkedBorderColor = Color.Transparent, // Sin borde visible cuando está encendido
            checkedIconColor = Color.Transparent, // Sin icono interno (se podría añadir uno)

            // Colores para el estado 'unchecked' (apagado)
            uncheckedThumbColor = Color.White, // Color del círculo
            uncheckedTrackColor = Color(0xFFB0BEC5), // Gris claro para el riel
            uncheckedBorderColor = Color(0xFF78909C), // Gris más oscuro para el borde
            uncheckedIconColor = Color.Transparent, // Sin icono interno

        )
    )
}

/**
 * @brief Previsualización del [SwitchToggle] mostrando ambos estados (encendido y apagado).
 */
@Preview(showBackground = true)
@Composable
fun SwitchTogglePreview() {
    MaterialTheme { // Envuelve para asegurar que los colores del tema estén disponibles si SwitchDefaults los usa implícitamente
        Row(
            Modifier
                .padding(16.dp)
                .wrapContentWidth(), // Ajusta el ancho al contenido de la fila
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre los interruptores
        ) {
            // Estado para el primer interruptor (apagado)
            var off by remember { mutableStateOf(false) }
            // El Switch apagado ahora debería mostrar el borde configurado
            SwitchToggle(checked = off, onCheckedChange = { off = it }, enabled = true)

            // Estado para el segundo interruptor (encendido)
            var on by remember { mutableStateOf(true) }
            // El Switch encendido no debería mostrar borde (checkedBorderColor = Transparent)
            SwitchToggle(checked = on, onCheckedChange = { on = it }, enabled = true)

            // Ejemplo de un Switch deshabilitado y apagado
            SwitchToggle(checked = false, onCheckedChange = {}, enabled = false)

            // Ejemplo de un Switch deshabilitado y encendido
            SwitchToggle(checked = true, onCheckedChange = {}, enabled = false)
        }
    }
}