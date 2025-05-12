/**
 * @file    NotificationService.kt
 * @ingroup core_notifications
 *
 * @brief   Crea el canal “habits” y dispara notificaciones de recordatorio.
 *
 * @details
 *  • Se inyecta el [NotificationManager] y el contexto de aplicación (Hilt).
 *  • El canal se construye en `init` sólo la primera vez.
 *  • API pública: [showHabitReminder] recibe el [Habit] completo y
 *    compone título + cuerpo usando el mensaje personalizado del usuario.
 *    Si no hay mensaje, aplica un fallback localizado.
 *
 *  Nota: el ícono pequeño DEBE ser un recurso *drawable* (no ImageVector).
 */
package com.app.tibibalance.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.Habit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val nm: NotificationManager            // proporcionado por SystemServicesModule
) {
    companion object {
        private const val CH_ID = "habits"
    }

    init {
        // Android 8+ requiere canal; sólo se crea si no existe
        if (nm.getNotificationChannel(CH_ID) == null) {
            val channel = NotificationChannel(
                CH_ID,
                ctx.getString(R.string.channel_habits),
                NotificationManager.IMPORTANCE_HIGH            // hace vibrar/sonar
            ).apply {
                description = ctx.getString(R.string.channel_habits)
            }
            nm.createNotificationChannel(channel)
        }
    }

    /**
     * Construye y muestra la notificación de recordatorio.
     *
     * @param habit Instancia de [Habit] con nombre, mensaje, icono y ID.
     */
    fun showHabitReminder(habit: Habit) {
        val (title, body) = composeText(habit)
        val smallIconId   = iconResId(habit.icon)

        val notif = NotificationCompat.Builder(ctx, CH_ID)
            .setSmallIcon(smallIconId)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // evita truncado
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(habit.id.hashCode(), notif)      // ID estable por hábito
    }

    /* ---------- helpers privados ---------- */

    /** Título → nombre del hábito; cuerpo → mensaje del usuario o fallback */
    private fun composeText(h: Habit): Pair<String, String> {
        val body = h.notifConfig.message.takeIf { it.isNotBlank() }
            ?: (ctx.getString(R.string.default_reminder_body) + "" + h.name)
        return h.name to body
    }

    /**
     * Mapea `iconName` a un drawable.  Busca por nombre (lowercase) y, si no se
     * encuentra, vuelve al ícono genérico `appicon`.
     */
    private fun iconResId(iconName: String): Int =
        ctx.resources.getIdentifier(iconName.lowercase(), "drawable", ctx.packageName)
            .takeIf { it != 0 } ?: R.drawable.appicon
}
