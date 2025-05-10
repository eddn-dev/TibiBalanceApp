package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/*@HiltViewModel
class ConfigureNotificationViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Unit?>(null)
    val state: StateFlow<Unit?> = _state

    /**
     * Cambia el estado de notificaciones de un hábito.
     */
    fun onToggleNotification(habitId: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                repo.setNotificationsEnabled(habitId, enabled)
            } catch (e: Exception) {
                e.printStackTrace() // O manejar con un estado de error si deseas
            }
        }
    }
}*/