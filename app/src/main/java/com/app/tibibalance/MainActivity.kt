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
 * - Carga la navegación de la app con Jetpack Compose.
 *
 * @author
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /// Repositorio de plantillas de hábito inyectado por Hilt.
    @Inject
    lateinit var templateRepo: HabitTemplateRepository

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
                    readStepData()
                    readCaloriesExpendedData()
                    readExerciseSessionData()
                    readHeartRateData()
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
     * @brief Lee y suma los registros de pasos del día.
     *
     * - Crea un ReadRecordsRequest<StepsRecord> con rango de hoy.
     * - Llama a healthConnectClient.readRecords().
     * - Suma `record.count` de cada registro.
     * - Loggea el total de pasos.
     */
    private fun readStepData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                val request = ReadRecordsRequest<StepsRecord>(
                    StepsRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )

                val response = healthConnectClient.readRecords(request)
                var totalSteps = 0L
                for (record in response.records) {
                    totalSteps += record.count
                }
                Log.i("HealthConnect", "Total de pasos del día: $totalSteps")
            } catch (e: Exception) {
                Log.e("HealthConnect", "Error al leer los pasos", e)
            }
        }
    }

    /**
     * @brief Lee y suma las calorías activas quemadas del día.
     *
     * - Utiliza ActiveCaloriesBurnedRecord.
     * - Suma `record.energy.inCalories`.
     * - Loggea el total de calorías.
     */
    private fun readCaloriesExpendedData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                val request = ReadRecordsRequest<ActiveCaloriesBurnedRecord>(
                    ActiveCaloriesBurnedRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )

                val response = healthConnectClient.readRecords(request)
                var totalCalories = 0.0
                for (record in response.records) {
                    totalCalories += record.energy.inCalories
                }
                Log.i("HealthConnect", "Calorías activas quemadas hoy: $totalCalories")
            } catch (e: Exception) {
                Log.e("HealthConnect", "Error al leer calorías quemadas", e)
            }
        }
    }

    /**
     * @brief Lee la duración total de sesiones de ejercicio del día en minutos.
     *
     * - Usa ExerciseSessionRecord.
     * - Resta endTime - startTime y convierte a minutos.
     * - Loggea el total de minutos.
     */
    private fun readExerciseSessionData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                val request = ReadRecordsRequest<ExerciseSessionRecord>(
                    ExerciseSessionRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )

                val response = healthConnectClient.readRecords(request)
                var totalMinutes = 0L
                for (record in response.records) {
                    val durationMs = record.endTime.toEpochMilli() - record.startTime.toEpochMilli()
                    totalMinutes += durationMs / (1000 * 60)
                }
                Log.i("HealthConnect", "Minutos de ejercicio hoy: $totalMinutes")
            } catch (e: Exception) {
                Log.e("HealthConnect", "Error al leer ejercicio", e)
            }
        }
    }

    /**
     * @brief Calcula la frecuencia cardíaca promedio del día.
     *
     * - Usa HeartRateRecord.
     * - Extrae todos los samples y sus `beatsPerMinute`.
     * - Calcula el promedio y lo loggea.
     */
    private fun readHeartRateData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Instant.now()
                val startOfDay = LocalDateTime
                    .of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()

                val request = ReadRecordsRequest<HeartRateRecord>(
                    HeartRateRecord::class,
                    TimeRangeFilter.between(startOfDay, now)
                )

                val response = healthConnectClient.readRecords(request)
                val heartRates = response.records
                    .flatMap { it.samples }
                    .map { it.beatsPerMinute }

                if (heartRates.isNotEmpty()) {
                    val avg = heartRates.average()
                    Log.i("HealthConnect", "Frecuencia cardíaca promedio hoy: $avg")
                }
            } catch (e: Exception) {
                Log.e("HealthConnect", "Error al leer frecuencia cardíaca", e)
            }
        }
    }
}
