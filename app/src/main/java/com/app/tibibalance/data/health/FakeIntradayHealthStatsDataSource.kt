// app/src/main/java/com/app/tibibalance/data/health/FakeIntradayHealthStatsDataSource.kt

package com.app.tibibalance.data.health

import com.app.tibibalance.domain.model.HealthStats
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Simula HealthStats en tiempo real:
 *  • Se reinicia cada día a medianoche.
 *  • Emite un nuevo valor cada minuto.
 *  • Pasos, calorías y ejercicio (minutes) se van acumulando.
 *  • BPM aleatorio entre 0 y 120.
 *
 * Reemplazable 1:1 por el origen real de Wear OS.
 */
class FakeIntradayHealthStatsDataSource @Inject constructor() {

    private val _stats = MutableStateFlow(HealthStats(0L, 0.0, 0.0, 0))
    val statsFlow: Flow<HealthStats> = _stats.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var lastTickDay: LocalDate = LocalDate.now()

    init {
        scope.launch {
            // emisión inicial inmediata
            emitStats()
            // luego cada minuto
            while (isActive) {
                delay(millisUntilNextMinute())
                emitStats()
            }
        }
    }

    private suspend fun emitStats() {
        val now = LocalDateTime.now()
        // si cambió de día, reiniciamos todo a cero
        if (now.toLocalDate() != lastTickDay) {
            _stats.value = HealthStats(0L, 0.0, 0.0, 0)
            lastTickDay = now.toLocalDate()
        }

        val prev = _stats.value
        // simulamos incremento de pasos (0–50 por tick)
        val stepInc = Random.nextLong(0, 50)
        val newSteps = prev.steps + stepInc
        // calorías aproximadas (0.04 kcal por paso)
        val newCalories = prev.calories + stepInc * 0.04
        // minutos de ejercicio: +1 si damos al menos 100 pasos en este tick
        val exerciseInc = if (stepInc >= 100) 1 else 0
        val newExercise = prev.exerciseMinutes + exerciseInc
        // bpm aleatorio
        val newBpm = Random.nextDouble(0.0, 120.0)

        _stats.value = HealthStats(
            steps           = newSteps,
            calories        = newCalories,
            heartRate       = newBpm,
            exerciseMinutes = newExercise
        )
    }

    /** Milisegundos hasta el siguiente inicio de minuto */
    private fun millisUntilNextMinute(): Long {
        val now = Instant.now()
        val next = now.plusSeconds(60 - (now.epochSecond % 60))
        return Duration.between(now, next).toMillis()
    }

    /** Llamar desde ViewModel.onCleared() para parar el refresco */
    fun close() {
        scope.cancel()
    }
}
