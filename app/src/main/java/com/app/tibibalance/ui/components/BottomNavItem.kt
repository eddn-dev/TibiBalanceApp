package com.app.tibibalance.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import com.app.tibibalance.ui.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

data class BottomNavItem(
    val route : String,
    val icon  : ImageVector,
    val label : String
)

val bottomItems = listOf(
    BottomNavItem(Screen.Emotions.route, Icons.Filled.DateRange, "Emociones"),
    BottomNavItem(Screen.Habits.route,   Icons.AutoMirrored.Filled.List, "Hábitos"),
    BottomNavItem(Screen.Home.route,     Icons.Filled.Home,  "Inicio"),
    BottomNavItem(Screen.Profile.route,  Icons.Filled.Person,"Perfil"),
    BottomNavItem(Screen.Settings.route, Icons.Filled.Settings,"Ajustes")
)
