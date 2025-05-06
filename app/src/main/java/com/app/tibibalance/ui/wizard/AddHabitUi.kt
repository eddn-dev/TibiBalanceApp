// ui/screens/habits/AddHabitUi.kt
package com.app.tibibalance.ui.wizard

import com.app.tibibalance.ui.screens.habits.Day
import com.app.tibibalance.ui.screens.habits.NotifyType

/** Pasos del wizard */
sealed interface AddHabitUiState {
    object Suggestions                       : AddHabitUiState
    data class CustomDetails(                // datos que va rellenando el usuario
        val name       : String = "",
        val desc       : String = "",
        val freq       : String = "Diario",
        val category   : String = "Salud",
        val notify     : Boolean = false
    ) : AddHabitUiState
    data class NotificationConfig(           // sólo si notify = true
        val details    : CustomDetails,
        val cfg        : NotificationCfg = NotificationCfg()
    ) : AddHabitUiState
}

/** Estructura auxiliar para la 3.ª pantalla */
data class NotificationCfg(
    val time        : String = "",
    val message     : String = "",
    val days        : Set<Day> = emptySet(),
    val repeatValue : String = "",
    val repeatUnit  : String = "",
    val types       : Set<NotifyType> = emptySet()
)
