/**
 * @file    ForgotPasswordViewModel.kt
 * @ingroup ui_screens_auth // Grupo para ViewModels de pantallas de autenticación
 * @brief   ViewModel para la pantalla de recuperación de contraseña ([ForgotPasswordScreen]).
 *
 * @details Este ViewModel, anotado con `@HiltViewModel` para inyección de dependencias,
 * maneja la lógica de negocio y el estado de la interfaz de usuario para el proceso
 * de "Olvidé mi contraseña". Se encarga de:
 * - Mantener el estado del campo de texto del correo electrónico (`email`) a través de `mutableStateOf`.
 * - Exponer el estado actual de la UI (`_ui` como [MutableStateFlow] y `ui` como [StateFlow])
 * a [ForgotPasswordScreen], utilizando los estados definidos en [ForgotPasswordUiState]
 * (Idle, Loading, Success, Error).
 * - Validar que el campo de correo no esté vacío antes de intentar enviar el enlace.
 * - Invocar al [AuthRepository] para enviar el correo de restablecimiento de contraseña.
 * - Gestionar las excepciones que puedan ocurrir durante la operación y actualizar el estado
 * de la UI correspondientemente.
 * - Proporcionar una función para limpiar el estado de la UI y volver a `Idle` después
 * de que un mensaje de éxito o error haya sido mostrado.
 *
 * @see ForgotPasswordScreen Composable de la pantalla que observa este ViewModel.
 * @see ForgotPasswordUiState Sealed interface que define los estados de la UI.
 * @see com.app.tibibalance.data.repository.AuthRepository Repositorio para interactuar con el servicio de autenticación.
 * @see dagger.hilt.android.lifecycle.HiltViewModel
 * @see androidx.lifecycle.viewModelScope
 */
// ui/screens/auth/ForgotPasswordViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @brief ViewModel para la pantalla de recuperación de contraseña ([ForgotPasswordScreen]).
 * @details Gestiona la lógica para enviar un enlace de restablecimiento de contraseña al correo
 * electrónico proporcionado por el usuario. Expone el estado de la UI ([ForgotPasswordUiState])
 * y el valor actual del campo de correo electrónico.
 *
 * @constructor Inyecta la dependencia [AuthRepository] a través de Hilt.
 * @param repo Instancia del repositorio de autenticación para realizar la operación de restablecimiento.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    /**
     * @brief El correo electrónico introducido por el usuario en el campo de texto.
     * @details Se expone como una propiedad delegada `var` con un setter privado para
     * ser actualizado únicamente a través de [onEmailChange]. Realiza un `trim()`
     * para eliminar espacios en blanco al inicio y al final.
     */
    var email by mutableStateOf("")
        private set // El setter es privado para asegurar que solo onEmailChange lo modifique

    /**
     * @brief Flujo de estado mutable interno que representa el estado actual de la UI.
     * @see ForgotPasswordUiState
     */
    private val _ui = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    /**
     * @brief Flujo de estado inmutable expuesto a la UI ([ForgotPasswordScreen]) para observar
     * los cambios de estado ([ForgotPasswordUiState]).
     */
    val ui: StateFlow<ForgotPasswordUiState> = _ui

    /**
     * @brief Actualiza el valor de la propiedad [email] con el nuevo valor proporcionado.
     * @details Se llama desde la UI (generalmente en el `onValueChange` de un `TextField`)
     * cada vez que el usuario modifica el campo de correo electrónico.
     * Aplica `trim()` al `newValue` para limpiar espacios.
     *
     * @param newValue El nuevo [String] introducido por el usuario en el campo de correo.
     */
    fun onEmailChange(newValue: String) {
        email = newValue.trim() // Actualiza el estado del email y elimina espacios
    }

    /**
     * @brief Inicia el proceso para enviar un correo de restablecimiento de contraseña.
     * @details
     * 1. Valida si el campo [email] está en blanco. Si es así, emite [ForgotPasswordUiState.Error].
     * 2. Si el correo no está en blanco, emite [ForgotPasswordUiState.Loading].
     * 3. Llama a `repo.resetPass(email)` para solicitar el envío del correo.
     * 4. Si la operación es exitosa, emite [ForgotPasswordUiState.Success].
     * 5. Si ocurre una excepción, emite [ForgotPasswordUiState.Error] con el mensaje de la excepción.
     * La operación se lanza en el [viewModelScope].
     */
    fun sendResetLink() {
        // Validación básica: el correo no puede estar vacío.
        if (email.isBlank()) {
            _ui.value = ForgotPasswordUiState.Error("Escribe tu correo")
            return
        }

        // Lanza una corrutina en el scope del ViewModel para la operación asíncrona.
        viewModelScope.launch {
            try {
                // Cambia el estado de la UI a Loading antes de la llamada de red.
                _ui.value = ForgotPasswordUiState.Loading
                // Llama al repositorio para enviar el correo de restablecimiento.
                repo.resetPass(email)
                // Si la llamada es exitosa, cambia el estado de la UI a Success.
                _ui.value = ForgotPasswordUiState.Success
            } catch (e: Exception) {
                // Si ocurre cualquier excepción, cambia el estado de la UI a Error.
                _ui.value = ForgotPasswordUiState.Error(
                    e.message ?: "No se pudo enviar el correo" // Mensaje de error genérico si e.message es null
                )
            }
        }
    }

    /**
     * @brief Restablece el estado de la UI a [ForgotPasswordUiState.Idle].
     * @details Se llama desde la UI después de que un mensaje de [ForgotPasswordUiState.Success]
     * o [ForgotPasswordUiState.Error] ha sido mostrado y procesado por el usuario (e.g.,
     * al cerrar un diálogo o un Snackbar). Esto prepara al ViewModel para una nueva interacción.
     */
    fun clearStatus() {
        _ui.value = ForgotPasswordUiState.Idle
    }
}