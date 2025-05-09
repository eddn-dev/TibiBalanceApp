/**
 * @file    VerifyEmailViewModel.kt
 * @ingroup ui_screens_auth // Grupo para ViewModels de pantallas de autenticación
 * @brief   ViewModel para la pantalla de verificación de correo electrónico ([VerifyEmailScreen]).
 *
 * @details
 * Este ViewModel, gestionado por Hilt (`@HiltViewModel`), encapsula la lógica de negocio
 * y el estado de la UI para la pantalla que se muestra después de que un usuario
 * se registra o inicia sesión pero su correo electrónico aún no ha sido verificado.
 *
 * <h4>Responsabilidades Clave:</h4>
 * <ul>
 * <li>Exponer el estado actual de la UI ([VerifyEmailUiState]) mediante un [StateFlow].</li>
 * <li>Manejar la acción de **reenviar** el correo de verificación llamando a [FirebaseAuth].</li>
 * <li>Manejar la acción de **comprobar** si el correo ya ha sido verificado:
 * <ul>
 * <li>Llama a `reload()` en el [FirebaseUser] actual para obtener el estado más reciente.</li>
 * <li>Comprueba la propiedad `isEmailVerified`.</li>
 * <li>Si está verificado, llama a `repo.syncVerification()` para actualizar el estado en el backend (Firestore).</li>
 * </ul>
 * </li>
 * <li>Manejar la acción de **cerrar sesión**, delegando a [AuthRepository].</li>
 * <li>Actualizar el [StateFlow] `ui` para reflejar el estado de las operaciones (Idle, Loading, Success, Error, SignedOut).</li>
 * <li>Proporcionar un método (`clear`) para resetear el estado de la UI después de mostrar un mensaje.</li>
 * </ul>
 *
 * Las operaciones que interactúan con Firebase se ejecutan dentro del `viewModelScope`.
 *
 * @property ui Flujo de estado inmutable ([StateFlow]) que la [VerifyEmailScreen] observa
 * para reaccionar a los cambios de estado ([VerifyEmailUiState]).
 * @see VerifyEmailScreen Composable de la pantalla que utiliza este ViewModel.
 * @see VerifyEmailUiState Define los diferentes estados de la UI.
 * @see AuthRepository Repositorio utilizado para cerrar sesión y sincronizar la verificación.
 * @see FirebaseAuth SDK de Firebase utilizado directamente para `sendEmailVerification` y `reload`.
 * @see dagger.hilt.android.lifecycle.HiltViewModel
 * @see androidx.lifecycle.viewModelScope
 */
// ui/screens/auth/VerifyEmailViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @brief ViewModel para la pantalla [VerifyEmailScreen].
 * @details Gestiona la lógica para reenviar correos de verificación, comprobar el estado
 * de verificación y cerrar sesión.
 *
 * @constructor Inyecta las dependencias [AuthRepository] y [FirebaseAuth] mediante Hilt.
 * @param repo Repositorio para operaciones de autenticación como `signOut` y `syncVerification`.
 * @param auth Instancia de FirebaseAuth para operaciones directas como `sendEmailVerification` y `reload`.
 */
