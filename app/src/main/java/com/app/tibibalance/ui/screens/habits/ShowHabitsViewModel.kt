/* ShowHabitsViewModel.kt */
package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowHabitsViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    /* ───── one-shot events ───── */
    private val _events = MutableSharedFlow<HabitsEvent>()
    val events: SharedFlow<HabitsEvent> = _events.asSharedFlow()

    /* ───── UI-state stream ───── */
    val ui: StateFlow<HabitsUiState> = repo.observeHabits()
        .map { list ->
            val uiList = list.map { it.toUi() }                      // ← tu mapper
            if (uiList.isEmpty()) HabitsUiState.Empty
            else HabitsUiState.Loaded(uiList)
        }
        .catch { e -> emit(HabitsUiState.Error(e.message ?: "Error")) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HabitsUiState.Loading)

    /* ───── UI callbacks ───── */
    fun onAddClicked()        = viewModelScope.launch { _events.emit(HabitsEvent.AddClicked) }
    fun onHabitClicked(h: HabitUi) =
        viewModelScope.launch { _events.emit(HabitsEvent.ShowDetails(h.id)) }
}

/* ──────────────────────────────── helpers & models ───────────────────────────── */

/** Mini-model que la lista necesita */
data class HabitUi(
    val id: String,
    val name: String,
    val icon: String,
    val category: String,
    val checked: Boolean = false
)

/** Estados de la pantalla */
sealed interface HabitsUiState {
    object Loading                       : HabitsUiState
    object Empty                         : HabitsUiState
    data class Loaded(val data: List<HabitUi>) : HabitsUiState
    data class Error(val msg: String)    : HabitsUiState
}

/** Eventos one-shot */
sealed interface HabitsEvent {
    /** abrir modal “nuevo hábito” */
    object AddClicked : HabitsEvent
    /** abrir modal “detalle” */
    data class ShowDetails(val habitId: String) : HabitsEvent
}
