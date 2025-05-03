package com.app.tibibalance.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.remote.firebase.AuthService
import com.app.tibibalance.ui.screens.auth.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _ui = mutableStateOf<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val ui: State<ForgotPasswordUiState> get() = _ui

    fun sendResetEmail(email: String) {
        viewModelScope.launch {
            _ui.value = ForgotPasswordUiState.Loading
            try {
                authService.sendPasswordReset(email)
                _ui.value = ForgotPasswordUiState.Success
            } catch (e: FirebaseAuthInvalidUserException) {
                _ui.value = ForgotPasswordUiState.Error("El correo no está registrado: ${e.message}")
            } catch (e: Exception) {
                _ui.value = ForgotPasswordUiState.Error("Error al enviar el correo: ${e.message}")
            }
        }
    }

    fun consumeError() {
        _ui.value = ForgotPasswordUiState.Idle
    }

    fun dismissSuccess() {
        _ui.value = ForgotPasswordUiState.Idle
    }
}
