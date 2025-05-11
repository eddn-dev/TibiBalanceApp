package com.app.tibibalance.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state

    /**
     * Carga los datos actuales del perfil desde el repositorio.
     */
    suspend fun loadInitialProfile(): UserProfile? {
        return try {
            profileRepository.profile.first()
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "No se pudo cargar el perfil")
            null
        }
    }

    /**
     * Actualiza nombre o fecha de nacimiento si hay cambios.
     */
    fun updateProfile(name: String?, birthDate: String?) {
        viewModelScope.launch {
            try {
                profileRepository.update(
                    name = name.takeIf { it?.isNotBlank() == true },
                    photo = null,
                    birthDate = birthDate
                )
                _state.value = _state.value.copy(success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al actualizar: ${e.message}")
            }
        }
    }

    /**
     * Sube una nueva foto de perfil al repositorio.
     */
    fun updateProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                profileRepository.update(photo = uri)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al subir la foto: ${e.message}")
            }
        }
    }

    /**
     * Limpia el mensaje de error actual.
     */
    fun consumeError() {
        _state.value = _state.value.copy(error = null)
    }
}
