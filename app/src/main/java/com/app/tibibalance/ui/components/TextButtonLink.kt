/**
 * @file    TextButtonLink.kt
 * @ingroup ui_component_button // O ui_component para componentes generales
 * @brief   Define un [Composable] para un botón de texto ([TextButton]) estilizado como un hipervínculo.
 *
 * @details Este archivo contiene el [Composable] `TextButtonLink`, que proporciona una
 * forma estandarizada de mostrar botones de texto que se asemejan visualmente a
 * enlaces web. Utiliza el componente [TextButton] de Material 3 como base y aplica
 * estilos específicos al [Text] interno:
 * - Color: Utiliza el color primario del tema (`MaterialTheme.colorScheme.primary`).
 * - Tipografía: Utiliza el estilo `bodySmall` del tema.
 * - Decoración: Aplica un subrayado ([TextDecoration.Underline]) por defecto,
 * que puede desactivarse mediante el parámetro `underline`.
 *
 * Es útil para acciones secundarias, enlaces de navegación dentro de texto, o
 * cualquier acción que se beneficie de una apariencia de enlace discreta pero interactiva.
 *
 * @see androidx.compose.material3.TextButton Componente base de Material 3.
 * @see androidx.compose.material3.Text Usado para mostrar el texto del enlace.
 * @see androidx.compose.ui.text.style.TextDecoration Usado para el subrayado.
 * @see androidx.compose.material3.MaterialTheme Usado para obtener colores y estilos de tipografía por defecto.
 */
// ui/components/TextButtonLink.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.Column // Para Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding // Para Preview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Para Preview

/**
 * @brief Un [Composable] que renderiza un [TextButton] con la apariencia de un hipervínculo.
 *
 * @details Aplica el color primario del tema y el estilo de texto `bodySmall`.
 * Por defecto, el texto está subrayado, pero se puede desactivar con el parámetro `underline`.
 *
 * @param text El [String] que se mostrará como el texto del enlace/botón.
 * @param onClick La función lambda que se ejecutará cuando el usuario pulse el botón.
 * @param modifier Un [Modifier] opcional para aplicar al [TextButton] contenedor.
 * @param underline Un [Boolean] que indica si el texto debe mostrarse subrayado.
 * Por defecto es `true`.
 */
@Composable
fun TextButtonLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underline: Boolean = true // Subrayado por defecto
) {
    TextButton(
        onClick = onClick, // Asigna la acción de clic
        modifier = modifier // Aplica el modificador externo
        // Se podrían añadir contentPadding si fuera necesario aquí
        // contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        // Texto interno del botón
        Text(
            text = text, // El texto a mostrar
            color = MaterialTheme.colorScheme.primary, // Usa el color primario del tema
            // Usa el estilo bodySmall y aplica el subrayado condicionalmente
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None
            )
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [TextButtonLink] con subrayado (por defecto).
 */
@Preview(showBackground = true, name = "TextButtonLink Underlined")
@Composable
private fun TextButtonLinkUnderlinedPreview() {
    MaterialTheme { // Necesario para MaterialTheme.colorScheme y .typography
        TextButtonLink(text = "¿Olvidaste tu contraseña?", onClick = {})
    }
}

/**
 * @brief Previsualización del [TextButtonLink] sin subrayado.
 */
@Preview(showBackground = true, name = "TextButtonLink No Underline")
@Composable
private fun TextButtonLinkNoUnderlinePreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(8.dp)) { // Usa Column para mostrar ambos
            Text("¿Ya tienes cuenta?")
            TextButtonLink(
                text = "Iniciar Sesión",
                onClick = {},
                underline = false // Sin subrayado
            )
        }
    }
}