@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    /**
     * @brief Flujo de estado mutable interno que representa el estado actual de la UI.
     * @see VerifyEmailUiState
     */
    private val _ui = MutableStateFlow<VerifyEmailUiState>(VerifyEmailUiState.Idle)
    /**
     * @brief Flujo de estado inmutable expuesto a la UI ([VerifyEmailScreen]) para observar
     * los cambios de estado ([VerifyEmailUiState]).
     */
    val ui: StateFlow<VerifyEmailUiState> = _ui

    /**
     * @brief Solicita a Firebase que reenvíe el correo de verificación al usuario actual.
     * @details Cambia el estado de la UI a [VerifyEmailUiState.Loading], llama a
     * `sendEmailVerification()` en el usuario actual de FirebaseAuth, y actualiza
     * la UI a [VerifyEmailUiState.Success] o [VerifyEmailUiState.Error] según el resultado.
     * La operación se ejecuta en el `viewModelScope`.
     */
    fun resend() = viewModelScope.launch {
        _ui.value = VerifyEmailUiState.Loading // Inicia estado de carga
        runCatching { // Intenta la operación de Firebase
            auth.currentUser?.sendEmailVerification()?.await() // Llama y espera el resultado
        }.onSuccess { // Si la llamada a Firebase tiene éxito (no lanza excepción)
            _ui.value = VerifyEmailUiState.Success("Correo reenviado") // Emite estado de éxito
        }.onFailure { // Si ocurre una excepción durante la llamada a Firebase
            _ui.value = VerifyEmailUiState.Error(it.localizedMessage ?: "Error al reenviar") // Emite estado de error
        }
    }

    /**
     * @brief Comprueba si el correo del usuario actual ha sido verificado.
     * @details
     * 1. Cambia el estado a [VerifyEmailUiState.Loading].
     * 2. Llama a `reload()` en el usuario actual para obtener el estado más reciente de Firebase.
     * 3. Comprueba la propiedad `isEmailVerified`.
     * 4. Si está verificado (`true`):
     * - Llama a `repo.syncVerification()` para actualizar el estado en el backend (Firestore).
     * - Emite [VerifyEmailUiState.Success] con `goHome = true`.
     * 5. Si no está verificado (`false`):
     * - Emite [VerifyEmailUiState.Error] indicándolo.
     * 6. Si ocurre una excepción durante `reload()` o `syncVerification()`, emite [VerifyEmailUiState.Error].
     * La operación se ejecuta en el `viewModelScope`.
     */
    fun verify() = viewModelScope.launch {
        _ui.value = VerifyEmailUiState.Loading // Inicia estado de carga
        runCatching { // Intenta las operaciones de Firebase y repositorio
            auth.currentUser?.reload()?.await() // Recarga el estado del usuario
            // Comprueba si está verificado después de recargar
            if (auth.currentUser?.isEmailVerified == true) {
                repo.syncVerification() // Sincroniza el estado verificado con el backend/DB
                true // Devuelve true si está verificado
            } else {
                false // Devuelve false si no está verificado
            }
        }.onSuccess { isVerified -> // Si el bloque runCatching tiene éxito
            if (isVerified) // Si el resultado fue true (verificado)
                _ui.value = VerifyEmailUiState.Success("¡Verificado!", goHome = true) // Emite éxito y señal para ir a Home
            else // Si el resultado fue false (no verificado)
                _ui.value = VerifyEmailUiState.Error("Aún no está verificado") // Emite error informativo
        }.onFailure { // Si ocurre una excepción en runCatching
            _ui.value = VerifyEmailUiState.Error(it.localizedMessage ?: "Error de red") // Emite estado de error
        }
    }

    /**
     * @brief Cierra la sesión del usuario actual.
     * @details Llama a `repo.signOut()` y luego emite el estado [VerifyEmailUiState.SignedOut]
     * para que la UI pueda navegar de vuelta a la pantalla de inicio. Se recomienda
     * ofrecer esta opción en la pantalla de verificación.
     * La operación se ejecuta en el `viewModelScope`.
     */
    fun signOut() = viewModelScope.launch {          // Recomendado por Google para UX
        repo.signOut() // Llama al repositorio para cerrar sesión
        _ui.value = VerifyEmailUiState.SignedOut // Emite el evento de cierre de sesión
    }

    /**
     * @brief Restablece el estado de la UI a [VerifyEmailUiState.Idle].
     * @details Se utiliza para limpiar mensajes de éxito o error una vez que han sido
     * procesados por la UI (e.g., al cerrar un diálogo o Snackbar). No restablece
     * el estado si actualmente está en [VerifyEmailUiState.Loading] para evitar
     * interrupciones visuales.
     */
    fun clear() {
        // Solo cambia a Idle si NO está en estado de Loading
        if (_ui.value !is VerifyEmailUiState.Loading) {
            _ui.value = VerifyEmailUiState.Idle
        }
    }
}