package com.app.tibibalance.ui.screens.auth

import androidx.credentials.GetCredentialRequest             // ✅ Jetpack (no min-api 34)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.domain.util.PasswordValidator
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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

    /** Reporta un error específico de Google One-Tap a la UI. */
    fun reportGoogleError(msg: String) {
        _ui.value = SignUpUiState.Error(msg)
    }

    /** Regex para validar formato de email. */
    private val EMAIL_RX =
        Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\$", RegexOption.IGNORE_CASE)

    private fun String.isValidEmail() = EMAIL_RX.matches(this)

    private fun usernameError(u: String): String? =
        when {
            u.isBlank() -> "Requerido"
            u.length < 4 -> "≥ 4 caracteres"
            !u.matches(Regex("^[a-zA-Z0-9_]+\$")) -> "Solo letras, números o _"
            else -> null
        }

    private fun birthDateError(date: LocalDate?): String? {
        date ?: return "Selecciona fecha"
        if (date.isAfter(LocalDate.now())) return "Fecha inválida"
        val age = LocalDate.now().year - date.year -
                if (LocalDate.now().dayOfYear < date.dayOfYear) 1 else 0
        return if (age < 18) "Debes tener al menos 18 años" else null
    }

    private fun localValidate(
        userName: String,
        birthDate: LocalDate?,
        email: String,
        pass1: String,
        pass2: String
    ): SignUpUiState.FieldError? {
        val pwdError = PasswordValidator.validateStrength(pass1)
        val confirmError = if (pass1.isNotEmpty() && pass1 != pass2)
            "Las contraseñas no coinciden" else null

        val errors = SignUpUiState.FieldError(
            userNameError  = usernameError(userName),
            birthDateError = birthDateError(birthDate),
            emailError     = when {
                email.isBlank()       -> "Requerido"
                !email.isValidEmail() -> "Correo mal formado"
                else                  -> null
            },
            pass1Error = pwdError,
            pass2Error = confirmError
        )

        return if (
            errors.userNameError  != null ||
            errors.birthDateError != null ||
            errors.emailError     != null ||
            errors.pass1Error     != null ||
            errors.pass2Error     != null
        ) errors else null
    }

    fun signUp(
        userName: String,
        birthDate: LocalDate?,
        email: String,
        password: String,
        confirm: String
    ) {
        // 1) Validación local
        localValidate(userName, birthDate, email, password, confirm)?.let { fieldErrors ->
            _ui.value = fieldErrors
            return
        }

        // 2) Registro en Firebase
        viewModelScope.launch {
            try {
                _ui.value = SignUpUiState.Loading
                repo.signUpEmail(
                    email     = email,
                    pass      = password,
                    userName  = userName,
                    birthDate = birthDate!!
                )
                _ui.value = SignUpUiState.Success(email)
            } catch (e: Exception) {
                _ui.value = when (e) {
                    is FirebaseAuthWeakPasswordException ->
                        SignUpUiState.FieldError(pass1Error = PasswordValidator.validateStrength(password)!!)
                    is FirebaseAuthUserCollisionException ->
                        SignUpUiState.FieldError(emailError = "Ese correo ya está registrado")
                    is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {
                        "ERROR_INVALID_EMAIL" ->
                            SignUpUiState.FieldError(emailError = "Formato de correo inválido")
                        else ->
                            SignUpUiState.Error("Credenciales inválidas (${e.errorCode})")
                    }
                    else ->
                        SignUpUiState.Error(e.message ?: "Error desconocido durante el registro")
                }
            }
        }
    }

    fun consumeFieldError() {
        if (_ui.value is SignUpUiState.FieldError) _ui.value = SignUpUiState.Idle
    }

    fun consumeError() {
        if (_ui.value is SignUpUiState.Error) _ui.value = SignUpUiState.Idle
    }

    fun dismissSuccess() {
        if (_ui.value is SignUpUiState.Success || _ui.value is SignUpUiState.GoogleSuccess)
            _ui.value = SignUpUiState.Idle
    }

    fun finishGoogleSignUp(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignUpUiState.Loading
            repo.signInGoogle(idToken)
            _ui.value = SignUpUiState.GoogleSuccess
        } catch (e: Exception) {
            _ui.value = SignUpUiState.Error(e.message ?: "Error durante el inicio con Google")
        }
    }

    fun buildGoogleRequest(serverClientId: String): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()
        return GetCredentialRequest(listOf(option))
    }
}
