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

    fun signIn(email: String, pass: String) = viewModelScope.launch {
        /* ---- validación local ---- */
        if (email.isBlank() || !email.contains("@") || pass.length < 6) {
            _ui.value = SignInUiState.FieldError(
                emailError = if (!email.contains("@")) "Correo inválido" else null,
                passError  = if (pass.length < 6) "≥ 6 caracteres" else null
            )
            return@launch
        }

        try {
            _ui.value = SignInUiState.Loading
            repo.signIn(email.trim(), pass)          // <-- llamada al backend
            val verified = repo.syncVerification()
            _ui.value = SignInUiState.Success(verified)   // ← éxito
        } catch (e: Exception) {
            when (e) {

                // Cuenta borrada o nunca creada
                is FirebaseAuthInvalidUserException ->
                    _ui.value = SignInUiState.FieldError(
                        emailError = "La cuenta no existe o está deshabilitada"
                    )

                is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {

                    "ERROR_INVALID_EMAIL"  -> _ui.value =
                        SignInUiState.FieldError(emailError = "Correo mal formado")

                    "ERROR_INVALID_LOGIN_CREDENTIALS",
                    "ERROR_INVALID_CREDENTIAL" -> _ui.value =
                        SignInUiState.FieldError(
                            emailError = "Revisa correo o contraseña",
                            passError  = ""
                        )

                    else -> _ui.value =
                        SignInUiState.Error("Credenciales inválidas (${e.errorCode})")
                }

                is FirebaseNetworkException ->
                    _ui.value = SignInUiState.Error("Sin conexión. Intenta de nuevo")

                else ->
                    _ui.value = SignInUiState.Error(e.message ?: "Error desconocido")
            }
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