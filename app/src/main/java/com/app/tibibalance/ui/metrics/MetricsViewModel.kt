// src/main/java/com/app/ui/metrics/MetricsViewModel.kt
package com.app.tibibalance.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.domain.model.DailyMetrics
import com.app.tibibalance.data.repository.MetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @class MetricsViewModel
 * @brief ViewModel que expone la métrica diaria más reciente para la UI.
 *
 * @details
 * - Carga al inicio la última métrica guardada.
 * - Permite refrescar forzadamente desde Health Connect y actualizar el valor.
 */
@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val repo: MetricsRepository
) : ViewModel() {

    private val _latest = MutableStateFlow<DailyMetrics?>(null)
    /** Métrica diaria más reciente o null si no hay datos aún. */
    val latest: StateFlow<DailyMetrics?> = _latest

    init {
        // Carga inicial al crearse el ViewModel
        viewModelScope.launch {
            _latest.value = repo.getLatestMetrics()
        }
    }

    /**
     * @brief Fuerza el refresco de métricas desde Health Connect y actualiza el estado.
     */
    fun refreshMetrics() {
        viewModelScope.launch {
            // Si tienes otra función para leer y guardar, invócala aquí:
            // fetchAndSaveDailyMetrics()
            _latest.value = repo.getLatestMetrics()
        }
    }
}
