package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.EmotionEntity
import com.app.tibibalance.ui.screens.emotional.EmotionRecord
import com.app.tibibalance.ui.screens.emotional.Emotion
import java.time.LocalDate
import com.google.firebase.firestore.DocumentSnapshot

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
        date = this.date,
        iconRes = emotionEnum.drawableRes
    )
}

/**
 * Convierte de registro UI a entidad Room, guardando el nombre del enum.
 */
fun EmotionRecord.toEntity(): EmotionEntity =
    EmotionEntity(
        date = this.date,
        emotion = Emotion.values().firstOrNull { it.drawableRes == this.iconRes }
            ?.name ?: Emotion.TRANQUILIDAD.name,
        note = null
    )

/**
 * Construye el mapa para Firestore usando el nombre del enum.
 */
fun EmotionRecord.toFirestoreMap(): Map<String, Any?> = mapOf(
    Pair("date", this.date.toString()),
    Pair("emotion", Emotion.values().firstOrNull { it.drawableRes == this.iconRes }?.name
        ?: Emotion.TRANQUILIDAD.name),
    Pair("note", null)
)

/**
 * Convierte un DocumentSnapshot de Firestore a entidad Room.
 */
fun DocumentSnapshot.toEntityFromSnapshot(): EmotionEntity {
    val date = LocalDate.parse(id)
    val emotionStr = getString("emotion") ?: Emotion.TRANQUILIDAD.name
    val note = getString("note")
    return EmotionEntity(
        date = date,
        emotion = emotionStr,
        note = note
    )
}
