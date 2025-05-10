package com.app.tibibalance.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * @file    SettingsViewModel.kt
 * @ingroup ui_screens_settings
 * @brief   ViewModel para la pantalla de Ajustes ([SettingsScreen]).
 *
 * Observa el perfil ([UserProfile]) y maneja cierre de sesión y actualizaciones.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo   : AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepo.profile
                .filterNotNull()                                                      // ignora nulls
                .map { SettingsUiState.Ready(it) }                                    // mapea a Ready(userProfile)
                .catch { e -> _ui.value = SettingsUiState.Error(e.message ?: "Error") }
                .collect { state -> _ui.value = state }
        }
    }

    fun signOut() = viewModelScope.launch {
        _ui.value = SettingsUiState.Loading
        try {
            authRepo.signOut()
            profileRepo.clearLocal()
            _ui.value = SettingsUiState.SignedOut
        } catch (e: Exception) {
            _ui.value = SettingsUiState.Error(e.message ?: "No se pudo cerrar sesión")
        }
    }

    fun consumeSignedOut() {
        if (_ui.value is SettingsUiState.SignedOut) {
            _ui.value = SettingsUiState.Loading
        }
    }

    fun consumeError() {
        if (_ui.value is SettingsUiState.Error) {
            _ui.value = SettingsUiState.Loading
        }
    }

    /**
     * Actualiza nombre de usuario y/o fecha de nacimiento.
     */
    fun updateProfile(newName: String?, newBirthDate: String?) {
        viewModelScope.launch {
            profileRepo.update(
                name      = newName,
                photo     = null,
                birthDate = newBirthDate
            )
        }
    }
}
