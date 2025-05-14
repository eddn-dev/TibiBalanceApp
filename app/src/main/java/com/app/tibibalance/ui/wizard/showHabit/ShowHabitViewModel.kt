/* ui/wizard/showHabit/ShowHabitViewModel.kt */
package com.app.tibibalance.ui.wizard.showHabit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowHabitViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    /* id “actual” del hábito que el modal debe mostrar */
    private val habitId = MutableStateFlow<String?>(null)

    /** Estado expuesto al Composable */
    val ui: StateFlow<ShowHabitUiState> = habitId
        .flatMapLatest { id ->
            if (id == null)                              // todavía no hay id
                flowOf(ShowHabitUiState.Loading)
            else
                repo.observeHabit(id)
                    .map<_, ShowHabitUiState> { hab ->
                        if (hab == null) ShowHabitUiState.Error("No encontrado")
                        else             ShowHabitUiState.Info(hab)
                    }
                    .onStart { emit(ShowHabitUiState.Loading) }
        }
        .catch { e -> emit(ShowHabitUiState.Error(e.message ?: "Error")) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ShowHabitUiState.Loading)

    /** Lo invoca el modal cada vez que quiere mostrar otro hábito */
    fun load(id: String) { habitId.value = id }
}
