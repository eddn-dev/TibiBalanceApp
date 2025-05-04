// ui/screens/auth/ForgotPasswordViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    /** Contenido del text-field “correo” (2-way binding) */
    var email by mutableStateOf("")
        private set

    /** Exponer estados a la pantalla */
    private val _ui = MutableStateFlow<ForgotUiState>(ForgotUiState.Idle)
    val ui: StateFlow<ForgotUiState> = _ui

    fun onEmailChange(newValue: String) { email = newValue.trim() }

    /** Envía el mail de recuperación (link de Firebase) */
    fun sendResetLink() {
        if (email.isBlank()) {
            _ui.value = ForgotUiState.Error("Escribe tu correo")
            return
        }

        viewModelScope.launch {
            try {
                _ui.value = ForgotUiState.Loading
                repo.resetPass(email)
                _ui.value = ForgotUiState.Success
            } catch (e: Exception) {
                _ui.value = ForgotUiState.Error(
                    e.message ?: "No se pudo enviar el correo"
                )
            }
        }
    }

    /** Limpia error o success cuando el usuario ya lo vio */
    fun clearStatus() { _ui.value = ForgotUiState.Idle }
}
