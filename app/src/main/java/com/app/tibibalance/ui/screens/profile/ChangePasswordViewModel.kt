package com.app.tibibalance.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.domain.usecase.ChangePasswordUseCase
import com.app.tibibalance.domain.util.PasswordValidator
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun changePassword(current: String, newPass: String, confirm: String) {
        // 1) Validar coincidencia
        if (newPass != confirm) {
            _uiState.value = ChangePasswordUiState(error = "Las contraseñas no coinciden")
            return
        }

        // 2) Validar fortaleza
        PasswordValidator.validateStrength(newPass)?.let { strengthError ->
            _uiState.value = ChangePasswordUiState(error = strengthError)
            return
        }

        // 3) Re-autenticación y cambio
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState(isLoading = true)
            try {
                changePasswordUseCase(current, newPass)
                _uiState.value = ChangePasswordUiState(success = true)
            } catch (e: Exception) {
                // Diferenciar contraseña actual incorrecta
                val msg = when (e) {
                    is FirebaseAuthInvalidCredentialsException ->
                        "La contraseña actual es incorrecta"
                    else ->
                        e.message ?: "Error al cambiar contraseña"
                }
                _uiState.value = ChangePasswordUiState(error = msg)
            }
        }
    }

    fun consumeError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
