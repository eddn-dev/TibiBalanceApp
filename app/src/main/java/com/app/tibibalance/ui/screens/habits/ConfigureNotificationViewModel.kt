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
                habitList.forEach { habit ->
                    android.util.Log.d(
                        "VM",
                        "Habit: ${habit.name}, Times: ${habit.notifConfig.timesOfDay}, Msg: '${habit.notifConfig.message}', Days: ${habit.notifConfig.weekDays.days}, Mode: ${habit.notifConfig.mode}, Vibrate: ${habit.notifConfig.vibrate}"
                    )
                }

                _ui.value = habitList
            }
            .launchIn(viewModelScope)
    }

    fun toggleNotification(habit: Habit) {
        val updated = habit.copy(
            notifConfig = habit.notifConfig.copy(enabled = !habit.notifConfig.enabled)
        )
        viewModelScope.launch {
            repo.updateHabit(updated)
        }
    }


/*
    private val _selectedHabit = MutableStateFlow<Habit?>(null)
    val selectedHabit: StateFlow<Habit?> = _selectedHabit.asStateFlow()

    fun selectHabit(habit: Habit) {
        _selectedHabit.value = habit
    }

    fun clearSelectedHabit() {
        _selectedHabit.value = null
    }
*/
}

