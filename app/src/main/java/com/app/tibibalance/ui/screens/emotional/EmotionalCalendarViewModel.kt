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
 * Eventos one-shot que emite el ViewModel para la UI.
 */
sealed class EmotionalEvent {
    /** Click en un día para abrir el modal. */
    data class RegisterClicked(val date: LocalDate) : EmotionalEvent()
    /** Guardado exitoso de la emoción. */
    data class SaveCompleted(val date: LocalDate)  : EmotionalEvent()
    /** Ocurrió un error al guardar. */
    data class ErrorOccurred(val message: String)  : EmotionalEvent()
}

@HiltViewModel
class EmotionalCalendarViewModel @Inject constructor(
    private val repository: EmotionalRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<EmotionalEvent>()
    val events: SharedFlow<EmotionalEvent> = _events.asSharedFlow()

    val ui: StateFlow<EmotionalUiState> = repository
        .observeEmotions()
        .map { records ->
            val ym = YearMonth.now()
            val totalDays = ym.lengthOfMonth()
            val byDay = records.associateBy { it.date.dayOfMonth }
            val daysUi = (1..totalDays).map { day ->
                val rec = byDay[day]
                EmotionDayUi(
                    day         = day,
                    iconRes     = rec?.iconRes,
                    isRegistered= rec != null
                )
            }
            if (records.isEmpty()) EmotionalUiState.Empty
            else                      EmotionalUiState.Loaded(daysUi)
        }
        .catch { e ->
            emit(EmotionalUiState.Error(e.message ?: "Ocurrió un error"))
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.Eagerly,
            initialValue = EmotionalUiState.Loading
        )

    fun onDayClicked(date: LocalDate) = viewModelScope.launch {
        _events.emit(EmotionalEvent.RegisterClicked(date))
    }

    fun saveEmotion(record: EmotionRecord) = viewModelScope.launch {
        try {
            repository.saveEmotion(record)
            _events.emit(EmotionalEvent.SaveCompleted(record.date))
        } catch (e: Exception) {
            _events.emit(EmotionalEvent.ErrorOccurred(e.message ?: "Error guardando"))
        }
    }
}
