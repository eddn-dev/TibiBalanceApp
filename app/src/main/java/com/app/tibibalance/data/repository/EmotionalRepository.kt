// src/main/java/com/app/tibibalance/data/repository/EmotionalRepository.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.ui.screens.emotional.EmotionRecord
import kotlinx.coroutines.flow.Flow

/**
 * Fuente de datos de registros emotivos.
 */
interface EmotionalRepository {
    /** Devuelve los registros (puede venir de BD, red, mocks, etc.) */
    fun observeEmotions(): Flow<List<EmotionRecord>>
}
