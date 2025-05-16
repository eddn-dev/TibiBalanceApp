package com.app.wear.model

data class HabitActivity(
    val timestamp: Long   = 0L,    // fecha/hora de la actividad en ms
    val completed: Boolean = false // si se completó la actividad
)