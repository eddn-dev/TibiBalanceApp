package com.app.tibibalance.ui.screens.settings

import com.app.tibibalance.domain.usecase.ObserveProfileUseCase
import com.app.tibibalance.domain.usecase.ObserveAuthStateUseCase
import com.app.tibibalance.domain.usecase.DeleteAccountUseCase
import com.app.tibibalance.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val ui: StateFlow<SettingsUiState> = _uiState

    init {
        observeAuthState()
        observeUserProfile()   // ← ahora usamos flujo continuo
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { isLoggedIn ->
                if (!isLoggedIn) {
                    _uiState.value = SettingsUiState.SignedOut
                }
            }
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            observeProfileUseCase()
                .collect { profile ->
                    if (profile != null) {
                        _uiState.value = SettingsUiState.Ready(profile)
                    } else {
                        _uiState.value = SettingsUiState.Error("No se encontró perfil")
                    }
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                deleteAccountUseCase()
                signOutUseCase()
            } catch (e: Exception) {
                val message = if (e.message?.contains("recent") == true) {
                    "Debes volver a iniciar sesión para eliminar tu cuenta."
                } else {
                    "Error al eliminar la cuenta: ${e.message}"
                }
                _uiState.value = SettingsUiState.Error(message)
            }
        }
    }
}
