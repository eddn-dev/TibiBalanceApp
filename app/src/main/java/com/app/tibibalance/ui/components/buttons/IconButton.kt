/**
 * @file IconButton.kt
 * @ingroup ui_components
 * @brief Define un Composable [IconButton] reutilizable.
 *
 * @details
 * Este archivo contiene la implementación de `IconButton`, un botón circular
 * diseñado para mostrar un icono (proveniente de un recurso drawable) y manejar
 * interacciones de clic. Utiliza internamente el componente [com.app.tibibalance.ui.components.ImageContainer]
 * para renderizar el icono.
 *
 * El botón es personalizable en términos de tamaño, padding del contenido,
 * y color de fondo. Por defecto, tiene un fondo transparente.
 *
 * @see com.app.tibibalance.ui.components.ImageContainer El componente utilizado para mostrar el icono dentro del botón.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R // Asumiendo que R.drawable.iconbellswitch_off existe
import com.app.tibibalance.ui.components.ImageContainer

/**
 * @brief Un botón de icono circular personalizable.
 *
 * @details
 * Este Composable crea un área clicable con forma circular que muestra un icono
 * (obtenido de un recurso drawable) en su interior. El icono se renderiza utilizando
 * el componente [com.app.tibibalance.ui.components.ImageContainer].
 * El tamaño del botón, el padding alrededor del icono y el color de fondo son configurables.
 *
 * La estructura es un [Box] con un fondo circular y un modificador `clickable`.
 * Dentro del [Box], se centra un [com.app.tibibalance.ui.components.ImageContainer] que muestra el `resId` proporcionado.
 * El tamaño del [com.app.tibibalance.ui.components.ImageContainer] se ajusta automáticamente según el `size` del botón
 * y el `contentPadding`.
 *
 * @param resId El ID del recurso drawable para el icono a mostrar.
 * Debe ser un recurso válido anotado con `@DrawableRes`.
 * @param contentDescription Texto utilizado por los servicios de accesibilidad para describir el icono.
 * Puede ser `null` si el icono es puramente decorativo y su función
 * es comunicada por otros medios.
 * @param onClick Lambda que se invoca cuando el usuario pulsa el botón.
 * @param modifier Modificador de Compose para personalizar la apariencia o comportamiento del botón.
 * Por defecto es `Modifier`.
 * @param size El tamaño total (ancho y alto) del botón circular. Por defecto es `35.dp`.
 * @param contentPadding El padding aplicado dentro del círculo, alrededor del icono.
 * Afecta el tamaño visible del icono. Por defecto es `2.dp`.
 * @param backgroundColor El color de fondo del botón circular. Por defecto es `Color.Transparent`.
 *
 * @see com.app.tibibalance.ui.components.ImageContainer
 */
@Composable
fun IconButton(
    @DrawableRes resId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 35.dp,
    contentPadding: Dp = 2.dp,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .size(size) // Aplica el tamaño especificado.
            .background(backgroundColor, shape = CircleShape) // Fondo circular.
            .clickable(onClick = onClick) // Maneja los clics.
            .padding(contentPadding), // Padding interno para el icono.
        contentAlignment = Alignment.Center // Centra el ImageContainer.
    ) {
        // ImageContainer para mostrar el icono.
        // Su tamaño se calcula restando el doble del padding del contenido al tamaño total del botón.
        ImageContainer(
            resId = resId,
            contentDescription = contentDescription,
            modifier = Modifier.size(size - (contentPadding * 2))
        )
    }
}

/**
 * @brief Previsualización del Composable [IconButton].
 *
 * @details Muestra una instancia del [IconButton] con un icono de campana
 * y valores por defecto para los parámetros opcionales.
 * Esto permite verificar visualmente la apariencia básica del componente
 * durante el desarrollo.
 */
@Preview(showBackground = true )
@Composable
private fun PreviewIconButton() {
    IconButton(
        resId              = R.drawable.iconbellswitch_off, // Icono de ejemplo.
        contentDescription = "Campana apagada",
        onClick            = { /* Acción de ejemplo para la preview */ }
        // Se utilizan los valores por defecto para size, contentPadding y backgroundColor.
    )
}
