package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.domain.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigureNotificationViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<List<Habit>>(emptyList())
    val ui: StateFlow<List<Habit>> = _ui.asStateFlow()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        repo.observeHabits()
            .catch { e ->
                // Puedes usar un logger o emitir un estado vacío
                e.printStackTrace()
                _ui.value = emptyList()
            }
            .onEach { habitList ->
                _ui.value = habitList
            }
            .launchIn(viewModelScope)
    }

    fun toggleNotification(habit: Habit) {
        val updated = habit.copy(
            notifConfig = habit.notifConfig.copy(
                enabled = !habit.notifConfig.enabled
            )
        )
        viewModelScope.launch {
            repo.updateHabit(updated)
        }
    }
}

