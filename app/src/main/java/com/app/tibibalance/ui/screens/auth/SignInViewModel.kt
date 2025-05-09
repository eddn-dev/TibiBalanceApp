/**
 * @file    SignInViewModel.kt
 * @ingroup ui_screens_auth // Grupo para ViewModels de pantallas de autenticación
 * @brief   ViewModel para la pantalla de inicio de sesión ([SignInScreen]).
 *
 * @details Este ViewModel, gestionado por Hilt (`@HiltViewModel`), encapsula la lógica
 * de negocio y el estado de la UI para el proceso de inicio de sesión. Sus responsabilidades
 * incluyen:
 * - Validar localmente las entradas de correo electrónico y contraseña.
 * - Exponer el estado actual de la UI ([SignInUiState]) a través de un [StateFlow].
 * - Interactuar con [AuthRepository] para realizar el inicio de sesión con correo/contraseña
 * y con Google (One-Tap).
 * - Manejar diferentes tipos de excepciones de Firebase (`FirebaseAuthInvalidUserException`,
 * `FirebaseAuthInvalidCredentialsException`, `FirebaseNetworkException`, etc.) y
 * mapearlas a estados de UI apropiados ([SignInUiState.FieldError], [SignInUiState.Error]).
 * - Sincronizar el estado de verificación del correo (`repo.syncVerification()`) después
 * de un inicio de sesión exitoso con correo/contraseña.
 * - Proporcionar una función para construir la solicitud de credenciales de Google ([buildGoogleRequest]).
 * - Ofrecer un método para limpiar los estados de error ([consumeError]).
 *
 * @see SignInScreen Composable de la pantalla que observa este ViewModel.
 * @see SignInUiState Sealed interface que define los estados de la UI.
 * @see com.app.tibibalance.data.repository.AuthRepository Repositorio para interactuar con la capa de autenticación.
 * @see androidx.credentials.GetCredentialRequest Utilizado para la solicitud de Google One-Tap.
 * @see com.google.android.libraries.identity.googleid.GetGoogleIdOption Opción específica para Google One-Tap.
 * @see dagger.hilt.android.lifecycle.HiltViewModel
 * @see androidx.lifecycle.viewModelScope
 */
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

