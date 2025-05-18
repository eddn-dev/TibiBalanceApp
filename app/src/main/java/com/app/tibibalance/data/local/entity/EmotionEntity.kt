package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "emotions")
data class EmotionEntity(
    @PrimaryKey @DocumentId val id: String = "", // Compatible con Firebase y Room
    var date: String = "",  // Almacenado como String para compatibilidad
    val emotion: String = "",
    val note: String? = null
) {
    // Formato estándar para la fecha (ISO)
    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    // Métodos para manejar LocalDate directamente
    fun getLocalDate(): LocalDate {
        return LocalDate.parse(date, formatter)
    }

    fun setLocalDate(localDate: LocalDate) {
        date = localDate.format(formatter)
    }
}
