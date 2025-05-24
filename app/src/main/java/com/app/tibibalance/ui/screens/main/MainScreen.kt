// src/main/java/com/app/tibibalance/ui/screens/main/MainScreen.kt
package com.app.tibibalance.ui.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.emotional.EmotionalCalendarScreen
import com.app.tibibalance.ui.screens.home.HomeScreen
import com.app.tibibalance.ui.screens.habits.ShowHabitsScreen
import com.app.tibibalance.ui.screens.settings.SettingsScreen
import com.app.tibibalance.ui.screens.settings.SettingsUiState
import com.app.tibibalance.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * @file    MainScreen.kt
 * @ingroup ui_screens_main
 * @brief   Composable raíz con barra inferior y paginador.
 *
 * @param rootNav NavController global para navegación fuera de este flujo.
 * @param mainVm  ViewModel que maneja eventos como cierre de sesión.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    rootNav: NavHostController,
    mainVm: MainViewModel = hiltViewModel()
) {
    // Definimos las rutas de las pestañas
    val routes = listOf(
        Screen.Emotions.route,
        Screen.Habits.route,
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    // NavController interno para sincronizar con el HorizontalPager
    val innerNav = rememberNavController()
    val pagerState = rememberPagerState(initialPage = 2) { routes.size }
    val scope = rememberCoroutineScope()

    // NavHost "sombra" para innerNav (height=0)
    NavHost(
        navController = innerNav,
        startDestination = Screen.Home.route,
        modifier = Modifier.height(0.dp)
    ) {
        routes.forEach { route ->
            composable(route) { /* No renderiza UI aquí */ }
        }
    }

    // Función para navegar el pager al pulsar la barra inferior
    fun goTo(route: String) {
        val index = routes.indexOf(route)
        if (index >= 0) {
            scope.launch { pagerState.animateScrollToPage(index) }
        }
    }

    // Sincronizar cambios de página con innerNav
    LaunchedEffect(pagerState.currentPage) {
        val route = routes[pagerState.currentPage]
        if (innerNav.currentDestination?.route != route) {
            innerNav.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    // Escuchar evento de cierre de sesión para navegar fuera
    LaunchedEffect(Unit) {
        mainVm.events.collect { ev ->
            if (ev is MainEvent.SignedOut) {
                rootNav.navigate(Screen.Launch.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }
    }

    // UI principal
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
                Screen.Home.route -> HomeScreen()
                Screen.Emotions.route -> EmotionalCalendarScreen()
                Screen.Habits.route -> ShowHabitsScreen()
                Screen.Profile.route -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Perfil", fontSize = 32.sp) }
                Screen.Settings.route -> SettingsTab(mainVm)
            }
        }
    }
}

/**
 * @brief Sub-composable para la pestaña Ajustes.
 *
 * @param mainVm ViewModel principal para acciones globales (cerrar sesión).
 */
@Composable
private fun SettingsTab(mainVm: MainViewModel) {
    val vm: SettingsViewModel = hiltViewModel()
    val uiState by vm.ui.collectAsState()

    SettingsScreen(
        state = uiState,
        onEditPersonal = { /* TODO: navegar a editar perfil */ },
        onAchievements = { /* TODO: navegar a logros */ },
        onSignOut = mainVm::signOut,
        onDelete = {
            vm.deleteAccount()
            mainVm.signOut()
        },
        onNotis = { /* TODO: navegar a notificaciones */ }
    )
}
