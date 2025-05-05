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
import com.app.tibibalance.ui.components.BottomNavBar
import com.app.tibibalance.ui.components.bottomItems
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.home.HomeScreen
import com.app.tibibalance.ui.screens.settings.*
import com.app.tibibalance.ui.screens.habits.ShowHabitsScreen   // ← nuevo
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    rootNav: NavHostController,                 // ← controlador global
    mainVm : MainViewModel = hiltViewModel()
) {
    /* rutas de pager/bottom bar */
    val routes = listOf(
        Screen.Emotions.route,
        Screen.Habits.route,
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    /* controlador interno para el gráfico local */
    val innerNav = rememberNavController()

    val pagerState = rememberPagerState(initialPage = 2, pageCount = { routes.size })
    val scope      = rememberCoroutineScope()

    /* ───── NavHost sombra solo con innerNav ───── */
    NavHost(
        navController    = innerNav,            //  ← cambio clave
        startDestination = Screen.Home.route,
        modifier         = Modifier.height(0.dp)
    ) { routes.forEach { composable(it) { /* vacío */ } } }

    /* BottomBar -> Pager */
    fun goTo(route: String) {
        val idx = routes.indexOf(route)
        if (idx >= 0) scope.launch { pagerState.animateScrollToPage(idx) }
    }

    /* Pager -> innerNav */
    LaunchedEffect(pagerState.currentPage) {
        val route = routes[pagerState.currentPage]
        if (innerNav.currentDestination?.route != route)
            innerNav.navigate(route) { launchSingleTop = true }
    }

    /* Logout listener */
    LaunchedEffect(Unit) {
        mainVm.events.collect { ev ->
            if (ev is MainEvent.SignedOut) {
                innerNav.getBackStackEntry(Screen.Home.route).viewModelStore.clear()
                rootNav.navigate(Screen.Launch.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }
    }

    /* UI */
    Scaffold(
        bottomBar = {
            BottomNavBar(
                items         = bottomItems,
                selectedRoute = routes[pagerState.currentPage],
                onItemClick   = ::goTo
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { padding ->
        HorizontalPager(
            state    = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) { page ->
            when (routes[page]) {
                Screen.Home.route     -> HomeScreen()
                Screen.Emotions.route -> Centered("Emociones")
                Screen.Habits.route   -> ShowHabitsScreen()
                Screen.Profile.route  -> Centered("Perfil")
                Screen.Settings.route -> SettingsTab(mainVm, rootNav)
            }
        }
    }
}

/* sub-composable para la pestaña Ajustes */
@Composable
private fun SettingsTab(mainVm: MainViewModel, rootNav: NavHostController) {
    val vm: SettingsViewModel = hiltViewModel()
    val uiState by vm.ui.collectAsState()   // ← sigue siendo SettingsUiState

    SettingsScreen(
        state          = uiState,           // 👈  nombre correcto + sin toUi()
        onNavigateUp   = rootNav::navigateUp,
        onEditPersonal = { /* TODO */ },
        onDevices      = { /* TODO */ },
        onAchievements = { /* TODO */ },
        onSignOut      = mainVm::signOut,
        onDelete       = { /* TODO */ },
        onNotis        = { /* TODO */ }
    )
}


/* placeholder simple */
@Composable private fun Centered(txt: String) = Box(
    Modifier.fillMaxSize(), Alignment.Center
) { androidx.compose.material3.Text(txt, fontSize = 32.sp) }
