package com.app.tibibalance.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.remote.firebase.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _ui = mutableStateOf<RecoverPasswordUiState>(RecoverPasswordUiState.Idle)
    val ui: State<RecoverPasswordUiState> get() = _ui

    fun resetPassword(password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _ui.value = RecoverPasswordUiState.Error("Las contraseñas no coinciden")
            return
        }
        if (password.length < 6) {
            _ui.value = RecoverPasswordUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _ui.value = RecoverPasswordUiState.Loading
            try {
                authService.updatePassword(password) // Asegúrate de que este método esté definido en tu AuthService
                _ui.value = RecoverPasswordUiState.Success
            } catch (e: Exception) {
                _ui.value = RecoverPasswordUiState.Error("Error al actualizar la contraseña: ${e.message}")
            }
        }
    }

    fun consumeError() {
        _ui.value = RecoverPasswordUiState.Idle
    }

    fun dismissSuccess() {
        _ui.value = RecoverPasswordUiState.Idle
    }
}
