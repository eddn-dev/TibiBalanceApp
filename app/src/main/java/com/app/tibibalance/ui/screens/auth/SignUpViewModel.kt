/**
 * @file    SignUpViewModel.kt
 * @ingroup ui_screens_auth
 * @brief   ViewModel para la pantalla de registro de usuarios ([SignUpScreen]).
 *
 * @details Gestiona la lógica de registro, incluyendo validación de campos,
 * interacción con [AuthRepository] para crear la cuenta (con correo/contraseña
 * o Google One-Tap), y expone el estado de la UI ([SignUpUiState]).
 * **Mejora UX:** La validación de contraseña ahora devuelve un mensaje con todos
 * los requisitos incumplidos.
 *
 * @see SignUpScreen Composable de la pantalla que observa este ViewModel.
 * @see SignUpUiState Define los diferentes estados de la UI.
 * @see AuthRepository Repositorio para interactuar con la capa de autenticación.
 */
package com.app.tibibalance.ui.screens.auth

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

    /**
     * @brief Reporta un error específico de Google One-Tap a la UI.
     * @param msg Mensaje de error a mostrar.
     */
    fun reportGoogleError(msg: String) {
        _ui.value = SignUpUiState.Error(msg)
    }

    private val _ui = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val ui: StateFlow<SignUpUiState> = _ui

    /** Expresión regular para validar formato de email. */
    private val EMAIL_RX =
        Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\$", RegexOption.IGNORE_CASE)

    /**
     * @brief Verifica si una cadena tiene formato de email válido.
     * @receiver La cadena a validar.
     * @return `true` si el formato es válido, `false` en caso contrario.
     */
    private fun String.isValidEmail() = EMAIL_RX.matches(this)

    /**
     * @brief Valida la fortaleza de una contraseña y devuelve un mensaje con TODOS los requisitos incumplidos.
     *
     * @details Verifica longitud mínima (8), presencia de mayúsculas, minúsculas, números y símbolos.
     * Si la contraseña es válida, devuelve `null`. Si no, devuelve un String
     * listando todos los requisitos faltantes, separados por saltos de línea.
     *
     * @receiver La contraseña a validar.
     * @return Un [String] con los errores concatenados, o `null` si la contraseña es válida.
     */
    private fun String.passwordStrengthError(): String? {
        val errors = mutableListOf<String>() // Lista para acumular errores
        val symbols = "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~" // Símbolos permitidos/requeridos

        if (length < 8)            errors.add("• Mínimo 8 caracteres")
        if (!any(Char::isUpperCase)) errors.add("• Incluir mayúsculas (A-Z)")
        if (!any(Char::isLowerCase)) errors.add("• Incluir minúsculas (a-z)")
        if (!any(Char::isDigit))     errors.add("• Incluir números (0-9)")
        if (!any { symbols.contains(it) }) errors.add("• Incluir símbolos (${symbols.take(5)}...)")

        // Si la lista de errores no está vacía, los une en un solo mensaje.
        return if (errors.isNotEmpty()) {
            "La contraseña debe:\n" + errors.joinToString("\n")
        } else {
            null // La contraseña cumple todos los requisitos
        }
    }

    /**
     * @brief Valida el nombre de usuario.
     * @param u El nombre de usuario a validar.
     * @return Mensaje de error [String?] o `null` si es válido.
     */
    private fun usernameError(u: String): String? =
        when {
            u.isBlank()                -> "Requerido" // Añadido para campo vacío
            u.length < 4               -> "≥ 4 caracteres"
            !u.matches(Regex("^[a-zA-Z0-9_]+\$")) -> "Solo letras, números o _"
            else                       -> null
        }

    /**
     * @brief Valida la fecha de nacimiento.
     * @param date La [LocalDate?] a validar.
     * @return Mensaje de error [String?] o `null` si es válida (mayor de 18).
     */
    private fun birthDateError(date: LocalDate?): String? {
        date ?: return "Selecciona fecha" // Si es null, pide seleccionar
        if (date.isAfter(LocalDate.now())) return "Fecha inválida" // Fecha futura no válida
        // Cálculo de edad
        val age = LocalDate.now().year - date.year -
                if (LocalDate.now().dayOfYear < date.dayOfYear) 1 else 0
        // Validación de mayoría de edad
        return if (age < 18) "Debes tener al menos 18 años" else null
    }


    /**
     * @brief Realiza la validación local de todos los campos del formulario de registro.
     *
     * @param userName Nombre de usuario introducido.
     * @param birthDate Fecha de nacimiento seleccionada.
     * @param email Correo electrónico introducido.
     * @param pass1 Contraseña introducida.
     * @param pass2 Confirmación de contraseña introducida.
     * @return Un objeto [SignUpUiState.FieldError] con los mensajes de error correspondientes
     * si algún campo es inválido, o `null` si todos los campos son válidos localmente.
     */
    private fun localValidate(
        userName : String,
        birthDate: LocalDate?,
        email    : String,
        pass1    : String,
        pass2    : String
    ): SignUpUiState.FieldError? {

        // Llama a las funciones de validación individuales para cada campo.
        val errors = SignUpUiState.FieldError(
            userNameError  = usernameError(userName),
            birthDateError = birthDateError(birthDate),
            emailError     = when { // Validación de email
                email.isBlank()        -> "Requerido"
                !email.isValidEmail()  -> "Correo mal formado"
                else                   -> null
            },
            pass1Error = pass1.passwordStrengthError(), // Validación de fortaleza
            pass2Error = if (pass1.isNotEmpty() && pass1 != pass2) "Las contraseñas no coinciden" else null // Validación de coincidencia (solo si pass1 no está vacía)
        )

        // Devuelve el objeto de errores solo si al menos uno de los campos tiene error.
        return if (
            errors.userNameError  != null ||
            errors.birthDateError != null ||
            errors.emailError     != null ||
            errors.pass1Error     != null ||
            errors.pass2Error     != null
        ) errors else null
    }



    /**
     * @brief Inicia el proceso de registro con correo electrónico y contraseña.
     *
     * @details Primero valida los campos localmente usando [localValidate]. Si hay errores,
     * emite el estado [SignUpUiState.FieldError]. Si la validación local es exitosa,
     * emite [SignUpUiState.Loading] y llama al método `signUpEmail` del repositorio.
     * Maneja las excepciones específicas de Firebase Auth para proporcionar feedback
     * detallado al usuario (contraseña débil, correo ya en uso, etc.). Si el registro
     * en el repositorio es exitoso, emite [SignUpUiState.Success].
     *
     * @param userName Nombre de usuario.
     * @param birthDate Fecha de nacimiento.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param confirm Confirmación de la contraseña.
     */
    fun signUp(userName: String, birthDate: LocalDate?,
               email: String, password: String, confirm: String) {

        // 1. Validación local primero.
        localValidate(userName, birthDate, email, password, confirm)?.let { fieldErrors ->
            _ui.value = fieldErrors      // Emite el estado con los errores de campo.
            return // Detiene la ejecución si hay errores locales.
        }

        // 2. Si la validación local pasa, inicia el proceso asíncrono.
        viewModelScope.launch {
            try {
                _ui.value = SignUpUiState.Loading // Indica que la operación ha comenzado.
                // Llama al repositorio para registrar y crear perfil (puede lanzar excepciones).
                repo.signUpEmail(
                    email      = email,
                    pass       = password,
                    userName   = userName,
                    birthDate  = birthDate!! // Se asume no nulo por validación previa.
                )

                _ui.value = SignUpUiState.Success(email) // Emite estado de éxito.

            } catch (e: Exception) { // Captura excepciones del repositorio o Firebase.
                // Mapea excepciones específicas a estados de UI adecuados.
                _ui.value = when (e) {
                    // Contraseña débil según Firebase (podría mostrar el mismo mensaje detallado local, pero Firebase es la autoridad final).
                    is FirebaseAuthWeakPasswordException ->
                        SignUpUiState.FieldError(pass1Error = "Contraseña débil según el servidor") // Mensaje específico de Firebase.

                    // El correo ya está registrado.
                    is FirebaseAuthUserCollisionException ->
                        SignUpUiState.FieldError(emailError = "Ese correo ya está registrado")

                    // Credenciales inválidas (formato de email incorrecto).
                    is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {
                        "ERROR_INVALID_EMAIL" ->
                            SignUpUiState.FieldError(emailError = "Formato de correo inválido")
                        else -> // Otros errores de credenciales.
                            SignUpUiState.Error("Credenciales inválidas (${e.errorCode})")
                    }

                    // Error general o desconocido.
                    else -> SignUpUiState.Error(e.message ?: "Error desconocido durante el registro")
                }
            }
        }
    }

    /**
     * @brief Limpia el estado de error de campo ([SignUpUiState.FieldError]) volviendo a [SignUpUiState.Idle].
     * @details Se llama desde la UI (e.g., en `onValueChange`) para que los mensajes de error
     * desaparezcan tan pronto como el usuario empieza a corregir el campo.
     */
    fun consumeFieldError() {
        if (_ui.value is SignUpUiState.FieldError) _ui.value = SignUpUiState.Idle
    }


    /**
     * @brief Finaliza el proceso de registro/inicio de sesión utilizando un ID Token de Google.
     * @details Llama al método `signInGoogle` del repositorio. Si tiene éxito, emite
     * [SignUpUiState.GoogleSuccess]. Si falla, emite [SignUpUiState.Error].
     * @param idToken El ID Token JWT obtenido de Google Identity Services.
     */
    fun finishGoogleSignUp(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignUpUiState.Loading
            repo.signInGoogle(idToken)              // Llama al repositorio.
            _ui.value = SignUpUiState.GoogleSuccess // Éxito -> Navegar a Home.
        } catch (e: Exception) {
            _ui.value = SignUpUiState.Error(e.message ?: "Error durante el inicio con Google")
        }
    }

    /* --- Helpers para limpiar estados --- */
    /** Limpia el estado de Error global volviendo a Idle. */
    fun consumeError()  { if (_ui.value is SignUpUiState.Error)   _ui.value = SignUpUiState.Idle }
    /** Limpia el estado de Success (formulario) volviendo a Idle. */
    fun dismissSuccess(){ if (_ui.value is SignUpUiState.Success || _ui.value is SignUpUiState.GoogleSuccess) _ui.value = SignUpUiState.Idle } // Actualizado para GoogleSuccess

    /**
     * @brief Construye el objeto [GetCredentialRequest] necesario para iniciar Google One-Tap.
     * @details Configura [GetGoogleIdOption] con el ID de cliente web y deshabilita el
     * filtrado por cuentas autorizadas para mostrar siempre la hoja de selección de cuentas.
     * @param serverClientId El ID de cliente web de OAuth 2.0 asociado a tu proyecto de Firebase/Google Cloud.
     * @return Un objeto [GetCredentialRequest] listo para ser pasado a `CredentialManager.getCredential()`.
     */
    fun buildGoogleRequest(serverClientId: String): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)          // Muestra siempre la hoja de selección.
            .setServerClientId(serverClientId)             // Necesario para obtener el ID Token para Firebase.
            .build()                                       // Construye la opción.

        // Crea la solicitud de credenciales con la opción de Google.
        return GetCredentialRequest(listOf(option))        // Usa el constructor de Jetpack Credentials.
    }
}