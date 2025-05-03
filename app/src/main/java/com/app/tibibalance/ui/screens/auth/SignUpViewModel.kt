package com.app.tibibalance.ui.screens.auth

import androidx.credentials.GetCredentialRequest             // ✅ Jetpack (no min-api 34)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption // implements CredentialOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val ui: StateFlow<SignUpUiState> = _ui

    /* -------- Registro con correo/contraseña -------- */
    fun signUp(
        userName: String,
        birthDate: LocalDate?,
        email: String,
        password: String,
        confirm: String
    ) {
        validate(userName, birthDate, email, password, confirm)?.let {
            _ui.value = SignUpUiState.Error(it); return
        }

        viewModelScope.launch {
            try {
                _ui.value = SignUpUiState.Loading
                repo.signUpEmail(email, password)            // firma del repo corregida
                _ui.value = SignUpUiState.Success(email)
            } catch (e: Exception) {
                _ui.value = SignUpUiState.Error(e.message ?: "Error")
            }
        }
    }

    /* -------- Google bottom-sheet (idToken llega desde la UI) -------- */
    fun finishGoogleSignUp(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignUpUiState.Loading
            repo.signInGoogle(idToken)
            _ui.value = SignUpUiState.Idle               // navegación la maneja la pantalla
        } catch (e: Exception) {
            _ui.value = SignUpUiState.Error(e.message ?: "Google error")
        }
    }

    /* --- helpers --- */
    fun consumeError()  { if (_ui.value is SignUpUiState.Error)   _ui.value = SignUpUiState.Idle }
    fun dismissSuccess(){ if (_ui.value is SignUpUiState.Success) _ui.value = SignUpUiState.Idle }

    /**
     * Construye el GetCredentialRequest para Sign-in with Google
     * @param serverClientId  Client-ID Web de OAuth2 (Google Cloud Console)
     */
    fun buildGoogleRequest(serverClientId: String): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)          // hojita siempre visible :contentReference[oaicite:1]{index=1}
            .setServerClientId(serverClientId)             // token para backend
            .build()                                       // GetGoogleIdOption IMPLEMENTA CredentialOption :contentReference[oaicite:2]{index=2}

        return GetCredentialRequest(listOf(option))        // ctor Jetpack; API-21+ :contentReference[oaicite:3]{index=3}
    }

    private fun validate(
        userName: String,
        birthDate: LocalDate?,
        email: String,
        password: String,
        confirm: String
    ): String? = when {
        userName.isBlank()      -> "El nombre de usuario es obligatorio"
        birthDate == null       -> "Selecciona tu fecha de nacimiento"
        email.isBlank()         -> "El correo es obligatorio"
        password.length < 6     -> "La contraseña debe tener al menos 6 caracteres"
        password != confirm     -> "Las contraseñas no coinciden"
        else                    -> null
    }
}
