// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedViewModel.kt
package com.app.tibibalance.ui.screens.conection

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.MetricsRepository
import com.app.tibibalance.data.wear.WearConnectionHelper
import com.app.tibibalance.domain.model.DailyMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gestiona:
 *  - isConnected: si el Wear OS está emparejado y al alcance.
 *  - latest: últimas métricas obtenidas.
 *  - isLoading/isError: estados de carga de métricas.
 */
@HiltViewModel
class ConnectedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: MetricsRepository
) : ViewModel() {

    // Estado de conexión
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Métricas
    private val _latest = MutableStateFlow<DailyMetrics?>(null)
    val latest: StateFlow<DailyMetrics?> = _latest

    // Carga / error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    init {
        // Primer chequeo al arrancar
        refreshConnection()
        refreshMetrics()
    }

    /** Comprueba si el Wear OS está conectado */
    fun refreshConnection() {
        viewModelScope.launch {
            _isConnected.value = WearConnectionHelper.isWearConnected(context)
        }
    }

    /** Refresca las métricas desde el repositorio */
    fun refreshMetrics() {
        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            runCatching {
                repo.getLatestMetrics()
            }.onSuccess { metrics ->
                _latest.value = metrics
            }.onFailure { throwable ->
                Log.e("ConnectedViewModel", "Error al refrescar métricas", throwable)
                _latest.value = null
                _isError.value = true
            }
            _isLoading.value = false
        }
    }
}
