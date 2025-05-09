/**
 * @file    SignInUiState.kt
 * @ingroup ui_screens_auth // Grupo para estados de UI de pantallas de autenticación
 * @brief   Define los posibles estados de la interfaz de usuario para la pantalla de inicio de sesión ([SignInScreen]).
 *
 * @details Esta interfaz sellada (`sealed interface`) encapsula los diferentes estados por los que
 * puede pasar la pantalla de inicio de sesión mientras el usuario interactúa con ella y
 * el [SignInViewModel] procesa la lógica de autenticación.
 *
 * Cada estado representa una situación visual o de interacción específica:
 * - [Idle]: El estado inicial o de reposo, esperando la entrada del usuario.
 * - [Loading]: Indica que la operación de inicio de sesión (ya sea por correo/contraseña o Google)
 * está en curso. La UI debería mostrar un indicador de carga y deshabilitar entradas.
 * - [FieldError]: Indica que hubo errores de validación locales o específicos devueltos por el
 * backend relacionados con los campos de entrada (correo o contraseña). Contiene mensajes
 * de error opcionales para cada campo.
 * - [Error]: Indica que ocurrió un error general no asociado directamente a un campo
 * específico (e.g., error de red, cuenta deshabilitada, fallo inesperado). Contiene un
 * mensaje de error global.
 * - [Success]: Indica que el inicio de sesión fue exitoso. Contiene un flag booleano
 * (`verified`) para informar a la UI si el correo del usuario está verificado o no,
 * permitiendo la navegación al destino adecuado ([Screen.Main] o [Screen.VerifyEmail]).
 *
 * @see SignInScreen Composable de la pantalla que observa este estado.
 * @see SignInViewModel ViewModel que emite estos estados.
 */
// ui/screens/auth/SignInUiState.kt
package com.app.tibibalance.ui.screens.auth

/**
 * @brief Interfaz sellada que representa los distintos estados posibles de la UI
 * durante el proceso de inicio de sesión en [SignInScreen].
 */
sealed interface SignInUiState {
    /**
     * @brief Estado inicial o inactivo de la pantalla.
     * @details La UI está lista para recibir la entrada del usuario. No se muestran
     * indicadores de carga ni mensajes de error/éxito.
     */
    data object Idle                       : SignInUiState

    /**
     * @brief Estado de carga.
     * @details Indica que la solicitud de inicio de sesión (correo/contraseña o Google)
     * está siendo procesada. La UI debe mostrar un indicador de progreso y
     * deshabilitar los controles de entrada y botones de acción.
     */
    data object Loading                    : SignInUiState

    /**
     * @brief Estado que indica errores de validación específicos en los campos de entrada.
     * @details Contiene mensajes de error opcionales para los campos de correo electrónico
     * y contraseña. La UI debe mostrar estos mensajes asociados a los campos
     * correspondientes para guiar al usuario.
     * @param emailError Mensaje de error [String?] para el campo de correo electrónico, o `null` si es válido.
     * @param passError Mensaje de error [String?] para el campo de contraseña, o `null` si es válido.
     */
    data class  FieldError(
        val emailError: String? = null,
        val passError : String? = null
    )                                      : SignInUiState

    /**
     * @brief Estado que indica un error general o no específico de un campo.
     * @details Representa errores que no se pueden atribuir directamente a la entrada del usuario,
     * como problemas de red, errores del servidor, o credenciales incorrectas que no
     * distinguen entre usuario/contraseña erróneos. La UI debe mostrar el `message`
     * de forma prominente (e.g., en un [ModalInfoDialog] o [Snackbar]).
     * @param message El mensaje [String] descriptivo del error ocurrido.
     */
    data class  Error(val message: String) : SignInUiState

    /**
     * @brief Estado de éxito tras un inicio de sesión correcto.
     * @details Indica que la autenticación se completó satisfactoriamente. Contiene
     * información adicional sobre el estado de verificación del correo del usuario.
     * @param verified Un [Boolean] que es `true` si el correo electrónico del usuario
     * está verificado por Firebase Authentication, y `false` en caso contrario. La UI
     * utiliza este flag para decidir si navegar a [Screen.Main] o a [Screen.VerifyEmail].
     */
    data class  Success(val verified: Boolean) : SignInUiState
}