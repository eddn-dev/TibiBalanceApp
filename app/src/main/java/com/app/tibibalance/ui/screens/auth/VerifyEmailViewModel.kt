// ui/screens/auth/VerifyEmailViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    val email: String? get() = auth.currentUser?.email

    /** Reenvía el correo de verificación  */
    fun resend(onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        try {
            auth.currentUser?.sendEmailVerification()?.await()
            onResult(true, "Correo reenviado")
        } catch (e: Exception) {
            onResult(false, e.localizedMessage ?: "Error al reenviar")
        }
    }

    /** Pregunta nuevamente a Firebase si el usuario ya verificó */
    fun check(
        onVerified: () -> Unit,
        onNotYet: () -> Unit
    ) = viewModelScope.launch {
        auth.currentUser?.reload()?.await()
        if (auth.currentUser?.isEmailVerified == true) {
            repo.syncVerification()              // actualiza Firestore
            onVerified()
        } else onNotYet()
    }

    fun signOut() = repo.signOut()
}
