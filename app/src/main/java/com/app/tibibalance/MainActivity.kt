package com.app.tibibalance

import android.os.Bundle
import android.util.Log
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
 * - Inicializa Hilt para inyección de dependencias.
 * - Configura Health Connect para lectura de datos de salud (pasos, calorías, ejercicio, frecuencia cardíaca).
 * - Lee y guarda las métricas diarias en la base de datos Room.
 * - Carga la navegación de la app con Jetpack Compose.
 *
 * @author
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /// Repositorio de plantillas de hábito inyectado por Hilt.
    @Inject
    lateinit var templateRepo: HabitTemplateRepository

    /// Repositorio de métricas inyectado por Hilt.
    @Inject
    lateinit var metricsRepository: MetricsRepository

    /// Launcher para solicitar múltiples permisos en tiempo de ejecución.
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    /// Cliente de Health Connect para acceder a datos de salud.
    private lateinit var healthConnectClient: HealthConnectClient

    /**
     * @brief Punto de entrada al crear la actividad.
     * @param savedInstanceState Bundle con estado previo (si existe).
     *
     * - Configura edge-to-edge.
     * - Sincroniza plantillas una sola vez y comienza listener.
     * - Inicializa Health Connect.
     * - Prepara el launcher de permisos.
     * - Monta la UI con Compose.
     * - Solicita permisos de Health Connect.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Sincronización inicial de plantillas
        lifecycleScope.launch(Dispatchers.IO) {
            templateRepo.refreshOnce()
            templateRepo.startSync(this)
        }

        // Inicializa Health Connect Client
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        // Configura el launcher para permisos de Health Connect
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result.all { it.value }) {
                    Log.i("HealthConnect", "Permisos concedidos")
                    fetchAndSaveDailyMetrics()
                } else {
                    Log.w("HealthConnect", "Permisos denegados: $result")
                }
            }

        // Monta la UI de Compose
        setContent {
            TibiBalanceTheme {
                AppNavGraph()
            }
        }

        // Define y solicita los permisos necesarios
        val permissions = arrayOf(
            "android.permission.health.READ_STEPS",
            "android.permission.health.READ_EXERCISE",
            "android.permission.health.READ_CALORIES_EXPENDED",
            "android.permission.health.READ_HEART_RATE"
        )
        requestPermissionLauncher.launch(permissions)
    }

    /**
     * @brief Lee todas las métricas diarias (pasos, calorías activas, minutos de ejercicio,
     *        frecuencia cardíaca promedio) y las guarda en la base de datos.
     *
     * - Crea un ReadRecordsRequest para cada tipo de registro con rango de hoy.
     * - Suma o procesa los datos según corresponda.
     * - Construye un objeto DailyMetrics.
     * - Llama a MetricsRepository.saveMetrics() para persistir los datos.
     */
    private fun fetchAndSaveDailyMetrics() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                // --- Lectura de pasos ---
                val stepsRequest = ReadRecordsRequest<StepsRecord>(
                    StepsRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )
                val stepsResponse = healthConnectClient.readRecords(stepsRequest)
                val totalSteps = stepsResponse.records.sumOf { it.count }

                // --- Lectura de calorías activas ---
                val caloriesRequest = ReadRecordsRequest<ActiveCaloriesBurnedRecord>(
                    ActiveCaloriesBurnedRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )
                val caloriesResponse = healthConnectClient.readRecords(caloriesRequest)
                val totalCalories = caloriesResponse.records.sumOf { it.energy.inCalories }

                // --- Lectura de ejercicio ---
                val exerciseRequest = ReadRecordsRequest<ExerciseSessionRecord>(
                    ExerciseSessionRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )
                val exerciseResponse = healthConnectClient.readRecords(exerciseRequest)
                val totalMinutes = exerciseResponse.records
                    .sumOf { record ->
                        val durationMs = record.endTime.toEpochMilli() - record.startTime.toEpochMilli()
                        durationMs / (1000 * 60)
                    }

                // --- Lectura de frecuencia cardíaca ---
                val hrRequest = ReadRecordsRequest<HeartRateRecord>(
                    HeartRateRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )
                val hrResponse = healthConnectClient.readRecords(hrRequest)
                val allBeats = hrResponse.records
                    .flatMap { it.samples }
                    .map { it.beatsPerMinute }
                val avgHeartRate = if (allBeats.isNotEmpty()) allBeats.average() else null

                // --- Construcción del modelo y guardado ---
                val today = LocalDateTime.now().toLocalDate().toString()
                val daily = DailyMetrics(
                    date = today,
                    steps = totalSteps,
                    activeCalories = totalCalories,
                    exerciseMinutes = totalMinutes,
                    avgHeartRate = avgHeartRate
                )

                metricsRepository.saveMetrics(daily)
                Log.i("HealthConnect", "Métricas diarias guardadas: $daily")

            } catch (e: Exception) {
                Log.e("HealthConnect", "Error al leer o guardar métricas", e)
            }
        }
    }
}
