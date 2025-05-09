/**
 * @file    RoundedIconButton.kt
 * @ingroup ui_component_button // Grupo específico para componentes de botón
 * @brief   Define un [Composable] para un botón de acción flotante circular con un icono.
 *
 * @details Este archivo contiene el [Composable] `RoundedIconButton`. Renderiza un [Box]
 * con forma de [CircleShape] que actúa como contenedor. Dentro de él, se centra un
 * [Icon]. El [Box] tiene un fondo personalizable, sombra, y es clicable, simulando
 * la apariencia y comportamiento de un Botón de Acción Flotante (FAB) pequeño o un
 * botón de icono prominente.
 *
 * Es comúnmente utilizado para acciones importantes y singulares en una pantalla, como
 * añadir un nuevo elemento.
 *
 * @see androidx.compose.foundation.layout.Box Contenedor principal con forma y fondo.
 * @see androidx.compose.foundation.shape.CircleShape Usada para la forma circular.
 * @see androidx.compose.material3.Icon Composable para mostrar el icono vectorial.
 * @see androidx.compose.ui.Modifier Modificadores para tamaño, sombra, fondo y click.
 * @see androidx.compose.ui.graphics.vector.ImageVector Tipo de dato esperado para el icono.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // Para Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // Para Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow // Para aplicar sombra
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que representa un botón circular con un icono, típicamente para acciones flotantes.
 *
 * @details Muestra un [Icon] centrado dentro de un [Box] circular. El [Box] tiene un tamaño
 * predeterminado de `56.dp` (tamaño estándar para FAB), un color de fondo personalizable,
 * sombra, y responde a los clics del usuario.
 *
 * @param onClick La función lambda que se ejecuta cuando el usuario pulsa el botón.
 * @param icon El [ImageVector] que se mostrará en el centro del botón.
 * @param contentDescription El [String] que describe la acción del botón para la accesibilidad.
 * @param modifier Un [Modifier] opcional para aplicar al [Box] contenedor principal.
 * Por defecto aplica un tamaño de `56.dp`. Se pueden añadir otros modificadores como padding.
 * @param backgroundColor El [Color] de fondo para el círculo del botón. Por defecto es un azul (`0xFF42A5F5`).
 * Considera usar `MaterialTheme.colorScheme.primaryContainer` o `secondaryContainer` para adaptarse al tema.
 * @param iconTint El [Color] (tinte) que se aplicará al `icon`. Por defecto es `Color.White`.
 * Considera usar `MaterialTheme.colorScheme.onPrimaryContainer` o `onSecondaryContainer`.
 */
@Composable
fun RoundedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier, // Modificador externo opcional
    backgroundColor: Color = Color(0xFF42A5F5), // Color de fondo azul por defecto
    iconTint: Color = Color.White // Tinte blanco por defecto para el icono
) {
    Box(
        modifier = modifier
            .size(56.dp) // Tamaño estándar de FAB por defecto
            // Aplica sombra con forma circular
            .shadow(elevation = 6.dp, shape = CircleShape)
            // Fondo circular con el color especificado
            .background(color = backgroundColor, shape = CircleShape)
            // Hace que toda el área sea clicable
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center // Centra el Icon dentro del Box
    ) {
        Icon(
            imageVector = icon, // El icono a mostrar
            contentDescription = contentDescription, // Descripción para accesibilidad
            tint = iconTint // Tinte del icono
            // El tamaño del icono se ajustará al Box si no se especifica un Modifier.size() aquí
        )
    }
}

/**
 * @brief Previsualización Composable para [RoundedIconButton].
 * @details Muestra el botón con el icono de 'Add' y los colores por defecto.
 */
@Preview(showBackground = true)
@Composable
fun RoundedIconButtonPreview() {
    MaterialTheme { // Envuelve para que los iconos estándar estén disponibles
        RoundedIconButton(
            onClick = {}, // Acción vacía para preview
            icon = Icons.Default.Add, // Icono estándar de añadir
            contentDescription = "Agregar" // Descripción
            // Usa backgroundColor y iconTint por defecto
        )
    }
}