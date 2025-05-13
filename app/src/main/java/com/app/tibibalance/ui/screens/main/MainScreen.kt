/**
 * @file    MainScreen.kt
 * @ingroup ui_screens_main // Grupo para pantallas principales post-autenticación
 * @brief   Define el [Composable] raíz para la sección principal de la aplicación, que incluye la barra de navegación inferior y el contenido paginado.
 *
 * @details Este Composable actúa como el contenedor principal después de que el usuario
 * ha iniciado sesión y verificado su correo (si es necesario). Utiliza un [Scaffold]
 * para estructurar la pantalla con una [BottomNavBar] fija en la parte inferior y un
 * [HorizontalPager] que muestra el contenido de la sección actualmente seleccionada.
 *
 * La sincronización entre la [BottomNavBar] y el [HorizontalPager] se gestiona mediante
 * un [PagerState] compartido. También incluye un [NavHost] "sombra" (`innerNav`) que,
 * aunque no muestra contenido directamente, se utiliza para mantener el estado de la ruta
 * actual consistente con el [PagerState], lo que podría ser útil para integrar con otras
 * lógicas de navegación o estado que dependan de un `NavController`.
 *
 * Además, observa eventos del [MainViewModel], como [MainEvent.SignedOut], para
 * manejar la navegación de vuelta al flujo de autenticación ([Screen.Launch]) y limpiar
 * el estado del ViewModel asociado a la pantalla principal.
 *
 * @see MainViewModel ViewModel que gestiona el estado y eventos de esta pantalla.
 * @see BottomNavBar Componente para la barra de navegación inferior.
 * @see bottomItems Lista de elementos mostrados en la BottomNavBar.
 * @see HorizontalPager Componente para mostrar contenido paginado horizontalmente.
 * @see PagerState Estado que controla el HorizontalPager y se sincroniza con la BottomNavBar.
 * @see NavHost Componente de Navigation Compose (usado aquí de forma "sombra").
 * @see Screen Clase sellada que define las rutas de navegación.
 * @see Scaffold Estructura básica de Material 3.
 * @see SettingsTab Sub-composable para la pestaña de Ajustes.
 */
@file:OptIn(ExperimentalFoundationApi::class)

package com.app.tibibalance.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.app.tibibalance.ui.components.navigation.BottomNavBar
import com.app.tibibalance.ui.components.navigation.bottomItems
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.conection.ConnectedDeviceScreen_Preview_Empty
import com.app.tibibalance.ui.screens.emotional.EmotionalCalendarScreen
import com.app.tibibalance.ui.screens.home.HomeScreen
import com.app.tibibalance.ui.screens.settings.*
import com.app.tibibalance.ui.screens.habits.ShowHabitsScreen   // ← nuevo
import kotlinx.coroutines.launch

/**
 * @brief Composable principal que define la estructura de navegación post-autenticación con barra inferior y paginador.
 *
 * @details Configura un [Scaffold] con una [BottomNavBar] y un [HorizontalPager] sincronizados.
 * Utiliza un [NavHostController] `rootNav` para la navegación global (salir de esta pantalla)
 * y un `innerNav` local para reflejar el estado del paginador. Escucha eventos del [MainViewModel]
 * para manejar el cierre de sesión.
 *
 * @param rootNav El [NavHostController] principal de la aplicación, utilizado para navegar
 * fuera de este flujo principal (e.g., volver a [Screen.Launch] al cerrar sesión).
 * @param mainVm Instancia de [MainViewModel] (inyectada por Hilt) que gestiona el estado
 * de la página actual, el estado de la pantalla de ajustes y los eventos como el cierre de sesión.
 */
