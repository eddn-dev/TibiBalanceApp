/**
 * @file    CloseIconButton.kt
 * @ingroup ui_component_button // Grupo específico para componentes de botón
 * @brief   Define un [Composable] reutilizable para un botón de icono con una 'X' (cerrar).
 *
 * @details Este archivo contiene el [Composable] `CloseIconButton` que muestra un
 * [IconButton] estándar con el icono [Icons.Default.Close]. Se utiliza comúnmente
 * para permitir al usuario cerrar diálogos modales, pantallas o secciones.
 * Incluye también una función de previsualización [CloseIconButtonPreview].
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Usado para el tinte hardcoded
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que muestra un [IconButton] con el icono de cierre ([Icons.Default.Close]).
 *
 * @details Este componente proporciona un botón estándar para acciones de "cerrar".
 * Internamente, utiliza el componente [IconButton] de Material 3 y le aplica un
 * tamaño fijo de `32.dp`. El icono [Icon] utiliza el vector `Icons.Default.Close`.
 *
 * **Nota:** La descripción de contenido (`contentDescription`) y el tinte del icono (`tint`)
 * están actualmente hardcodeados a "Cerrar" y `Color.Black` respectivamente. Para
 * internacionalización y flexibilidad de tema, considera obtener la descripción de
 * `strings.xml` y el tinte de `MaterialTheme.colorScheme`.
 *
 * @param onClick La función lambda que se ejecutará cuando el botón sea pulsado.
 * Típicamente, esta lambda contendrá la lógica para cerrar el
 * elemento UI correspondiente (e.g., `onDismissRequest` de un diálogo).
 * @param modifier Un [Modifier] opcional que se aplicará al [IconButton]. Permite
 * personalizar el padding, tamaño adicional, etc., desde el lugar
 * donde se llama al componente. Por defecto es [Modifier].
 */
@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Modificador opcional con valor por defecto
) {
    IconButton(
        onClick = onClick, // Asigna la acción de clic
        // Aplica el modificador recibido Y un tamaño fijo interno
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close, // Icono estándar de 'cerrar'
            // TODO: Considerar usar stringResource(R.string.close_button_description)
            contentDescription = "Cerrar", // Texto para accesibilidad
            // TODO: Considerar usar MaterialTheme.colorScheme.onSurface o similar
            tint = Color.Black // Tinte del icono (actualmente negro fijo)
        )
    }
}

/**
 * @brief Previsualización Composable para [CloseIconButton].
 * @details Muestra el [CloseIconButton] en el panel de previsualización de Android Studio
 * con un fondo y una acción `onClick` vacía. Útil para verificar la apariencia
 * básica del componente de forma aislada.
 */
@Preview(showBackground = true, name = "Close Icon Button Preview") // Añadido nombre al Preview
@Composable
private fun CloseIconButtonPreview() { // Marcado como private, común para previews
    // Llama al componente con una lambda vacía para el onClick
    CloseIconButton(onClick = { })
}
