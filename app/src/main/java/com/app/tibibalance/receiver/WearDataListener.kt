// src/main/java/com/app/tibibalance/receiver/WearDataListener.kt
package com.app.tibibalance.receiver

import android.util.Log
import com.app.tibibalance.domain.model.DailyMetrics
import com.app.tibibalance.data.repository.MetricsRepository
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * @class WearDataListener
 * @brief Servicio que escucha mensajes enviados desde el módulo Wear OS.
 *
 * @details
 * - Intercepta mensajes en el path "/daily-metrics".
 * - Decodifica el payload JSON a [DailyMetrics].
 * - Guarda las métricas en la base de datos Room a través de [MetricsRepository].
 */
@AndroidEntryPoint
class WearDataListener : WearableListenerService() {

    /// Repositorio de métricas inyectado por Hilt.
    @Inject
    lateinit var metricsRepository: MetricsRepository

    /// JSON parser con configuración por defecto.
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * @brief Evento disparado al recibir un mensaje Wearable.
     * @param event Contiene el path y payload del mensaje.
     *
     * - Solo procesa mensajes con path "/daily-metrics".
     * - Decodifica el JSON y lanza una corrutina para guardar en Room.
     */
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/daily-metrics") {
            try {
                // Decodifica el mensaje JSON
                val payloadString = event.data.decodeToString()
                val metrics = json.decodeFromString<DailyMetrics>(payloadString)

                // Guarda en Room en hilo de IO
                CoroutineScope(Dispatchers.IO).launch {
                    metricsRepository.saveMetrics(metrics)
                    Log.i("WearDataListener", "Métricas recibidas y guardadas: $metrics")
                }
            } catch (e: Exception) {
                Log.e("WearDataListener", "Error al procesar mensaje Wear", e)
            }
        } else {
            super.onMessageReceived(event)
        }
    }
}
