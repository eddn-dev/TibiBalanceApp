/**
 * @file    IconContainer.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o específicos de iconos
 * @brief   Define un [Composable] reutilizable para mostrar un icono vectorial dentro de un contenedor circular opcionalmente coloreado.
 *
 * @details Este archivo contiene el [Composable] `IconContainer`, que utiliza un componente
 * [Box] con forma de [CircleShape] para contener y centrar un [Icon] de Material 3.
 * Permite personalizar el icono a mostrar, su descripción de contenido, el color de fondo
 * del círculo contenedor (que por defecto es transparente), el tinte del icono y el tamaño
 * general del contenedor y el icono.
 *
 * Es útil para mostrar iconos de forma aislada o como parte de otros componentes,
 * asegurando un tamaño y alineación consistentes, con la opción de añadir un fondo
 * circular distintivo.
 *
 * @see androidx.compose.material3.Icon Componente base de Material 3 para mostrar iconos vectoriales.
 * @see androidx.compose.foundation.layout.Box Contenedor utilizado para centrar el icono y aplicar el fondo.
 * @see androidx.compose.foundation.shape.CircleShape Forma utilizada para el fondo y el recorte.
 * @see androidx.compose.ui.graphics.vector.ImageVector Tipo de dato esperado para el icono.
 */
package com.app.tibibalance.ui.components

// Import R si se usa en previews (no en este caso)
// import com.app.tibibalance.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons // Para preview
import androidx.compose.material.icons.filled.Favorite // Para preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // Para preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Importar clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que muestra un [ImageVector] centrado dentro de un [Box] circular.
 *
 * @details Renderiza un [Icon] dentro de un [Box] que tiene un tamaño fijo y una forma circular.
 * Permite configurar un color de fondo para el círculo (transparente por defecto) y
 * un tinte para el icono (negro por defecto). El tamaño aplica tanto al contenedor
 * como al icono interno para asegurar que el icono llene el círculo.
 *
 * @param icon El [ImageVector] que se mostrará.
 * @param contentDescription Un [String] opcional que describe el icono para la accesibilidad.
 * @param backgroundColor El [Color] de fondo para el [Box] circular. Por defecto es [Color.Transparent].
 * @param iconTint El [Color] que se aplicará como tinte al [icon]. Por defecto es [Color.Black].
 * @param size El tamaño (ancho y alto) en [Int] Dp para el [Box] contenedor y el [Icon]. Por defecto `48`.
 * @param modifier Un [Modifier] opcional que se aplica al [Box] contenedor principal.
 */
@Suppress("UNUSED_FUNCTION") // Suprime el aviso si la función no se usa fuera de este archivo (común para previews o helpers internos)
@Composable
fun IconContainer(
    icon: ImageVector,
    contentDescription: String?,
    backgroundColor: Color = Color.Transparent, // Fondo transparente por defecto
    iconTint: Color = Color.Black, // Tinte negro por defecto
    size: Int = 48, // Tamaño 48dp por defecto
    modifier: Modifier = Modifier // Modificador estándar
) {
    Box(
        modifier = modifier
            .size(size.dp) // Aplica el tamaño
            // Aplica el fondo con la forma circular ANTES del clip si quieres que el fondo sea visible
            // O DESPUÉS si quieres que el clip afecte también al fondo (aunque con CircleShape no hay diferencia)
            .clip(CircleShape) // Asegura que el contenido (Icon) se recorte si excede el círculo
            .background(color = backgroundColor, shape = CircleShape), // Aplica fondo circular
        contentAlignment = Alignment.Center // Centra el Icon dentro del Box
    ) {
        Icon(
            imageVector = icon, // El icono a mostrar
            contentDescription = contentDescription, // Descripción para accesibilidad
            tint = iconTint, // Aplica el tinte al icono
            // El tamaño del Icon no se especifica aquí, tomará el del Box padre si es posible
            // o se puede forzar con modifier = Modifier.size(size.dp * factor) si se quiere más pequeño
            modifier = Modifier.size((size * 0.6f).dp) // Ejemplo: Hacer el icono un 60% del tamaño del contenedor
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [IconContainer] con valores por defecto (fondo transparente).
 */
@Preview(showBackground = true, name = "IconContainer Default")
@Composable
private fun IconContainerDefaultPreview() {
    MaterialTheme {
        IconContainer(
            icon = Icons.Filled.Favorite,
            contentDescription = "Icono Favorito (Default)"
            // Usa backgroundColor y iconTint por defecto
        )
    }
}

/**
 * @brief Previsualización del [IconContainer] con fondo y tinte personalizados.
 */
@Preview(showBackground = true, name = "IconContainer Custom Colors")
@Composable
private fun IconContainerCustomPreview() {
    MaterialTheme {
        IconContainer(
            icon = Icons.Filled.Favorite,
            contentDescription = "Icono Favorito (Custom)",
            backgroundColor = MaterialTheme.colorScheme.primaryContainer, // Fondo de color primario container
            iconTint = MaterialTheme.colorScheme.onPrimaryContainer, // Tinte que contrasta con el fondo
            size = 64 // Tamaño más grande
        )
    }
}

/**
 * @brief Previsualización del [IconContainer] con tamaño pequeño.
 */
@Preview(showBackground = true, name = "IconContainer Small")
@Composable
private fun IconContainerSmallPreview() {
    MaterialTheme {
        IconContainer(
            icon = Icons.Filled.Favorite,
            contentDescription = "Icono Favorito (Pequeño)",
            size = 24 // Tamaño pequeño
        )
    }
}