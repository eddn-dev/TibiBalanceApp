// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceRoute.kt
package com.app.tibibalance.ui.screens.conection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * @file    ConnectedDeviceRoute.kt
 * @ingroup ui_screens_conection
 * @brief   Route que conecta el ViewModel con la UI de conexión Wear OS y métricas.
 *
 * @details
 *  - Inyecta [ConnectedViewModel].
 *  - Observa los cuatro StateFlows: conexión, métricas, carga y error.
 *  - Dispara la comprobación inicial de conexión y carga de métricas.
 *  - Llama a [ConnectedDeviceScreen] pasando todos los parámetros.
 *
 * @see ConnectedViewModel
 * @see ConnectedDeviceScreen
 */
@Composable
fun ConnectedDeviceRoute(
    vm: ConnectedViewModel = hiltViewModel()
) {
    // 1. Obtenemos el estado actual de cada flujo
    val isConnected by vm.isConnected.collectAsState()
    val metrics    by vm.latest.collectAsState()
    val isLoading  by vm.isLoading.collectAsState()
    val isError    by vm.isError.collectAsState()

    // 2. Al montar la ruta, disparamos la comprobación inicial
    LaunchedEffect(Unit) {
        vm.refreshConnection()
        vm.refreshMetrics()
    }

    // 3. Invocamos la pantalla con todos los parámetros
    ConnectedDeviceScreen(
        metrics        = metrics,
        isConnected    = isConnected,
        isLoading      = isLoading,
        isError        = isError,
        onRefreshClick = {
            vm.refreshConnection()
            vm.refreshMetrics()
        }
    )
}
