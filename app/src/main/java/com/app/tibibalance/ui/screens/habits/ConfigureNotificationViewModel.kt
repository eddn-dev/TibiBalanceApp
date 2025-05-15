package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository
import com.app.tibibalance.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigureNotificationViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<List<Habit>>(emptyList())
    val ui: StateFlow<List<Habit>> = _ui.asStateFlow()  //Lista de habitos

    private val _selectedHabit = MutableStateFlow<Habit?>(null)
    val selectedHabit: StateFlow<Habit?> = _selectedHabit.asStateFlow()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        repo.observeHabits()
            .catch { e ->
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
    fun forceUpdateMessage(habit: Habit, newMessage: String) {
        val updated = habit.copy(
            notifConfig = habit.notifConfig.copy(message = newMessage)
        )
        viewModelScope.launch {
            repo.updateHabit(updated)
        }
    }

    fun selectHabit(habit: Habit) {
        _selectedHabit.value = habit
    }

    fun clearSelectedHabit() {
        _selectedHabit.value = null
    }

    fun updateNotificationConfig(
        habit: Habit,
        time: String,
        message: String,
        days: Set<Day>,
        mode: NotifMode,
        vibrate: Boolean,
        repeatPreset: RepeatPreset
    ) {
        val updatedHabit = habit.copy(
            repeatPreset = repeatPreset,
            notifConfig = habit.notifConfig.copy(
                message = message,
                timesOfDay = listOf(convertTo24hFormat(time)),
                weekDays = WeekDays(
                    days.map {
                        when (it) {
                            Day.L  -> 1
                            Day.M  -> 2
                            Day.MI -> 3
                            Day.J  -> 4
                            Day.V  -> 5
                            Day.S  -> 6
                            Day.D  -> 7
                        }
                    }.toSet()
                ),
                mode = mode,
                vibrate=vibrate
            )
        )

        viewModelScope.launch {
            repo.updateHabit(updatedHabit)
            clearSelectedHabit()
        }
    }
}


