/**
 * @file    MainScreen.kt
 * @ingroup ui_screens_main
 * @brief   Composable raíz que contiene la pantalla principal de navegación tras el login.
 *
 * @details
 * Esta pantalla actúa como contenedor principal tras iniciar sesión. Organiza
 * las secciones de la app mediante un `HorizontalPager` y una `BottomNavBar`.
 *
 * La pestaña de inicio (`Screen.Home`) muestra un resumen de actividad diaria. Se
 * integra con el `MetricsViewModel` para determinar si hay conexión con el reloj
 * y mostrar contenido correspondiente:
 *
 * - Si **no hay reloj conectado**, se muestra el componente `HomeModalsSection` con
 *   un tip del día y la opción de conectar el reloj.
 * - Si **sí hay conexión**, se muestran métricas de pasos, ejercicio, calorías y ritmo cardíaco.
 *
 * También responde a eventos del `MainViewModel` como el cierre de sesión o eliminación de cuenta.
 *
 * @param rootNav Controlador de navegación global (desde AppNavGraph).
 * @param mainVm ViewModel principal que maneja eventos como cerrar sesión.
 */
@file:Suppress("UNUSED_PARAMETER") // Para parámetros de navegaciones no implementadas aún

package com.app.tibibalance.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.tibibalance.ui.components.navigation.BottomNavBar
import com.app.tibibalance.ui.components.navigation.bottomItems
import com.app.tibibalance.ui.metrics.MetricsViewModel
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.emotional.EmotionalCalendarScreen
import com.app.tibibalance.ui.screens.habits.ShowHabitsScreen
import com.app.tibibalance.ui.screens.home.HomeScreen
import com.app.tibibalance.ui.screens.settings.SettingsScreen
import com.app.tibibalance.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    rootNav: NavHostController,
    mainVm: MainViewModel = hiltViewModel()
) {
    // Define las rutas por pestaña
    val routes = listOf(
        Screen.Emotions.route,
        Screen.Habits.route,
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    val innerNav = rememberNavController()
    val pagerState = rememberPagerState(initialPage = 2) { routes.size }
    val scope = rememberCoroutineScope()

    // ViewModel para obtener el estado de conexión del reloj
    val metricsVm: MetricsViewModel = hiltViewModel()
    val latest by metricsVm.latest.collectAsState()

    // Mantiene sincronía de rutas (invisible)
    NavHost(
        navController = innerNav,
        startDestination = Screen.Home.route,
        modifier = Modifier.height(0.dp)
    ) {
        routes.forEach { route -> composable(route) { /* Sin UI aquí */ } }
    }

    // Sincroniza barra inferior con página activa
    fun goTo(route: String) {
        val index = routes.indexOf(route)
        if (index >= 0) {
            scope.launch { pagerState.animateScrollToPage(index) }
        }
    }

    // Navega al cambiar de página
    LaunchedEffect(pagerState.currentPage) {
        val route = routes[pagerState.currentPage]
        if (innerNav.currentDestination?.route != route) {
            innerNav.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    // Escucha eventos globales (ej. cerrar sesión)
    LaunchedEffect(Unit) {
        mainVm.events.collect { ev ->
            if (ev is MainEvent.SignedOut) {
                rootNav.navigate(Screen.Launch.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = bottomItems,
                selectedRoute = routes[pagerState.currentPage],
                onItemClick = ::goTo
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) { page ->
            when (routes[page]) {
                Screen.Home.route -> HomeScreen(
                    isWatchConnected = latest != null,
                    onNavigateToConnectedDevices = {
                        rootNav.navigate(Screen.ManageDevices.route)
                    }
                )

                Screen.Emotions.route -> EmotionalCalendarScreen()

                Screen.Habits.route -> ShowHabitsScreen()

                Screen.Profile.route -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Perfil", fontSize = 32.sp)
                }

                Screen.Settings.route -> SettingsTab(mainVm)
            }
        }
    }
}

/**
 * @brief Sub-componente para la pestaña de Ajustes dentro del pager.
 *
 * @param mainVm ViewModel principal para manejar acciones globales como eliminar cuenta o cerrar sesión.
 */
@Composable
private fun SettingsTab(mainVm: MainViewModel) {
    val vm: SettingsViewModel = hiltViewModel()
    val uiState by vm.ui.collectAsState()

    SettingsScreen(
        state = uiState,
        onEditPersonal = { /* TODO: Implementar navegación a editar perfil */ },
        onAchievements = { /* TODO: Implementar navegación a logros */ },
        onSignOut = mainVm::signOut,
        onDelete = {
            vm.deleteAccount()
            mainVm.signOut()
        },
        onNotis = { /* TODO: Implementar navegación a configuración de notificaciones */ }
    )
}
