// src/main/java/com/app/tibibalance/MainActivity.kt
package com.app.tibibalance

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.app.tibibalance.data.repository.HabitTemplateRepository
import com.app.tibibalance.data.repository.MetricsRepository
import com.app.tibibalance.domain.model.DailyMetrics
import com.app.tibibalance.ui.navigation.AppNavGraph
import com.app.tibibalance.ui.theme.TibiBalanceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.request.ReadRecordsRequest

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * @class MainActivity
 * @brief Actividad principal de TibiBalance.
 *
 * @details
 * - Configura edge-to-edge.
 * - Sincroniza plantillas de hábito.
 * - Inicializa Health Connect (si está instalado), atrapando excepción si no lo está.
 * - Solicita permisos de Health Connect.
 * - Lee y guarda métricas diarias.
 * - Monta el grafo de navegación Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var templateRepo: HabitTemplateRepository

    @Inject
    lateinit var metricsRepository: MetricsRepository

    /// Launcher para solicitar permisos de Health Connect.
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    /// Cliente de Health Connect, o null si no está disponible.
    private var healthConnectClient: HealthConnectClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Sincronización de plantillas
        lifecycleScope.launch(Dispatchers.IO) {
            templateRepo.refreshOnce()
            templateRepo.startSync(this)
        }

        // Inicializa Health Connect atrapando IllegalStateException si falta el servicio
        healthConnectClient = try {
            HealthConnectClient.getOrCreate(this)
        } catch (e: IllegalStateException) {
            Toast.makeText(
                this,
                "Health Connect no está disponible en este dispositivo",
                Toast.LENGTH_LONG
            ).show()
            Log.w("MainActivity", "Health Connect no disponible", e)
            null
        }

        // Configura el launcher de permisos
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result.all { it.value }) {
                    Log.i("HealthConnect", "Permisos concedidos")
                    fetchAndSaveDailyMetrics()
                } else {
                    Log.w("HealthConnect", "Permisos denegados: $result")
                }
            }

        // Monta la UI
        setContent {
            TibiBalanceTheme {
                AppNavGraph()
            }
        }

        // Define y lanza petición de permisos
        val permissions = arrayOf(
            "android.permission.health.READ_STEPS",
            "android.permission.health.READ_EXERCISE",
            "android.permission.health.READ_CALORIES_EXPENDED",
            "android.permission.health.READ_HEART_RATE"
        )
        requestPermissionLauncher.launch(permissions)
    }

    /**
     * @brief Lee y guarda métricas diarias desde Health Connect.
     *
     * - Si no hay cliente, sale sin hacer nada.
     * - Lee pasos, calorías, ejercicio y frecuencia cardiaca.
     * - Persiste un [DailyMetrics] en la base de datos.
     */
    private fun fetchAndSaveDailyMetrics() {
        val client = healthConnectClient ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                // Pasos
                val stepsResp = client.readRecords(
                    ReadRecordsRequest(
                        recordType = StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                    )
                )
                val totalSteps = stepsResp.records.sumOf { it.count }

                // Calorías activas
                val calResp = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ActiveCaloriesBurnedRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                    )
                )
                val totalCalories = calResp.records.sumOf { it.energy.inCalories }

                // Ejercicio
                val exResp = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ExerciseSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                    )
                )
                val totalMinutes = exResp.records.sumOf { rec ->
                    val durMs = rec.endTime.toEpochMilli() - rec.startTime.toEpochMilli()
                    durMs / (1000 * 60)
                }

                // Frecuencia cardiaca
                val hrResp = client.readRecords(
                    ReadRecordsRequest(
                        recordType = HeartRateRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                    )
                )
                val allBeats = hrResp.records
                    .flatMap { it.samples }
                    .map { it.beatsPerMinute }
                val avgHr = if (allBeats.isNotEmpty()) allBeats.average() else null

                // Construye y guarda
                val today = LocalDateTime.now().toLocalDate().toString()
                val metrics = DailyMetrics(
                    date = today,
                    steps = totalSteps,
                    activeCalories = totalCalories,
                    exerciseMinutes = totalMinutes,
                    avgHeartRate = avgHr
                )
                metricsRepository.saveMetrics(metrics)
                Log.i("HealthConnect", "Métricas guardadas: $metrics")

            } catch (e: Exception) {
                Log.e("HealthConnect", "Error leyendo o guardando métricas", e)
            }
        }
    }
}
