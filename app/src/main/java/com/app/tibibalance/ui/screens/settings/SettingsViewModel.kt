package com.app.tibibalance.ui.screens.settings

import com.app.tibibalance.domain.usecase.SignOutUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.domain.usecase.GetProfileUseCase
import com.app.tibibalance.domain.usecase.ObserveAuthStateUseCase
import com.app.tibibalance.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val ui: StateFlow<SettingsUiState> = _uiState

    init {
        observeAuthState()
        getUserProfile()
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

    private fun getUserProfile() {
        viewModelScope.launch {
            try {
                val profile = getProfileUseCase()
                _uiState.value = SettingsUiState.Ready(profile)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Error al cargar el perfil")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                deleteAccountUseCase()
                signOutUseCase() // <- Esto cierra la sesión y dispara el evento en MainViewModel
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
