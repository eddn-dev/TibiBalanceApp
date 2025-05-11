// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionRecord.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.annotation.DrawableRes
import java.time.LocalDate

/**
 * Registro emocional de un día: fecha e icono asociado.
 */
data class EmotionRecord(
    val date: LocalDate,
    @DrawableRes val iconRes: Int
)
