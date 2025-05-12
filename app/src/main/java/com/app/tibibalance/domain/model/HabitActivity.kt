/* HabitActivity.kt */
package com.app.tibibalance.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

/**
 * @brief  Registro atómico de que un hábito “sonó” o fue “completado”.
 */
data class HabitActivity(
    val id        : String         = UUID.randomUUID().toString(),
    val habitId   : String,
    val type      : Type,          // ALERT | COMPLETED | SKIPPED
    val timestamp : Instant        = Clock.System.now()
) {
    enum class Type { ALERT, COMPLETED, SKIPPED }
}