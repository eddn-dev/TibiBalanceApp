// ui/screens/auth/SignInViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ui/screens/auth/SignInViewModel.kt
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val ui: StateFlow<SignInUiState> = _ui

    /* ───── Inicio sesión e-mail ───── */
    fun signIn(email: String, pass: String) = viewModelScope.launch {
        if (email.isBlank() || !email.contains("@") ||
            pass.length < 6) {
            _ui.value = SignInUiState.FieldError(
                emailError = if (!email.contains("@")) "Correo inválido" else null,
                passError  = if (pass.length < 6) "≥ 6 caracteres" else null
            )
            return@launch
        }

        try {
            _ui.value = SignInUiState.Loading
            repo.signIn(email.trim(), pass)
            val verified = repo.syncVerification()
            _ui.value = SignInUiState.Success(verified)
        } catch (e: Exception) {
            _ui.value = SignInUiState.Error(
                e.message ?: "Credenciales incorrectas"
            )
        }
    }

    /* ───── Google One-Tap ───── */
    fun finishGoogleSignIn(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignInUiState.Loading
            repo.signInGoogle(idToken)
            // Con Google la cuenta siempre llega verificada
            _ui.value = SignInUiState.Success(verified = true)
        } catch (e: Exception) {
            _ui.value = SignInUiState.Error(e.message ?: "Google error")
        }
    }

    /* helper para limpiar Error / FieldError */
    fun consumeError() {
        if (ui.value is SignInUiState.Error ||
            ui.value is SignInUiState.FieldError) {
            _ui.value = SignInUiState.Idle
        }
    }

    /* ---------- Google request ---------- */
    fun buildGoogleRequest(serverClientId: String): GetCredentialRequest =
        GetCredentialRequest(
            listOf(
                GetGoogleIdOption.Builder()
                    .setServerClientId(serverClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
        )
}