/**
 * @file    VerifyEmailUiState.kt
 * @ingroup ui_screens_auth // Grupo para estados de UI de pantallas de autenticación
 * @brief   Define los posibles estados de la interfaz de usuario para la pantalla de verificación de correo electrónico ([VerifyEmailScreen]).
 *
 * @details
 * Esta interfaz sellada (`sealed interface`) encapsula los diferentes estados por los que
 * puede pasar la pantalla de verificación de correo. Estos estados son emitidos por el
 * [VerifyEmailViewModel] para comunicar a la [VerifyEmailScreen] el resultado de las
 * acciones del usuario (como reenviar correo o comprobar verificación) o cambios
 * en el estado de la sesión.
 *
 * La UI reacciona a estos estados mostrando información relevante:
 * - [Idle]: Estado de reposo, la pantalla espera la interacción del usuario.
 * - [Loading]: Se está procesando una acción (e.g., reenviando correo, comprobando
 * el estado de verificación). La UI típicamente muestra un indicador de carga
 * y deshabilita interacciones.
 * - [Success]: Una operación fue exitosa (e.g., correo reenviado, verificación confirmada).
 * Contiene un mensaje para el usuario y un flag opcional `goHome` que indica si se debe
 * navegar a la pantalla principal.
 * - [Error]: Ocurrió un error durante una operación (e.g., fallo de red, el correo aún
 * no está verificado). Contiene un mensaje de error.
 * - [SignedOut]: Indica que el usuario ha cerrado sesión desde esta pantalla. Este estado
 * desencadena la navegación de regreso a la pantalla de inicio de la app ([Screen.Launch]).
 *
 * @see VerifyEmailScreen Composable de la pantalla que observa estos estados.
 * @see VerifyEmailViewModel ViewModel que emite y gestiona estos estados.
 * @see com.app.tibibalance.ui.components.dialogs.ModalInfoDialog Componente usado para mostrar feedback en los estados Loading, Success y Error.
 */
package com.app.tibibalance.ui.screens.auth

/**
 * @brief Interfaz sellada que representa los distintos estados posibles de la UI
 * durante el proceso de verificación de correo en [VerifyEmailScreen].
 */
sealed interface VerifyEmailUiState {
    /**
     * @brief Estado inicial o inactivo de la pantalla.
     * @details La UI está lista para que el usuario interactúe con los botones de
     * "Reenviar correo", "Ya lo verifiqué" o "Cerrar sesión".
     */
    data object Idle    : VerifyEmailUiState

    /**
     * @brief Estado de carga durante una operación asíncrona.
     * @details Indica que la aplicación está procesando una solicitud, como
     * reenviar el correo de verificación o comprobar el estado de verificación
     * con el backend. La UI debe mostrar un indicador de progreso.
     */
    data object Loading : VerifyEmailUiState

    /**
     * @brief Estado que indica que una operación se completó exitosamente.
     * @details Contiene un mensaje para informar al usuario y un flag opcional
     * para indicar si se debe navegar a la pantalla principal.
     *
     * @param message El [String] con el mensaje de éxito a mostrar al usuario
     * (e.g., "Correo reenviado", "¡Verificado!").
     * @param goHome Un [Boolean] que es `true` si, tras este éxito, la navegación
     * debe dirigirse a la pantalla principal ([Screen.Main]). Por defecto es `false`.
     */
    data class Success(val message: String, val goHome: Boolean = false)
        : VerifyEmailUiState

    /**
     * @brief Estado que indica que ocurrió un error durante una operación.
     * @details Contiene un mensaje descriptivo del error para mostrar al usuario.
     *
     * @param message El [String] con el mensaje de error detallado
     * (e.g., "Aún no está verificado", "Error de red").
     */
    data class Error(val message: String) : VerifyEmailUiState

    /**
     * @brief Estado que se emite cuando el usuario ha cerrado sesión desde esta pantalla.
     * @details Este estado es una señal para que la [VerifyEmailScreen] navegue
     * de regreso a la pantalla de inicio de la aplicación ([Screen.Launch]).
     */
    data object SignedOut : VerifyEmailUiState
}