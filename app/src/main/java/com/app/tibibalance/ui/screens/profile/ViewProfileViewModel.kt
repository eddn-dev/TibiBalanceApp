/* ui/screens/settings/SettingsViewModel.kt */
package com.app.tibibalance.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Locale

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val birthDate: String = "",
    val password: String = "" // Aquí cargaremos la contraseña (enmascarada)
)

@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState

    init {
        loadUserProfile()
    }

    // Método para cargar los datos del usuario desde Firebase
    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            _profileState.value = _profileState.value.copy(email = currentUser.email ?: "")

            // Obtener información adicional de Firestore
            firestore.collection("profiles")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val formattedBirthDate = formatDate(document.getString("birthDate") ?: "Sin fecha de nacimiento")

                        _profileState.value = ProfileState(
                            name = document.getString("userName") ?: "Usuario Anónimo",
                            email = currentUser.email ?: "No tiene correo",
                            birthDate = formattedBirthDate,
                            password = "**********" // Contraseña enmascarada
                        )
                    }
                }
                .addOnFailureListener {
                    _profileState.value = ProfileState(
                        name = "Error al cargar",
                        email = "Error",
                        birthDate = "Error",
                        password = "**********"
                    )
                }
        }
    }

    private fun formatDate(rawDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Ajusta esto al formato original si es diferente
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            rawDate // Si falla el formato, mantiene la fecha original
        }
    }
}