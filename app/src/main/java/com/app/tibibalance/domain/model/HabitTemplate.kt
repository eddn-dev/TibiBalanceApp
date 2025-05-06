// domain/model/HabitTemplate.kt
package com.app.tibibalance.domain.model

data class HabitTemplate(
    val id          : String,
    val name        : String,
    val description : String,
    val category    : String,
    val icon        : String,
    val message     : String,
    val repeatEvery : Int,
    val repeatType  : String,   // DAILY, WEEKLY…
    val notifMode   : String,   // SOUND, SILENT…
    val scheduled   : Boolean
)
