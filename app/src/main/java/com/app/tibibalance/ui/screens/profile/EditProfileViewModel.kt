// EditProfileViewModel.kt
package com.app.tibibalance.ui.screens.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.remote.firebase.StorageService
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val storageService: StorageService
) : ViewModel() {

    // FirebaseAuth instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State: can the user change password?
    private val _canChangePassword = MutableStateFlow(false)
    val canChangePassword: StateFlow<Boolean> = _canChangePassword

    // UI state: error / success flags
    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    // Event: photo successfully updated
    private val _photoUpdatedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val photoUpdatedEvent: SharedFlow<Unit> = _photoUpdatedEvent.asSharedFlow()

    init {
        checkPasswordProvider()
    }

    /** Check if the current user has an email/password provider */
    private fun checkPasswordProvider() {
        val user = auth.currentUser
        val hasPassword = user?.providerData
            ?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } == true
        _canChangePassword.value = hasPassword
    }

    /** Load initial profile data */
    suspend fun loadInitialProfile(): UserProfile? {
        return try {
            profileRepository.profile.first()
        } catch (e: Exception) {
            _state.update { it.copy(error = "No se pudo cargar el perfil") }
            null
        }
    }

    /** Update name and/or birth date */
    fun updateProfile(name: String?, birthDate: String?) {
        viewModelScope.launch {
            try {
                profileRepository.update(
                    name = name,
                    photo = null,
                    birthDate = birthDate
                )
                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar: ${e.message}") }
            }
        }
    }

    /**
     * Upload a new profile photo, update in Firestore
     * and emit an event on success.
     */
    fun updateProfilePhoto(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                    ?: throw Exception("Usuario no autenticado")

                // Open input stream
                val inputStream = context.contentResolver
                    .openInputStream(uri)
                    ?: throw Exception("No se pudo abrir el stream de la URI")
                Log.d("EditProfileVM", "Stream abierto correctamente")

                // Upload to Storage
                val downloadUrl = storageService
                    .uploadProfileImage(inputStream, user.uid)
                Log.d("EditProfileVM", "Foto subida correctamente: $downloadUrl")

                // Update Firestore
                profileRepository.update(photo = Uri.parse(downloadUrl))
                Log.d("EditProfileVM", "Firestore actualizado con photoUrl")

                // Emit photo-updated event
                _photoUpdatedEvent.tryEmit(Unit)

            } catch (e: CancellationException) {
                // Re-throw coroutine cancellations
                throw e
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error en updateProfilePhoto", e)
                _state.update { it.copy(error = "Error al subir imagen: ${e.message}") }
            }
        }
    }

    /** Delete entire user account (Firestore doc, Storage image, FirebaseAuth) */
    fun deleteUserAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val firestore = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("profile_images/$uid.jpg")

        viewModelScope.launch {
            try {
                // Delete Firestore document
                firestore.collection("users").document(uid).delete().await()

                // Delete storage file if exists
                try {
                    storageRef.metadata.await()
                    storageRef.delete().await()
                } catch (_: Exception) {
                    // Ignore if file does not exist
                }

                // Delete FirebaseAuth user
                user.delete().await()

                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al eliminar cuenta: ${e.message}") }
            }
        }
    }

    /** Re-authenticate with email/password */
    fun reauthenticateUserWithPassword(password: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                    ?: throw Exception("Usuario no válido")
                val email = user.email
                    ?: throw Exception("Usuario sin email")
                val credential = EmailAuthProvider
                    .getCredential(email, password)
                user.reauthenticate(credential).await()
                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al reautenticar: ${e.message}") }
            }
        }
    }

    /** Re-authenticate with Google ID token */
    fun reauthenticateUserWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                    ?: throw Exception("Usuario no válido")
                val credential = GoogleAuthProvider
                    .getCredential(idToken, null)
                user.reauthenticate(credential).await()
                _state.update { it.copy(success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al reautenticar: ${e.message}") }
            }
        }
    }

    /** Clear current error from state */
    fun consumeError() {
        _state.update { it.copy(error = null) }
    }

    /** Clear success flag (used after showing dialogs) */
    fun clearSuccess() {
        _state.update { it.copy(success = false) }
    }
}
