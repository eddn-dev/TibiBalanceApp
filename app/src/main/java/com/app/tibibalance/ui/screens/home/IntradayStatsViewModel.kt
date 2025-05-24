package com.app.tibibalance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.health.FakeIntradayHealthStatsDataSource
import com.app.tibibalance.domain.model.HealthStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel que expone HealthStats en tiempo real,
 * reiniciado a medianoche y actualizado cada minuto.
 */
@HiltViewModel
class IntradayStatsViewModel @Inject constructor(
    private val source: FakeIntradayHealthStatsDataSource
) : ViewModel() {

    /**
     * StateFlow con el último HealthStats emitido.
     * Se inicia en cero y se actualiza con cada tick del source.
     */
    val stats: StateFlow<HealthStats> = source.statsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HealthStats(0L, 0.0, 0.0,0)
        )

    override fun onCleared() {
        super.onCleared()
        source.close()
    }
}
