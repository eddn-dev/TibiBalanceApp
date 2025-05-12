/**
 * @file    HabitAlertReceiver.kt
 * @ingroup receiver
 * @brief   Receiver que procesa la alarma exacta de un hábito y dispara la notificación.
 *
 * @details
 * 1. Obtiene el `habitId` del `Intent`.
 * 2. Con `goAsync()` traslada el trabajo a una corrutina en `Dispatchers.IO`
 *    para no bloquear el hilo principal del BroadcastReceiver.
 * 3. Busca el hábito en el repositorio (Room-first) y:
 *      a) Registra la actividad *ALERT* mediante [LogHabitActivityUseCase].
 *      b) Muestra la notificación personalizada con [NotificationService.showHabitReminder].
 *
 * ¡IMPORTANTE! Llamar siempre a `result.finish()` cuando se termina,
 * de acuerdo con la documentación de `BroadcastReceiver`.
 */
package com.app.tibibalance.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.app.tibibalance.core.notifications.NotificationService
import com.app.tibibalance.domain.usecase.LogHabitActivityUseCase
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.domain.model.HabitActivity
import kotlinx.coroutines.flow.firstOrNull

@AndroidEntryPoint
class HabitAlertReceiver : BroadcastReceiver() {

    @Inject lateinit var habitRepo : HabitRepository
    @Inject lateinit var logUseCase: LogHabitActivityUseCase
    @Inject lateinit var notifSvc  : NotificationService

    override fun onReceive(ctx: Context, intent: Intent?) {
        val result = goAsync()          // permite trabajo asíncrono

        val habitId = intent?.getStringExtra("habitId").orEmpty()
        if (habitId.isBlank()) {
            Log.w(TAG, "⛔ habitId vacío, se descarta la alarma")
            result.finish()
            return
        }
        Log.i(TAG, "🔔 alarma recibida para id=$habitId")

        // corrutina aislada para procesar sin bloquear el hilo de broadcast
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                // ── 1 ▸ obtener el hábito localmente (Room) ───────────────
                val habit = habitRepo.observeHabit(habitId).firstOrNull()
                if (habit == null) {
                    Log.e(TAG, "❓ Hábito $habitId no encontrado.")
                    return@launch
                }

                // ── 2 ▸ registrar ALERT en el repositorio ────────────────
                logUseCase(habitId, HabitActivity.Type.ALERT)
                Log.d(TAG, "✅ ALERT registrado en repo ($habitId)")

                // ── 3 ▸ notificar al usuario ─────────────────────────────
                notifSvc.showHabitReminder(habit)

            } catch (e: Exception) {
                Log.e(TAG, "💥 Error al procesar alarma", e)
            } finally {
                result.finish()        // ← ¡siempre!
            }
        }
    }

    private companion object { const val TAG = "HabitAlertRcvr" }
}
