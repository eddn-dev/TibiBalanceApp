// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceRoute.kt
package com.app.tibibalance.ui.screens.conection


import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

/**
 * Wrapper composable que maneja estado y navegación para la pantalla de conexión Wear/Bluetooth.
 */
@Composable
fun ConnectedDeviceRoute(
    navController: NavHostController,
    viewModel: ConnectedViewModel = hiltViewModel()
) {
    // Estado de conexión (Wear, Bluetooth, etc.)
    val isConnected by viewModel.isConnected.collectAsState()

    ConnectedDeviceScreen(
        isConnected    = isConnected,
        onRefreshClick = { viewModel.refresh() }
    )
}
