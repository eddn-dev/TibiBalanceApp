package com.app.tibibalance.ui.screens.auth

import SignUpUiState
import androidx.credentials.GetCredentialRequest             // ✅ Jetpack (no min-api 34)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption // implements CredentialOption
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

    private val EMAIL_RX =
        Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\$", RegexOption.IGNORE_CASE)

    private fun String.isValidEmail() = EMAIL_RX.matches(this)

    private fun String.passwordStrengthError(): String? {
        if (length < 8)            return "≥ 8 caracteres"
        if (!any(Char::isUpperCase)) return "Debe incluir mayúsculas"
        if (!any(Char::isLowerCase)) return "Debe incluir minúsculas"
        if (!any(Char::isDigit))     return "Debe incluir números"
        if (!any { "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~".contains(it) })
            return "Debe incluir símbolos"
        return null                // fuerte ✔
    }

    private fun usernameError(u: String): String? =
        when {
            u.length < 4               -> "≥ 4 caracteres"
            !u.matches(Regex("^[a-zA-Z0-9_]+\$")) -> "Solo letras, números o _"
            else                       -> null
        }

    private fun birthDateError(date: LocalDate?): String? {
        date ?: return "Selecciona fecha"
        if (date.isAfter(LocalDate.now())) return "Fecha inválida"
        val age = LocalDate.now().year - date.year -
                if (LocalDate.now().dayOfYear < date.dayOfYear) 1 else 0
        return if (age < 18) "Debes tener al menos 18 años" else null
    }


    private fun localValidate(
        userName : String,
        birthDate: LocalDate?,
        email    : String,
        pass1    : String,
        pass2    : String
    ): SignUpUiState.FieldError? {

        val errors = SignUpUiState.FieldError(
            userNameError  = usernameError(userName),
            birthDateError = birthDateError(birthDate),
            emailError     = when {
                email.isBlank()        -> "Requerido"
                !email.isValidEmail()  -> "Correo mal formado"
                else                   -> null
            },
            pass1Error = pass1.passwordStrengthError(),
            pass2Error = if (pass1 != pass2) "No coincide" else null
        )

        return if (
            errors.userNameError  != null ||
            errors.birthDateError != null ||
            errors.emailError     != null ||
            errors.pass1Error     != null ||
            errors.pass2Error     != null
        ) errors else null
    }



    /* -------- Registro con correo/contraseña -------- */
    fun signUp(userName: String, birthDate: LocalDate?,
               email: String, password: String, confirm: String) {

        localValidate(userName, birthDate, email, password, confirm)?.let {
            _ui.value = it                // ← emite FieldError
            return
        }



        viewModelScope.launch {
            try {
                _ui.value = SignUpUiState.Loading
                repo.signUpEmail(
                    email      = email,
                    pass       = password,
                    userName   = userName,
                    birthDate  = birthDate!!
                )

                _ui.value = SignUpUiState.Success(email)

            } catch (e: Exception) {
                _ui.value = when (e) {

                    is FirebaseAuthWeakPasswordException ->
                        SignUpUiState.FieldError(pass1Error = "Contraseña débil")

                    is FirebaseAuthUserCollisionException ->
                        SignUpUiState.FieldError(emailError =
                            "Ese correo ya está registrado")

                    is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {
                        "ERROR_INVALID_EMAIL" ->
                            SignUpUiState.FieldError(emailError = "Correo mal formado")
                        else ->
                            SignUpUiState.Error("Credenciales inválidas")
                    }

                    else -> SignUpUiState.Error(e.message ?: "Error desconocido")
                }
            }
        }
    }

    /* limpiar errores una vez mostrados */
    fun consumeFieldError() {
        if (_ui.value is SignUpUiState.FieldError) _ui.value = SignUpUiState.Idle
    }


    /* -------- Google bottom-sheet (idToken llega desde la UI) -------- */
    fun finishGoogleSignUp(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignUpUiState.Loading
            repo.signInGoogle(idToken)              // la cuenta ya está verificada
            _ui.value = SignUpUiState.GoogleSuccess // ⬅️ dispara navegación a Home
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
}
