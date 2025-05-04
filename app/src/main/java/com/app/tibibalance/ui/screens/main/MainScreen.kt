@file:OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)

package com.app.tibibalance.ui.screens.main

/* ---- imports habituales (sin cambios) ---- */
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.app.tibibalance.ui.components.BottomNavBar
import com.app.tibibalance.ui.components.bottomItems
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.home.HomeScreen

@Composable
fun MainScreen() {

    /* ---------- rutas ---------- */
    val routes = listOf(
        Screen.Emotions.route,
        Screen.Habits.route,
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    /* ---------- controladores ---------- */
    val nav = rememberNavController()
    val pagerState = rememberPagerState(
        initialPage = 2,
        pageCount   = { routes.size }
    )
    val scope = rememberCoroutineScope()

    /* ---------- NavHost sombra (¡sin UI!) ---------- */
    NavHost(
        navController    = nav,
        startDestination = Screen.Home.route,
        modifier         = Modifier.height(0.dp)   // ocupa 0 px
    ) {
        routes.forEach { r -> composable(r) { /* vacío */ } }
    }

    /* ---------- BottomBar → Pager ---------- */
    fun goTo(route: String) {
        val index = routes.indexOf(route)
        if (index >= 0) scope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    /* ---------- Pager → back-stack ---------- */
    LaunchedEffect(pagerState.currentPage) {
        val route = routes[pagerState.currentPage]
        if (nav.currentDestination?.route != route) {
            nav.navigate(route) { launchSingleTop = true }
        }
    }

    /* ---------- UI ---------- */
    Scaffold(
        bottomBar = {
            BottomNavBar(
                items         = bottomItems,
                selectedRoute = routes[pagerState.currentPage],
                onItemClick   = ::goTo
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { inner ->

        HorizontalPager(
            state    = pagerState,
            modifier = Modifier
                .padding(inner)
                .fillMaxSize(),
            pageSize = PageSize.Fill
        ) { page ->
            when (routes[page]) {
                Screen.Home.route     -> HomeScreen()
                Screen.Emotions.route -> EmotionsScreen()
                Screen.Habits.route   -> HabitsScreen()
                Screen.Profile.route  -> ProfileScreen()
                Screen.Settings.route -> SettingsScreen()
            }
        }
    }
}

/* ------- placeholders ------- */
@Composable private fun EmotionsScreen() = Centered("Emociones")
@Composable private fun HabitsScreen()   = Centered("Hábitos")
@Composable private fun ProfileScreen()  = Centered("Perfil")
@Composable private fun SettingsScreen() = Centered("Ajustes")

@Composable
private fun Centered(txt: String) = Box(
    Modifier.fillMaxSize(), Alignment.Center
) { Text(txt, fontSize = 32.sp) }
