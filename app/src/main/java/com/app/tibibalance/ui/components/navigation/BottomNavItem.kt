/**
 * @file    BottomNavItem.kt
 * @ingroup ui_component_navigation // Grupo específico para componentes de navegación
 * @brief   Define la estructura de datos para un elemento de la barra de navegación inferior y la lista de elementos predeterminados.
 *
 * @details Este archivo contiene la data class [BottomNavItem] que encapsula la información
 * necesaria para renderizar un botón en la [BottomNavBar] (ruta de navegación, icono y etiqueta),
 * y la lista predefinida [bottomItems] que contiene las instancias de [BottomNavItem]
 * para las secciones principales de la aplicación.
 *
 * @see BottomNavItem Data class que representa un elemento de navegación.
 * @see bottomItems Lista predefinida de los elementos a mostrar en la barra.
 * @see BottomNavBar Composable que utiliza esta información para renderizar la barra.
 * @see Screen Clase/Objeto que define las rutas de navegación de la aplicación.
 */
package com.app.tibibalance.ui.components.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.app.tibibalance.ui.navigation.Screen // Importa la clase/objeto Screen para acceder a las rutas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List // Importa el icono auto-reflejado para List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

/**
 * @brief Data class que representa un elemento individual en la barra de navegación inferior ([BottomNavBar]).
 * @details Contiene la información necesaria para que el componente [NavBarButton] se renderice
 * y para que [BottomNavBar] maneje la navegación.
 *
 * @property route La [String] que identifica la ruta de navegación asociada a este elemento.
 * Debe coincidir con una ruta definida en el grafo de navegación (e.g., `Screen.Home.route`).
 * @property icon El [ImageVector] del icono de Material Design que se mostrará para este elemento.
 * @property label El [String] de texto que se muestra como etiqueta debajo del icono.
 */
data class BottomNavItem(
    val route : String,         // Ruta de navegación (e.g., "home", "profile")
    val icon  : ImageVector,    // Icono a mostrar (e.g., Icons.Filled.Home)
    val label : String          // Etiqueta de texto (e.g., "Inicio")
)

/**
 * @brief Lista predefinida de los elementos [BottomNavItem] que se mostrarán en la [BottomNavBar].
 * @details Define el orden y el contenido de cada botón en la barra de navegación inferior.
 * Utiliza las rutas definidas en [Screen] y los iconos estándar de Material Icons.
 * Se utiliza el icono `AutoMirrored.Filled.List` para "Hábitos" para que se invierta
 * automáticamente en layouts RTL (Right-to-Left).
 */
val bottomItems: List<BottomNavItem> = listOf(
    // Elemento para la pantalla de Emociones
    BottomNavItem(
        route = Screen.Emotions.route,      // Ruta desde Screen.Emotions
        icon  = Icons.Filled.DateRange,     // Icono de calendario/rango
        label = "Emociones"                 // Etiqueta
    ),
    // Elemento para la pantalla de Hábitos
    BottomNavItem(
        route = Screen.Habits.route,        // Ruta desde Screen.Habits
        icon  = Icons.AutoMirrored.Filled.List, // Icono de lista (auto-reflejado)
        label = "Hábitos"                   // Etiqueta
    ),
    // Elemento para la pantalla de Inicio (Home)
    BottomNavItem(
        route = Screen.Home.route,          // Ruta desde Screen.Home
        icon  = Icons.Filled.Home,          // Icono de casa
        label = "Inicio"                    // Etiqueta
    ),
    // Elemento para la pantalla de Perfil
    BottomNavItem(
        route = Screen.Profile.route,       // Ruta desde Screen.Profile
        icon  = Icons.Filled.Person,        // Icono de persona
        label = "Perfil"                    // Etiqueta
    ),
    // Elemento para la pantalla de Ajustes
    BottomNavItem(
        route = Screen.Settings.route,      // Ruta desde Screen.Settings
        icon  = Icons.Filled.Settings,      // Icono de engranaje
        label = "Ajustes"                   // Etiqueta
    )
)
