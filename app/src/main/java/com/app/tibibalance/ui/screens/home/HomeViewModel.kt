package com.app.tibibalance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.request.ReadRecordsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import javax.inject.Inject

/**
 * @file    HomeViewModel.kt
 * @ingroup ui_screens_home
 * @brief   ViewModel de la pantalla Home, expone métricas de actividad.
 *
 * @details
 * - Inyecta HealthConnectClient para acceder a datos de salud.
 * - Expone StateFlows para pasos, calorías, minutos de ejercicio y frecuencia cardíaca.
 * - Al inicializarse, lanza las lecturas de todas las métricas.
 *
 * @see ReadRecordsRequest
 * @see StepsRecord
 * @see ActiveCaloriesBurnedRecord
 * @see ExerciseSessionRecord
 * @see HeartRateRecord
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthConnectClient: HealthConnectClient
) : ViewModel() {

    /// Pasos acumulados del día
    private val _steps = MutableStateFlow(0L)
    val steps: StateFlow<Long> = _steps.asStateFlow()

    /// Calorías activas quemadas del día
    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories.asStateFlow()

    /// Minutos totales de sesión de ejercicio del día
    private val _exerciseMinutes = MutableStateFlow(0L)
    val exerciseMinutes: StateFlow<Long> = _exerciseMinutes.asStateFlow()

    /// Frecuencia cardíaca promedio del día
    private val _heartRate = MutableStateFlow(0.0)
    val heartRate: StateFlow<Double> = _heartRate.asStateFlow()

    init {
        // Al crear el VM, lanzamos la obtención de todas las métricas
        fetchAllMetrics()
    }

    /**
     * @brief Lanza en paralelo la lectura de cada métrica.
     */
    private fun fetchAllMetrics() {
        viewModelScope.launch { readSteps() }
        viewModelScope.launch { readCalories() }
        viewModelScope.launch { readExercise() }
        viewModelScope.launch { readHeartRate() }
    }

    /**
     * @brief Consulta y suma los registros de pasos del día.
     */
    private suspend fun readSteps() {
        val (start, end) = todayRange()
        val req = ReadRecordsRequest<StepsRecord>(
            StepsRecord::class,
            TimeRangeFilter.between(start, end)
        )
        val resp = healthConnectClient.readRecords(req)
        _steps.value = resp.records.sumOf { it.count }
    }

    /**
     * @brief Consulta y suma las calorías activas quemadas del día.
     */
    private suspend fun readCalories() {
        val (start, end) = todayRange()
        val req = ReadRecordsRequest<ActiveCaloriesBurnedRecord>(
            ActiveCaloriesBurnedRecord::class,
            TimeRangeFilter.between(start, end)
        )
        val resp = healthConnectClient.readRecords(req)
        _calories.value = resp.records.sumOf { it.energy.inCalories }
    }

    /**
     * @brief Consulta y suma los minutos de ejercicio del día.
     */
    private suspend fun readExercise() {
        val (start, end) = todayRange()
        val req = ReadRecordsRequest<ExerciseSessionRecord>(
            ExerciseSessionRecord::class,
            TimeRangeFilter.between(start, end)
        )
        val resp = healthConnectClient.readRecords(req)
        _exerciseMinutes.value = resp.records.sumOf {
            val durationMs = it.endTime.toEpochMilli() - it.startTime.toEpochMilli()
            durationMs / (1000 * 60)
        }
    }

    /**
     * @brief Consulta y calcula la frecuencia cardíaca promedio del día.
     */
    private suspend fun readHeartRate() {
        val (start, end) = todayRange()
        val req = ReadRecordsRequest<HeartRateRecord>(
            HeartRateRecord::class,
            TimeRangeFilter.between(start, end)
        )
        val resp = healthConnectClient.readRecords(req)
        val samples = resp.records.flatMap { it.samples }.map { it.beatsPerMinute }
        _heartRate.value = samples.average().takeIf { !it.isNaN() } ?: 0.0
    }

    /**
     * @brief Devuelve el rango de tiempo desde el inicio del día hasta ahora.
     * @return Par (startInstant, endInstant)
     */
    private fun todayRange(): Pair<Instant, Instant> {
        val now = Instant.now()
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        return startOfDay to now
    }
}
