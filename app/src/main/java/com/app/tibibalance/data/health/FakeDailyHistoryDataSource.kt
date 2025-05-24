// app/src/main/java/com/app/tibibalance/data/health/FakeDailyHistoryDataSource.kt
package com.app.tibibalance.data.health

import com.app.tibibalance.domain.model.HealthStats
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

/**
 * Simula un historial diario que:
 *  • Se popula desde el día 1 del mes en curso hasta hoy.
 *  • Se refresca cada hora para añadir el nuevo día cuando llegue la medianoche.
 *
 * Reemplazable 1:1 por la capa real de Wear OS.
 */
class FakeDailyHistoryDataSource @Inject constructor() {

    // Mapa interno: día → métricas fijas
    private val history: MutableMap<LocalDate, HealthStats> = mutableMapOf()

    // Flow que expone una copia inmutable del historial
    private val historyFlow =
        MutableStateFlow<Map<LocalDate, HealthStats>>(emptyMap())

    // Scope propio para el refresco periódico
    private val refresherScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        // Poblamos inicialmente hasta hoy
        ensureHistoryUpTo(LocalDate.now())
        // Cada hora revisamos si hay un nuevo día que añadir
        refresherScope.launch {
            while (isActive) {
                delay(60 * 60 * 1000) // 1 hora
                ensureHistoryUpTo(LocalDate.now())
            }
        }
    }

    /**
     * Flow que emite el historial completo cada vez que se actualiza.
     */
    fun historyFlow(): Flow<Map<LocalDate, HealthStats>> =
        historyFlow.asStateFlow()

    /**
     * Asegura que el historial contenga datos desde el día 1 del mes de `target`
     * hasta `target`, añadiendo nuevas entradas si hace falta.
     */
    private fun ensureHistoryUpTo(target: LocalDate) {
        val rng = Random(2025)               // semilla fija → valores reproducibles
        var date = target.withDayOfMonth(1)  // arrancamos en el día 1 del mes actual
        while (date <= target) {
            if (!history.containsKey(date)) {
                history[date] = HealthStats(
                    steps     = rng.nextLong(5_000, 12_000),
                    calories  = rng.nextDouble(1_600.0, 3_000.0),
                    heartRate = rng.nextDouble(60.0, 95.0),
                    exerciseMinutes = 0
                )
            }
            date = date.plusDays(1)
        }
        // Emitimos snapshot actualizado al Flow
        historyFlow.value = history.toMap()
    }

    /**
     * Cancela el refresco periódico cuando el data source ya no se necesite.
     */
    fun close() {
        refresherScope.cancel()
    }
}
