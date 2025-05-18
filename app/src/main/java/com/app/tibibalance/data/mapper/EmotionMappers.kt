package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.ui.screens.emotional.EmotionRecord
import com.app.tibibalance.ui.screens.emotional.Emotion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.firebase.firestore.DocumentSnapshot
import com.app.tibibalance.data.local.entity.EmotionEntity

// Formato de fecha estándar (ISO)
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

/**
 * Convierte de la entidad Room a registro de UI, reutilizando el enum Emotion.
 */
fun EmotionEntity.toDomainRecord(): EmotionRecord {
    val emotionEnum = try {
        Emotion.valueOf(this.emotion)
    } catch (_: Exception) {
        Emotion.TRANQUILIDAD
    }
    return EmotionRecord(
        date = LocalDate.parse(this.date, dateFormatter), // Convertir String a LocalDate
        iconRes = emotionEnum.drawableRes
    )
}

/**
 * Convierte de registro UI a entidad Room, guardando el nombre del enum.
 */
fun EmotionRecord.toEntity(): EmotionEntity =
    EmotionEntity(
        date = this.date.format(dateFormatter), // Convertir LocalDate a String
        emotion = Emotion.values().firstOrNull { it.drawableRes == this.iconRes }
            ?.name ?: Emotion.TRANQUILIDAD.name,
        note = null
    )

/**
 * Construye el mapa para Firestore usando el nombre del enum.
 */
fun EmotionRecord.toFirestoreMap(): Map<String, Any?> = mapOf(
    Pair("date", this.date.format(dateFormatter)), // Usar formato String compatible con Firebase
    Pair("emotion", Emotion.values().firstOrNull { it.drawableRes == this.iconRes }?.name
        ?: Emotion.TRANQUILIDAD.name),
    Pair("note", null)
)

/**
 * Convierte un DocumentSnapshot de Firestore a entidad Room.
 */
fun DocumentSnapshot.toEntityFromSnapshot(): EmotionEntity {
    val dateStr = getString("date") ?: LocalDate.now().format(dateFormatter)
    val emotionStr = getString("emotion") ?: Emotion.TRANQUILIDAD.name
    val note = getString("note")
    return EmotionEntity(
        date = dateStr, // Usar el String directamente
        emotion = emotionStr,
        note = note
    )
}
