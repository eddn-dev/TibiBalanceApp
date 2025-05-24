// app/src/main/java/com/app/tibibalance/ui/screens/home/StatsHistoryViewModel.kt
package com.app.tibibalance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.health.FakeDailyHistoryDataSource
import com.app.tibibalance.domain.model.HealthStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * Agrupa series de datos listos para graficar:
 * - steps:  valores de pasos
 * - cals:   valores de calorías
 * - hr:     valores de frecuencia cardíaca
 * - labels: etiquetas del eje X (día del mes o nombre corto del día)
 */
data class HealthSeries2(
    val steps:  List<Float> = emptyList(),
    val cals:   List<Float> = emptyList(),
    val hr:     List<Float> = emptyList(),
    val labels: List<String> = emptyList()
)

@HiltViewModel
class StatsHistoryViewModel @Inject constructor(
    private val dataSource: FakeDailyHistoryDataSource
) : ViewModel() {

    /** Historial completo día→HealthStats */
    private val fullHistory: StateFlow<Map<LocalDate, HealthStats>> =
        dataSource.historyFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    /** Serie para la semana actual: Lunes→hoy */
    val weeklySeries: StateFlow<HealthSeries2> = fullHistory
        .map { map ->
            val today = LocalDate.now()
            // Encuentra el lunes de esta semana
            val monday = today.with(DayOfWeek.MONDAY)
            // Genera lista de fechas desde lunes hasta hoy
            val days = generateSequence(monday) { it.plusDays(1) }
                .takeWhile { !it.isAfter(today) }
                .toList()
            days.toHealthSeries(map, labelFormatter = { it.dayOfWeek.name.take(3) })
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HealthSeries2())

    /** Serie para el mes actual: día 1→hoy */
    val monthlySeries: StateFlow<HealthSeries2> = fullHistory
        .map { map ->
            val today = LocalDate.now()
            val first = today.withDayOfMonth(1)
            val days = generateSequence(first) { it.plusDays(1) }
                .takeWhile { !it.isAfter(today) }
                .toList()
            days.toHealthSeries(map, labelFormatter = { it.dayOfMonth.toString() })
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HealthSeries2())

    /**
     * Extensión para convertir lista de fechas a HealthSeries2
     */
    private fun List<LocalDate>.toHealthSeries(
        db: Map<LocalDate, HealthStats>,
        labelFormatter: (LocalDate) -> String
    ): HealthSeries2 = HealthSeries2(
        steps  = map { (db[it]?.steps ?: 0L).toFloat() },
        cals   = map { (db[it]?.calories ?: 0.0).toFloat() },
        hr     = map { (db[it]?.heartRate ?: 0.0).toFloat() },
        labels = map(labelFormatter)
    )
}
