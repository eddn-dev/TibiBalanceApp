// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceRoute.kt
package com.app.tibibalance.ui.screens.conection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.domain.model.DailyMetrics
import com.app.tibibalance.ui.metrics.MetricsViewModel

/**
 * @brief Route para mostrar la pantalla de conexión de Wear OS y métricas.
 *
 * @details
 * - Inyecta el [MetricsViewModel].
 * - Obtiene el último valor de métricas.
 * - Invoca [ConnectedDeviceScreen] pasando el objeto [DailyMetrics]? y el callback.
 */
@Composable
fun ConnectedDeviceRoute(
    viewModel: MetricsViewModel = hiltViewModel()
) {
    // Estado de métricas: null = no conectado, no null = conectado
    val metrics = viewModel.latest.collectAsState().value

    ConnectedDeviceScreen(
        metrics = metrics,
        onRefreshClick = { viewModel.refreshMetrics() }
    )
}
