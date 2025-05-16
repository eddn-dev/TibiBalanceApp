package com.app.wear.model

data class Habit(
    val id: String        = "",   // el ID del hábito (Firestore document ID)
    val name: String      = "",   // nombre descriptivo
    val description: String = ""  // descripción o detalles
)