/**
 * @brief ViewModel para la pantalla de inicio de sesión ([SignInScreen]).
 * @details Maneja la lógica de autenticación para el inicio de sesión con correo/contraseña
 * y Google One-Tap, exponiendo el estado de la UI a través del [StateFlow] `ui`.
 *
 * @constructor Inyecta la dependencia [AuthRepository] mediante Hilt.
 * @param repo Instancia del repositorio de autenticación.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    /**
     * @brief Flujo de estado mutable interno que representa el estado actual de la UI.
     * @see SignInUiState
     */
    private val _ui = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    /**
     * @brief Flujo de estado inmutable expuesto a la UI ([SignInScreen]) para observar
     * los cambios de estado ([SignInUiState]).
     */
    val ui: StateFlow<SignInUiState> = _ui

    /**
     * @brief Intenta iniciar sesión utilizando correo electrónico y contraseña.
     *
     * @details
     * 1. Realiza validaciones locales básicas en `email` y `pass`. Si fallan, emite [SignInUiState.FieldError].
     * 2. Si la validación local pasa, emite [SignInUiState.Loading].
     * 3. Llama a `repo.signIn()` para autenticar al usuario.
     * 4. Si `signIn` es exitoso, llama a `repo.syncVerification()` para obtener el estado de verificación.
     * 5. Emite [SignInUiState.Success] con el estado de verificación.
     * 6. Si ocurre una excepción durante el proceso, la captura y la mapea a un estado
     * [SignInUiState.Error] o [SignInUiState.FieldError] apropiado.
     *
     * @param email El correo electrónico introducido por el usuario.
     * @param pass La contraseña introducida por el usuario.
     */
    fun signIn(email: String, pass: String) = viewModelScope.launch {
        /* ---- validación local ---- */
        // Comprueba si el email está vacío, no contiene '@', o la contraseña es muy corta.
        if (email.isBlank() || !email.contains("@") || pass.length < 6) {
            _ui.value = SignInUiState.FieldError(
                emailError = if (email.isBlank() || !email.contains("@")) "Correo inválido" else null,
                passError  = if (pass.length < 6) "≥ 6 caracteres" else null
            )
            return@launch // Termina la ejecución si hay errores locales.
        }

        try {
            _ui.value = SignInUiState.Loading // Indica que la operación ha comenzado.
            repo.signIn(email.trim(), pass)   // Llama al repositorio (puede lanzar excepciones).
            // Después del inicio de sesión, sincroniza/verifica el estado del correo.
            val verified = repo.syncVerification()
            _ui.value = SignInUiState.Success(verified) // Emite estado de éxito con el estado de verificación.
        } catch (e: Exception) {
            // Manejo de excepciones específicas de Firebase Auth y otras generales.
            _ui.value = when (e) {
                // Error: El usuario no existe o está deshabilitado.
                is FirebaseAuthInvalidUserException ->
                    SignInUiState.FieldError(
                        emailError = "La cuenta no existe o está deshabilitada"
                    )
                // Error: Credenciales inválidas (formato de email o contraseña incorrecta).
                is FirebaseAuthInvalidCredentialsException -> when (e.errorCode) {
                    "ERROR_INVALID_EMAIL"  -> SignInUiState.FieldError(emailError = "Correo mal formado")
                    // Firebase a menudo devuelve un error genérico para contraseña incorrecta.
                    "ERROR_INVALID_LOGIN_CREDENTIALS",
                    "ERROR_INVALID_CREDENTIAL" -> SignInUiState.FieldError(
                        emailError = "Revisa correo o contraseña", // Mensaje genérico
                        passError  = "" // Marca ambos campos (o solo contraseña si se prefiere)
                    )
                    // Otro error de credenciales no esperado.
                    else -> SignInUiState.Error("Credenciales inválidas (${e.errorCode})")
                }
                // Error de red.
                is FirebaseNetworkException ->
                    SignInUiState.Error("Sin conexión. Intenta de nuevo")
                // Otros errores no específicos.
                else ->
                    SignInUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /* ───── Google One-Tap ───── */

    /**
     * @brief Finaliza el proceso de inicio de sesión utilizando un ID Token de Google.
     *
     * @details Llama a `repo.signInGoogle()` para autenticar al usuario en Firebase
     * usando el token proporcionado por Google One-Tap. Si tiene éxito, emite
     * [SignInUiState.Success] asumiendo que las cuentas de Google siempre están
     * verificadas (`verified = true`). Captura excepciones y emite [SignInUiState.Error]
     * si la autenticación con Google falla.
     *
     * @param idToken El ID Token JWT obtenido de Google Identity Services.
     */
    fun finishGoogleSignIn(idToken: String) = viewModelScope.launch {
        try {
            _ui.value = SignInUiState.Loading // Indica inicio de la operación.
            repo.signInGoogle(idToken)        // Llama al repositorio.
            // Asume éxito y verificación para inicios de sesión con Google.
            _ui.value = SignInUiState.Success(verified = true)
        } catch (e: Exception) {
            // Maneja errores durante el inicio de sesión con Google.
            _ui.value = SignInUiState.Error(e.message ?: "Google error")
        }
    }

    /* ───── Helpers ───── */

    /**
     * @brief Restablece el estado de la UI a [SignInUiState.Idle] si el estado actual es de error.
     * @details Permite a la UI limpiar los mensajes de error (globales o de campo)
     * una vez que han sido mostrados o el usuario realiza una nueva acción.
     */
    fun consumeError() {
        if (ui.value is SignInUiState.Error || ui.value is SignInUiState.FieldError) {
            _ui.value = SignInUiState.Idle
        }
    }

    /**
     * @brief Construye el objeto [GetCredentialRequest] necesario para iniciar Google One-Tap.
     *
     * @details Configura [GetGoogleIdOption] con el ID de cliente web de OAuth 2.0
     * (obtenido de Google Cloud Console) y deshabilita el filtrado por cuentas autorizadas
     * para mostrar siempre la hoja de selección de cuentas de Google.
     *
     * @param serverClientId El ID de cliente web de OAuth 2.0 asociado a tu proyecto de Firebase/Google Cloud.
     * @return Un objeto [GetCredentialRequest] listo para ser pasado a `CredentialManager.getCredential()`.
     */
    fun buildGoogleRequest(serverClientId: String): GetCredentialRequest =
        GetCredentialRequest(
            listOf(
                GetGoogleIdOption.Builder()
                    .setServerClientId(serverClientId) // ID de cliente web para obtener ID Token.
                    .setFilterByAuthorizedAccounts(false) // Muestra siempre el selector de cuentas.
                    .build()
            )
        )
}