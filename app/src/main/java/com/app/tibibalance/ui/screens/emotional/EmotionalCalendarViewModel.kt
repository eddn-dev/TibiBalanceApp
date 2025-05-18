// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionalCalendarViewModel.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.EmotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * Eventos one-shot que emite el ViewModel.
 * Aquí solo tenemos dos:
 * - RegisterClicked: para abrir el modal de registro de fecha.
 * - ErrorOccurred: para notificar un fallo.
 */

@HiltViewModel
class EmotionalCalendarViewModel @Inject constructor(
    private val repository: EmotionalRepository
) : ViewModel() {

    // 1) Eventos exposables
    private val _events = MutableSharedFlow<EmotionalEvent>()
    val events: SharedFlow<EmotionalEvent> = _events.asSharedFlow()

    // 2) UI state exposes
    val ui: StateFlow<EmotionalUiState> = repository
        .observeEmotions()
        .map { records ->
            val ym = YearMonth.now()
            val daysInMonth = ym.lengthOfMonth()
            val byDay = records.associateBy { it.date.dayOfMonth }
            val daysUi = (1..daysInMonth).map { day ->
                val rec = byDay[day]
                EmotionDayUi(
                    day         = day,
                    iconRes     = rec?.iconRes,
                    isRegistered= rec != null
                )
            }
            if (records.isEmpty()) EmotionalUiState.Empty
            else                   EmotionalUiState.Loaded(daysUi)
        }
        .catch { e ->
            emit(EmotionalUiState.Error(e.message ?: "Ocurrió un error"))
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.Eagerly,
            initialValue = EmotionalUiState.Loading
        )

    /** Abre el modal de registro para `date`. */
    fun onDayClicked(date: LocalDate) = viewModelScope.launch {
        _events.emit(EmotionalEvent.RegisterClicked(date))
    }

    /** Guarda un registro (Room + Firestore). */
    fun saveEmotion(record: EmotionRecord) = viewModelScope.launch {
        try {
            repository.saveEmotion(record)
        } catch (e: Exception) {
            _events.emit(EmotionalEvent.ErrorOccurred(e.message ?: "Error guardando"))
        }
    }
}
