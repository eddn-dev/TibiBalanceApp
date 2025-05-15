package com.app.tibibalance.domain.usecase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val email = user.email ?: throw Exception("No hay correo asociado")
        // Reautentica
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()
        // Cambia la contraseña
        user.updatePassword(newPassword).await()
    }
}