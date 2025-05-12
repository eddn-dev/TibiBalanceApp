/* core/scheduler/HabitAlertSchedulerImpl.kt */
package com.app.tibibalance.core.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.tibibalance.receiver.HabitAlertReceiver
import com.app.tibibalance.domain.model.Habit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitAlertSchedulerImpl @Inject constructor(
    private val am: AlarmManager,
    @ApplicationContext private val ctx: Context
) : HabitAlertScheduler {

    override fun schedule(habit: Habit) {
        val triggerMs = habit.nextTrigger?.toEpochMilliseconds() ?: return
        val pi = pendingIntent(habit.id, PendingIntent.FLAG_UPDATE_CURRENT)!! // ← ¡no nulo!

        try {
            // En Android 12+ el usuario puede bloquear alarmas exactas
            val exactAllowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    am.canScheduleExactAlarms()

            if (exactAllowed) {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMs,
                    pi
                )
            } else {                           // reserva inexacta
                am.set(AlarmManager.RTC_WAKEUP, triggerMs, pi)
            }
        } catch (se: SecurityException) {      // reserva si se lanza la excepción
            am.set(AlarmManager.RTC_WAKEUP, triggerMs, pi)
        }
    }

    override fun cancel(habitId: String) {
        pendingIntent(habitId, PendingIntent.FLAG_NO_CREATE)
            ?.let { am.cancel(it) }
    }

    /** Obtiene (o no) el `PendingIntent` asociado al hábito. */
    private fun pendingIntent(habitId: String, extraFlags: Int = 0): PendingIntent? =
        PendingIntent.getBroadcast(
            ctx,
            habitId.hashCode(),
            Intent(ctx, HabitAlertReceiver::class.java).putExtra("habitId", habitId),
            PendingIntent.FLAG_IMMUTABLE or extraFlags
        )
}
