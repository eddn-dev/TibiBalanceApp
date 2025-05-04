/* ui/screens/settings/SettingsViewModel.kt */
package com.app.tibibalance.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                .map<_, SettingsUiState>(SettingsUiState::Ready)
                .catch { emit(SettingsUiState.Error(it.message ?: "Sin perfil")) }
                .collect { _ui.value = it }
        }
    }

    fun signOut() = viewModelScope.launch {
        _ui.value = SettingsUiState.Loading
        try {
            authRepo.signOut()
            profileRepo.clearLocal()      // limpia tabla local
            _ui.value = SettingsUiState.SignedOut
        } catch (e: Exception) {
            _ui.value = SettingsUiState.Error(e.message ?: "No se pudo cerrar la sesión")
        }
    }


    fun consumeSignedOut() { if (_ui.value is SettingsUiState.SignedOut) _ui.value = SettingsUiState.Loading }
    fun consumeError()      { if (_ui.value is SettingsUiState.Error)    _ui.value = SettingsUiState.Loading }
}
