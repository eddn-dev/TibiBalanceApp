package com.app.tibibalance.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch      //  <--  ¡ESTE IMPORT FALTABA!
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo   : AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    /* ---------- STATE ---------- */
    private val _ui = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    /* ---------- INIT ---------- */
    init {
        profileRepo.profile
            .filterNotNull()                               // Flow<UserProfile>
            .map { prof: UserProfile ->                   // → Flow<SettingsUiState>
                SettingsUiState.Ready(prof)
            }
            .onStart  { _ui.value = SettingsUiState.Loading }
            .catch    { e -> _ui.value = SettingsUiState.Error(e.message ?: "Error") }
            .onEach   { state -> _ui.value = state }
            .launchIn(viewModelScope)                      // <-- necesita launch import
    }

    /* ---------- ACTIONS ---------- */
    fun uploadProfilePhoto(uri: Uri) = viewModelScope.launch {
        _ui.value = SettingsUiState.Loading
        runCatching {
            profileRepo.update(name = null, photo = uri, birthDate = null)
        }.onFailure { e ->
            _ui.value = SettingsUiState.Error(e.message ?: "Error al subir foto")
        }
    }

    fun updateProfile(newName: String?, newBirthDate: String?) = viewModelScope.launch {
        _ui.value = SettingsUiState.Loading
        runCatching {
            profileRepo.update(
                name      = newName,
                photo     = null,
                birthDate = newBirthDate
            )
        }.onFailure { e ->
            _ui.value = SettingsUiState.Error(e.message ?: "Error al actualizar perfil")
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

    /* ---------- CONSUMERS ---------- */
    fun consumeSignedOut() { if (_ui.value is SettingsUiState.SignedOut) _ui.value = SettingsUiState.Loading }
    fun consumeError()     { if (_ui.value is SettingsUiState.Error)     _ui.value = SettingsUiState.Loading }
}
