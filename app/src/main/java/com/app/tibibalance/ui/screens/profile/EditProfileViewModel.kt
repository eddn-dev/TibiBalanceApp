package com.app.tibibalance.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.remote.firebase.StorageService
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storageService: StorageService
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state

    suspend fun loadInitialProfile(): UserProfile? {
        return try {
            profileRepository.profile.first()
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "No se pudo cargar el perfil")
            null
        }
    }

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

    fun updateProfilePhoto(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: throw Exception("Usuario no autenticado")

                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("No se pudo acceder al archivo")

                val photoUrl = storageService.uploadProfileImage(inputStream, user.uid)
                profileRepository.update(photo = Uri.parse(photoUrl))

                _state.value = _state.value.copy(success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al subir imagen: ${e.message}")
            }
        }
    }

    fun deleteUserAccount() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        val firestore = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            try {
                // Elimina datos en Firestore
                firestore.collection("users").document(uid).delete().await()

                // Verifica si el archivo existe antes de intentar eliminarlo
                try {
                    storageRef.metadata.await() // Si no lanza excepción, existe
                    storageRef.delete().await()
                } catch (e: Exception) {
                    // Archivo no existe, no hacemos nada
                }

                // Elimina la cuenta del usuario
                user.delete().await()

                _state.value = _state.value.copy(success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al eliminar cuenta: ${e.message}")
            }
        }
    }


    fun reauthenticateUserWithPassword(password: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            try {
                if (user == null || user.email.isNullOrBlank())
                    throw Exception("Usuario no válido")

                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()

                _state.value = _state.value.copy(success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al reautenticar: ${e.message}")
            }
        }
    }

    fun reauthenticateUserWithGoogle(idToken: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            try {
                if (user == null)
                    throw Exception("Usuario no válido")

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                user.reauthenticate(credential).await()

                _state.value = _state.value.copy(success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al reautenticar: ${e.message}")
            }
        }
    }

    fun consumeError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}
