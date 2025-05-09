/**
 * @file    Header.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o estructurales
 * @brief   Define un [Composable] reutilizable para la barra de aplicación superior (TopAppBar) centrada.
 *
 * @details Este archivo contiene el [Composable] `Header`, que implementa una barra de
 * aplicación superior utilizando [CenterAlignedTopAppBar] de Material 3. Está diseñado
 * para mostrar un título centrado y, opcionalmente, un icono de navegación "Atrás"
 * a la izquierda y/o un icono de acción (como la imagen de perfil del usuario) a la derecha.
 *
 * Proporciona una apariencia y comportamiento consistentes para los encabezados de las
 * diferentes pantallas de la aplicación. Los colores se toman por defecto del [MaterialTheme]
 * actual para asegurar la coherencia visual.
 *
 * @see androidx.compose.material3.CenterAlignedTopAppBar Componente base de Material 3 utilizado.
 * @see androidx.compose.material3.IconButton Botón utilizado para la navegación y la acción de perfil.
 * @see androidx.compose.material3.Icon Icono estándar utilizado para la flecha "Atrás".
 * @see androidx.compose.foundation.Image Componente utilizado para mostrar la imagen de perfil.
 * @see androidx.compose.ui.graphics.painter.Painter Tipo esperado para la imagen de perfil.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Icono auto-reflejado para RTL
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource // Para la preview
import androidx.compose.ui.tooling.preview.Preview // Para la preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.R // Para la preview

/**
 * @brief Un [Composable] que renderiza una barra de aplicación superior centrada y reutilizable.
 *
 * @details Utiliza [CenterAlignedTopAppBar] para mostrar un título centrado.
 * Permite incluir opcionalmente un botón de navegación "Atrás" (si `showBackButton` es `true`)
 * y una imagen de perfil como acción a la derecha (si `profileImage` no es `null`).
 * Los colores se adaptan al [MaterialTheme] actual.
 *
 * @param title El [String] que se mostrará como título principal centrado en la barra.
 * @param showBackButton Un [Boolean] que determina si se debe mostrar el icono de navegación
 * "Atrás" ([Icons.AutoMirrored.Filled.ArrowBack]). Por defecto es `false`.
 * @param onBackClick La función lambda que se ejecutará cuando el usuario pulse el botón "Atrás".
 * Solo es relevante si `showBackButton` es `true`. Por defecto es una lambda vacía.
 * @param profileImage Un [Painter] opcional que representa la imagen de perfil del usuario.
 * Si se proporciona, se mostrará como un [IconButton] circular a la derecha. Por defecto es `null`.
 * @param onProfileClick La función lambda que se ejecutará cuando el usuario pulse sobre la imagen
 * de perfil. Solo es relevante si `profileImage` no es `null`. Por defecto es una lambda vacía.
 * @param modifier Un [Modifier] opcional para aplicar al [CenterAlignedTopAppBar] contenedor.
 * Permite personalizar el layout, padding, etc. Por defecto es [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para CenterAlignedTopAppBar
@Composable
fun Header(
    title: String,
    showBackButton: Boolean = false, // No mostrar botón "Atrás" por defecto
    onBackClick: () -> Unit = {}, // Acción vacía por defecto
    profileImage: Painter? = null, // Sin imagen de perfil por defecto
    onProfileClick: () -> Unit = {}, // Acción vacía por defecto
    modifier: Modifier = Modifier // Modificador estándar
) {
    CenterAlignedTopAppBar(
        // Contenido del título (centrado)
        title = {
            Text(
                text = title,
                fontSize = 20.sp, // Tamaño de fuente específico para el título
                maxLines = 1 // Asegura que el título no ocupe múltiples líneas
                // Se podrían añadir otros estilos como fontWeight si fuera necesario
            )
        },
        // Icono de navegación (izquierda)
        navigationIcon = if (showBackButton) { // Se muestra solo si showBackButton es true
            { // Lambda que define el composable del icono
                IconButton(onClick = onBackClick) { // Botón interactivo
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Icono "Atrás" (soporta RTL)
                        contentDescription = "Atrás" // Descripción para accesibilidad
                    )
                }
            }
        } else {
            // Si no se muestra el botón, proporciona una lambda vacía
            {}
        },
        // Acciones (derecha)
        actions = {
            // Muestra la imagen de perfil solo si profileImage no es null
            profileImage?.let { painter ->
                IconButton(onClick = onProfileClick) { // Botón interactivo para la imagen
                    Image(
                        painter = painter, // El Painter proporcionado
                        contentDescription = "Perfil", // Descripción para accesibilidad
                        modifier = Modifier
                            .size(32.dp) // Tamaño del icono/imagen
                            .clip(CircleShape) // Recorte circular para estilo avatar
                        // contentScale = ContentScale.Crop // Podría ser útil si la imagen no es cuadrada
                    )
                }
            }
            // Se podrían añadir más acciones aquí si fuera necesario, dentro de este RowScope
        },
        // Configuración de colores basada en el tema actual
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background, // Fondo de la barra
            titleContentColor = MaterialTheme.colorScheme.onBackground, // Color del título
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground, // Color icono "Atrás"
            actionIconContentColor = MaterialTheme.colorScheme.onBackground // Color icono perfil
        ),
        // Aplica el modificador externo
        modifier = modifier
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [Header] solo con título.
 */
@Preview(showBackground = true, name = "Header - Title Only")
@Composable
private fun HeaderPreviewTitleOnly() {
    MaterialTheme {
        Header(title = "Pantalla Principal")
    }
}

/**
 * @brief Previsualización del [Header] con título y botón "Atrás".
 */
@Preview(showBackground = true, name = "Header - With Back Button")
@Composable
private fun HeaderPreviewWithBack() {
    MaterialTheme {
        Header(title = "Detalles", showBackButton = true, onBackClick = {})
    }
}

/**
 * @brief Previsualización del [Header] con título e imagen de perfil.
 */
@Preview(showBackground = true, name = "Header - With Profile Image")
@Composable
private fun HeaderPreviewWithProfile() {
    MaterialTheme {
        Header(
            title = "Mi Perfil",
            profileImage = painterResource(id = R.drawable.imagenprueba), // Usa un drawable de ejemplo
            onProfileClick = {}
        )
    }
}

/**
 * @brief Previsualización del [Header] con título, botón "Atrás" e imagen de perfil.
 */
@Preview(showBackground = true, name = "Header - Full")
@Composable
private fun HeaderPreviewFull() {
    MaterialTheme {
        Header(
            title = "Ajustes",
            showBackButton = true,
            onBackClick = {},
            profileImage = painterResource(id = R.drawable.imagenprueba),
            onProfileClick = {}
        )
    }
}