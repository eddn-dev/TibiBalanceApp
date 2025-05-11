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
 * @file    EmotionalCalendarViewModel.kt
 * @ingroup ui_screens_emotional
 * @brief   ViewModel para la pantalla de calendario emocional.
 *
 * @details
 * - Expone `val ui: StateFlow<EmotionalUiState>` con estados Loading/Empty/Loaded/Error
 *   generados por un flujo de registros de emociones transformado con `map`, `catch` y `stateIn`.
 * - Emite eventos one-shot (`EmotionalEvent.RegisterClicked`) a través de `val events: SharedFlow<…>`,
 *   usando un `MutableSharedFlow` interno.
 */
@HiltViewModel
class EmotionalCalendarViewModel @Inject constructor(
    private val repository: EmotionalRepository
) : ViewModel() {

    // Eventos one-shot al clicar un día
    private val _events = MutableSharedFlow<EmotionalEvent>()
    val events: SharedFlow<EmotionalEvent> = _events.asSharedFlow()

    // Estados de UI: Loading → Empty → Loaded → Error
    val ui: StateFlow<EmotionalUiState> = repository
        .observeEmotions()
        .map<List<EmotionRecord>, EmotionalUiState> { records ->
            val ym = YearMonth.now()
            val totalDays = ym.lengthOfMonth()
            val byDay = records.associateBy { it.date.dayOfMonth }

            val daysUi = (1..totalDays).map { day ->
                val rec = byDay[day]
                EmotionDayUi(
                    day = day,
                    iconRes = rec?.iconRes,
                    isRegistered = rec != null
                )
            }

            if (records.isEmpty()) EmotionalUiState.Empty
            else                      EmotionalUiState.Loaded(daysUi)
        }
        .catch { e ->
            emit(EmotionalUiState.Error(e.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = EmotionalUiState.Loading
        )

    /** Lanza el evento para que la UI abra el modal de registro de `date`. */
    fun onDayClicked(date: LocalDate) {
        viewModelScope.launch {
            _events.emit(EmotionalEvent.RegisterClicked(date))
        }
    }
}
