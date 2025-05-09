/**
 * @file    ForgotPasswordUiState.kt
 * @ingroup ui_screens_auth // Grupo para estados de UI de pantallas de autenticación
 * @brief   Define los posibles estados de la interfaz de usuario para la pantalla de recuperación de contraseña ([ForgotPasswordScreen]).
 *
 * @details Esta interfaz sellada (`sealed interface`) encapsula todos los estados visuales
 * y de interacción por los que puede pasar la pantalla de "Olvidé mi contraseña".
 * El [ForgotPasswordViewModel] emite instancias de estos estados para comunicar
 * el progreso y el resultado de la operación de envío del enlace de restablecimiento
 * a la [ForgotPasswordScreen], la cual reacciona actualizando la UI correspondientemente
 * (e.g., mostrando un diálogo de carga, un mensaje de éxito o un error).
 *
 * Los estados definidos son:
 * - [Idle]: Estado inicial o de reposo.
 * - [Loading]: Indica que la operación de envío está en curso.
 * - [Success]: Indica que el correo de restablecimiento se envió con éxito.
 * - [Error]: Indica que ocurrió un error durante el proceso, contiene un mensaje descriptivo.
 *
 * @see ForgotPasswordScreen Composable de la pantalla que observa este estado.
 * @see ForgotPasswordViewModel ViewModel que emite estos estados.
 */
package com.app.tibibalance.ui.screens.auth


/**
 * @brief Interfaz sellada que representa los distintos estados posibles de la UI
 * durante el proceso de recuperación de contraseña en [ForgotPasswordScreen].
 */
sealed interface ForgotPasswordUiState {
    /**
     * @brief Estado inicial o inactivo.
     * @details La pantalla está esperando la interacción del usuario (introducir correo y pulsar enviar).
     * No se muestra ningún indicador de progreso ni mensaje de resultado.
     */
    data object Idle                     : ForgotPasswordUiState

    /**
     * @brief Estado de carga.
     * @details Indica que la solicitud para enviar el enlace de restablecimiento está en progreso.
     * La UI típicamente mostrará un indicador de carga (e.g., [ModalInfoDialog] con `loading=true`)
     * y deshabilitará los controles de entrada.
     */
    data object Loading                  : ForgotPasswordUiState

    /**
     * @brief Estado de éxito.
     * @details Indica que el correo electrónico con el enlace para restablecer la contraseña
     * ha sido enviado correctamente a la dirección proporcionada por el usuario.
     * La UI debería mostrar un mensaje de confirmación (e.g., usando [ModalInfoDialog]).
     */
    data object Success                  : ForgotPasswordUiState      // e-mail enviado

    /**
     * @brief Estado de error.
     * @details Indica que ocurrió un problema durante el intento de enviar el correo de
     * restablecimiento (e.g., error de red, correo no encontrado, error del servidor).
     * Contiene un mensaje descriptivo del error para mostrar al usuario (e.g., en un [Snackbar]
     * o en el [ModalInfoDialog]).
     * @param message El [String] con el mensaje de error detallado.
     */
    data class Error(val message: String): ForgotPasswordUiState
}