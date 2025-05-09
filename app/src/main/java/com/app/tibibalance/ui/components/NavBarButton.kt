/**
 * @file    NavBarButton.kt
 * @ingroup ui_component_navigation // Grupo para componentes de navegación
 * @brief   Define un [Composable] para un botón individual utilizado dentro de la barra de navegación inferior ([BottomNavBar]).
 *
 * @details Este archivo contiene el [Composable] `NavBarButton`, que representa visualmente
 * un destino de navegación en la [BottomNavBar]. Muestra un icono y una etiqueta de texto
 * verticalmente alineados. Cambia su apariencia (color del icono, color del texto,
 * y un fondo de realce) para indicar si es el elemento actualmente seleccionado.
 *
 * El componente es interactivo (`clickable`) y delega la acción de navegación
 * al callback `onClick` proporcionado.
 *
 * @see com.app.tibibalance.ui.components.navigation.BottomNavItem Data class que define la información para este botón.
 * @see com.app.tibibalance.ui.components.navigation.BottomNavBar Composable padre que utiliza NavBarButton.
 * @see androidx.compose.material3.Icon Composable para mostrar el icono.
 * @see androidx.compose.material3.Text Composable para mostrar la etiqueta.
 * @see androidx.compose.foundation.layout.Box Contenedor principal del botón.
 * @see androidx.compose.foundation.layout.Column Usado para alinear icono y texto verticalmente.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Para Preview
import androidx.compose.material.icons.filled.Home // Para Preview
import androidx.compose.material.icons.filled.Person // Para Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Para Preview
import androidx.compose.runtime.mutableStateOf // Para Preview
import androidx.compose.runtime.remember // Para Preview
import androidx.compose.runtime.setValue // Para Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // Para Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.ui.components.navigation.BottomNavItem

/**
 * @brief Un [Composable] que renderiza un botón individual para la barra de navegación inferior.
 *
 * @details Muestra un icono y una etiqueta de texto alineados verticalmente dentro de un [Box]
 * clicable. Si el botón está `selected`, se aplica un fondo de realce semitransparente
 * con bordes redondeados, y el tinte del icono y el color del texto cambian al color
 * primario del tema. En estado no seleccionado, usa los colores estándar del tema
 * para contenido sobre superficie.
 *
 * @param item El objeto [BottomNavItem] que contiene la información del icono, etiqueta y ruta
 * para este botón de navegación.
 * @param selected Un [Boolean] que indica si este botón de navegación es el actualmente
 * seleccionado en la [BottomNavBar]. Determina su apariencia visual.
 * @param onClick La función lambda que se invoca cuando el usuario pulsa este botón.
 * Típicamente, esta lambda navega a la ruta asociada con el `item`.
 */
@Composable
fun NavBarButton(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Contenedor principal del botón, define el tamaño y la acción de clic
    Box(
        modifier = Modifier
            .size(75.dp) // Tamaño fijo para el área del botón
            .clickable(onClick = onClick), // Hace todo el Box clicable
        contentAlignment = Alignment.Center // Centra el contenido (la Columna)
    ) {
        // Fondo de realce, visible solo si 'selected' es true
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize() // Ocupa todo el espacio del Box padre
                    .background(
                        // Color primario del tema con baja opacidad para el realce
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        // Bordes redondeados para el fondo de realce
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
        // Columna para alinear el icono y el texto verticalmente
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centra icono y texto horizontalmente
            verticalArrangement = Arrangement.Center // Centra el contenido de la columna verticalmente
        ) {
            // Icono del botón
            Icon(
                imageVector = item.icon, // Vector del icono desde BottomNavItem
                contentDescription = item.label, // Descripción para accesibilidad (usa la etiqueta)
                // Tinte del icono: primario si está seleccionado, onSurface si no
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            // Espacio vertical entre el icono y el texto
            Spacer(modifier = Modifier.height(4.dp))
            // Etiqueta de texto del botón
            Text(
                text = item.label, // Texto de la etiqueta desde BottomNavItem
                fontSize = 12.sp, // Tamaño de fuente específico para la etiqueta
                // Color del texto: primario si está seleccionado, onSurface si no
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                // maxLines = 1 // Opcional: para asegurar que la etiqueta no haga wrap
            )
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [NavBarButton] en estado seleccionado.
 */
@Preview(showBackground = true, name = "NavBarButton Selected")
@Composable
private fun NavBarButtonSelectedPreview() {
    MaterialTheme {
        NavBarButton(
            item = BottomNavItem(route = "home", icon = Icons.Filled.Home, label = "Inicio"),
            selected = true,
            onClick = {}
        )
    }
}

/**
 * @brief Previsualización del [NavBarButton] en estado no seleccionado.
 */
@Preview(showBackground = true, name = "NavBarButton Not Selected")
@Composable
private fun NavBarButtonNotSelectedPreview() {
    MaterialTheme {
        NavBarButton(
            item = BottomNavItem(route = "profile", icon = Icons.Filled.Person, label = "Perfil"),
            selected = false,
            onClick = {}
        )
    }
}

/**
 * @brief Previsualización de múltiples [NavBarButton] para simular una barra.
 */
@Preview(showBackground = true, name = "NavBarButton Row")
@Composable
private fun NavBarButtonRowPreview() {
    val items = listOf(
        BottomNavItem(route = "home", icon = Icons.Filled.Home, label = "Inicio"),
        BottomNavItem(route = "profile", icon = Icons.Filled.Person, label = "Perfil")
    )
    var selectedRoute by remember { mutableStateOf("home") }
    MaterialTheme {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            items.forEach { item ->
                NavBarButton(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { selectedRoute = item.route }
                )
            }
        }
    }
}