@Composable
fun MainScreen(
    rootNav: NavHostController,                 // ← controlador global
    mainVm : MainViewModel = hiltViewModel()
) {
    /** @brief Lista de rutas correspondientes a las secciones de la BottomNavBar y las páginas del Pager. */
    val routes = remember { // Remember para evitar recreación innecesaria
        listOf(
            Screen.Emotions.route,
            Screen.Habits.route,
            Screen.Home.route,
            Screen.Profile.route,
            Screen.Settings.route
        )
    }

    /** @brief NavController interno, utilizado principalmente para mantener la ruta actual sincronizada con el Pager. */
    val innerNav = rememberNavController()

    /** @brief Estado del Pager que controla la página visible y permite la navegación programática. */
    val pagerState = rememberPagerState(initialPage = 2) { routes.size } // Home es la página inicial (índice 2)
    /** @brief Scope de Coroutine utilizado para lanzar animaciones del Pager. */
    val scope      = rememberCoroutineScope()

    /* ───── NavHost Sombra ───── */
    // Este NavHost no renderiza UI visible (height=0.dp). Su propósito es mantener
    // la pila de navegación de `innerNav` sincronizada con la página actual del Pager.
    // Podría ser útil si otros componentes o lógica necesitaran observar el NavController local.
    NavHost(
        navController    = innerNav,            // Controlador local
        startDestination = Screen.Home.route,   // Destino inicial (coincide con initialPage del Pager)
        modifier         = Modifier.height(0.dp) // Oculta el NavHost visualmente
    ) {
        // Define un destino Composable vacío para cada ruta gestionada por el Pager/BottomBar.
        routes.forEach { route -> composable(route) { /* No se muestra contenido aquí */ } }
    }

    /* ───── Sincronización BottomBar -> Pager ───── */
    // Función para navegar el Pager cuando se hace clic en un ítem de la BottomNavBar.
    fun goTo(route: String) {
        val idx = routes.indexOf(route) // Encuentra el índice de la ruta seleccionada
        if (idx >= 0) { // Si la ruta es válida
            scope.launch { // Lanza la animación en un scope de corrutina
                pagerState.animateScrollToPage(idx) // Anima el Pager a la página correspondiente
            }
        }
    }

    /* ───── Sincronización Pager -> NavHost Sombra ───── */
    // Efecto que se ejecuta cuando la página actual del Pager cambia.
    LaunchedEffect(pagerState.currentPage) {
        val route = routes[pagerState.currentPage] // Obtiene la ruta de la página actual
        // Navega en `innerNav` solo si la ruta actual del NavController no coincide ya.
        // `launchSingleTop = true` evita crear múltiples instancias de la misma pantalla en la pila.
        if (innerNav.currentDestination?.route != route) {
            innerNav.navigate(route) { launchSingleTop = true }
        }
    }

    /* ───── Observador de Evento de Cierre de Sesión ───── */
    // Efecto lanzado una vez para escuchar eventos del ViewModel.
    LaunchedEffect(Unit) {
        mainVm.events.collect { ev ->
            if (ev is MainEvent.SignedOut) { // Si se recibe el evento de cierre de sesión
                // Opcional: Limpia el estado asociado a ViewModels del NavHost interno si es necesario.
                // innerNav.getBackStackEntry(Screen.Home.route).viewModelStore.clear() // Ejemplo
                // Navega usando el NavController *global* (rootNav) de vuelta a LaunchScreen,
                // eliminando todo el flujo "Main" de la pila de navegación.
                rootNav.navigate(Screen.Launch.route) {
                    popUpTo(Screen.Main.route) { inclusive = true } // Limpia hasta MainScreen inclusive
                }
            }
        }
    }

    /* ───── UI Principal (Scaffold) ───── */
    Scaffold(
        // Define la barra de navegación inferior.
        bottomBar = {
            BottomNavBar(
                items         = bottomItems, // Los ítems a mostrar (definidos en ui.components.navigation)
                selectedRoute = routes[pagerState.currentPage], // La ruta seleccionada basada en la página actual del Pager
                onItemClick   = ::goTo // La acción a ejecutar al hacer clic en un ítem
            )
        },
        // Aplica padding para evitar que el contenido se dibuje debajo de las barras del sistema (navegación).
        modifier = Modifier.navigationBarsPadding()
    ) { padding -> // El contenido principal recibe el padding calculado por el Scaffold.
        // Paginador horizontal que ocupa el espacio disponible.
        HorizontalPager(
            state    = pagerState, // Vinculado al estado del Pager
            pageSize = PageSize.Fill, // Cada página ocupa todo el tamaño
            modifier = Modifier
                .padding(padding) // Aplica el padding del Scaffold
                .fillMaxSize() // Ocupa el espacio restante
        ) { pageIndex -> // El contenido de cada página
            // Determina qué pantalla mostrar basado en el índice de la página actual.
            when (routes[pageIndex]) {
                Screen.Home.route     -> HomeScreen()
                Screen.Emotions.route -> EmotionalCalendarScreen() // Placeholder
                Screen.Habits.route   -> ShowHabitsScreen()
                Screen.Profile.route  -> Centered("Perfil")   // Placeholder
                Screen.Settings.route -> SettingsTab(mainVm, rootNav) // Contenido específico para Ajustes
            }
        }
    }
}

/**
 * @brief Sub-composable privado que encapsula la lógica y UI de la pestaña "Ajustes".
 *
 * @details Obtiene la instancia de [SettingsViewModel] asociada a esta pestaña,
 * observa su estado [SettingsUiState], y renderiza el [SettingsScreen] correspondiente,
 * pasándole el estado y los callbacks necesarios.
 *
 * @param mainVm Instancia del [MainViewModel] principal, pasada para acceder a acciones globales como `signOut`.
 * @param rootNav El [NavHostController] principal de la aplicación, pasado para acciones como `MapsUp` desde `SettingsScreen`.
 */
@Composable
private fun SettingsTab(mainVm: MainViewModel, rootNav: NavHostController) {
    // Obtiene la instancia de SettingsViewModel asociada a este punto del grafo de navegación.
    val vm: SettingsViewModel = hiltViewModel()
    // Observa el estado de la UI emitido por SettingsViewModel.
    val uiState by vm.ui.collectAsState()

    // Renderiza la pantalla de Ajustes.
    SettingsScreen(
        state          = uiState,                     // Pasa el estado actual de la UI de ajustes.
        onNavigateUp   = rootNav::navigateUp,         // Acción para el botón Atrás (usa el NavController global).
        onEditPersonal = { /* TODO: Navegar a pantalla de edición */ },
        onDevices      = { rootNav.navigate("connected_devices") },
        onAchievements = { /* TODO: Navegar a pantalla de logros */ },
        onSignOut      = mainVm::signOut,             // Acción de cierre de sesión (manejada por MainViewModel).
        onDelete       = { /* TODO: Implementar borrado de cuenta */ },
        onNotis        = { /* TODO: Navegar a pantalla de config. notificaciones */ }
    )
}

/**
 * @brief Composable de utilidad simple para mostrar texto centrado en pantalla.
 * @details Usado como placeholder temporal para las secciones aún no implementadas.
 *
 * @param txt El texto [String] a mostrar.
 */
@Composable
private fun Centered(txt: String) = Box(
    Modifier.fillMaxSize(), Alignment.Center
) { androidx.compose.material3.Text(txt, fontSize = 32.sp) }