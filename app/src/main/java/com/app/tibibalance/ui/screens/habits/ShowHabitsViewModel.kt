package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository   // ← cuando lo tengas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Datos mínimos para cada fila de la lista */
data class HabitUi(
    val id      : String,
    val name    : String,
    val icon    : String,       // nombre Material, ej. "LocalDrink"
    val checked : Boolean = false
)

/** Estado que la UI observa */
sealed interface HabitsUiState {
    object Loading               : HabitsUiState
    object Empty                 : HabitsUiState
    data class Loaded(val data: List<HabitUi>) : HabitsUiState
    data class Error (val msg: String)         : HabitsUiState
}

/** Eventos one-shot hacia la UI (snackbar, navegación, etc.) */
sealed interface HabitsEvent {
    object AddClicked : HabitsEvent
}

@HiltViewModel
class ShowHabitsViewModel @Inject constructor(
    repo: HabitRepository          // tu repo real; aquí solo se simula
) : ViewModel() {

    private val _events = MutableSharedFlow<HabitsEvent>()
    val events: SharedFlow<HabitsEvent> = _events.asSharedFlow()

    /** Simulación de flujo de hábitos; reemplaza con repo.observeHabits() */
    private val habitsMock = flow {
        emit(emptyList<HabitUi>())            // empieza vacío
        // delay(1_000); emit(listOf(...))     // para probar Loaded
    }

    val ui: StateFlow<HabitsUiState> =
        habitsMock
            .map<List<HabitUi>, HabitsUiState> {
                if (it.isEmpty()) HabitsUiState.Empty
                else HabitsUiState.Loaded(it)
            }
            .catch { emit(HabitsUiState.Error(it.message ?: "Error")) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, HabitsUiState.Loading)

    fun onAddClicked() = viewModelScope.launch { _events.emit(HabitsEvent.AddClicked) }
}
