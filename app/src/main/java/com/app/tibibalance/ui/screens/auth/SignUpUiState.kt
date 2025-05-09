/**
 * @file    SignUpUiState.kt
 * @ingroup ui_screens_auth // Grupo para estados de UI de pantallas de autenticación
 * @brief   Define los posibles estados de la interfaz de usuario para la pantalla de registro ([SignUpScreen]).
 *
 * @details Esta interfaz sellada (`sealed interface`) encapsula los diferentes estados visuales y de
 * interacción por los que puede pasar la pantalla de registro mientras el usuario
 * completa el formulario y el [SignUpViewModel] procesa la lógica de creación de cuenta,
 * ya sea mediante correo/contraseña o Google One-Tap.
 *
 * Cada estado representa una situación específica:
 * - [Idle]: Estado inicial o de reposo, esperando la entrada del usuario.
 * - [Loading]: Indica que la operación de registro (con correo o Google) está en curso.
 * - [FieldError]: Señala errores de validación específicos en los campos del formulario
 * (nombre de usuario, fecha de nacimiento, correo, contraseña, confirmación).
 * - [Error]: Representa un error general no asociado a un campo específico (e.g., error de red,
 * fallo inesperado del servidor).
 * - [Success]: Indica que el registro mediante el formulario de correo/contraseña fue exitoso
 * y se envió el correo de verificación. Contiene el email registrado.
 * - [GoogleSuccess]: Indica que el registro o inicio de sesión mediante Google One-Tap
 * fue exitoso.
 *
 * La pantalla [SignUpScreen] observa estos estados y reacciona mostrando diálogos,
 * indicadores de carga, mensajes de error en los campos o navegando a la siguiente pantalla.
 *
 * @see SignUpScreen Composable de la pantalla que observa este estado.
 * @see SignUpViewModel ViewModel que emite estos estados.
 */

package com.app.tibibalance.ui.screens.auth

sealed interface SignUpUiState {
    /**
     * @brief Estado inicial o inactivo de la pantalla de registro.
     * @details La UI está lista para que el usuario comience a introducir sus datos.
     * No se muestran indicadores de carga ni mensajes de error/éxito.
     */
    data object Idle : SignUpUiState

    /**
     * @brief Estado de carga durante el proceso de registro.
     * @details Indica que la aplicación está procesando la solicitud de creación de cuenta
     * (ya sea con correo/contraseña o con Google). La UI debería mostrar un indicador
     * de progreso (e.g., en un [ModalInfoDialog]) y deshabilitar los controles de entrada.
     */
    data object Loading : SignUpUiState

    /**
     * @brief Estado que representa errores de validación específicos en los campos del formulario.
     * @details Contiene mensajes de error opcionales ([String]?) para cada campo validado
     * (nombre de usuario, fecha de nacimiento, correo, contraseñas). Si un campo es válido,
     * su propiedad correspondiente será `null`. La UI ([SignUpScreen]) utiliza estos mensajes
     * para mostrarlos como texto de soporte debajo de los campos de entrada erróneos.
     *
     * @param userNameError Mensaje de error para el campo "Nombre de usuario", o `null` si es válido.
     * @param birthDateError Mensaje de error para el campo "Fecha de nacimiento", o `null` si es válido.
     * @param emailError Mensaje de error para el campo "Correo electrónico", o `null` si es válido.
     * @param pass1Error Mensaje de error para el campo "Contraseña", o `null` si es válido.
     * @param pass2Error Mensaje de error para el campo "Confirmar contraseña", o `null` si coincide y es válido.
     */
    data class FieldError(
        val userNameError : String? = null,
        val birthDateError: String? = null,
        val emailError    : String? = null,
        val pass1Error    : String? = null,
        val pass2Error    : String? = null,
    ) : SignUpUiState

    /**
     * @brief Estado que representa un error general ocurrido durante el registro.
     * @details Indica un fallo no específico de un campo de entrada, como un problema de red,
     * un error interno del servidor, o una excepción inesperada. La UI debe mostrar el
     * `message` contenido de forma clara al usuario (e.g., en un [ModalInfoDialog] o [Snackbar]).
     *
     * @param message El [String] descriptivo del error ocurrido.
     */
    data class Error(val message: String) : SignUpUiState        // globales

    /**
     * @brief Estado de éxito tras completar el registro mediante el formulario de correo/contraseña.
     * @details Indica que la cuenta se creó correctamente en Firebase y se solicitó el envío
     * del correo de verificación. La UI suele mostrar un mensaje de confirmación
     * ([ModalInfoDialog]) y luego navegar a la pantalla de verificación ([Screen.VerifyEmail]).
     *
     * @param email El [String] del correo electrónico con el que se registró el usuario.
     * Puede ser útil para mostrarlo en el mensaje de confirmación.
     */
    data class Success(val email: String)  : SignUpUiState       // vía formulario

    /**
     * @brief Estado de éxito tras completar el registro o inicio de sesión mediante Google One-Tap.
     * @details Indica que la autenticación con Google fue exitosa. Dado que las cuentas de Google
     * se consideran verificadas, la UI típicamente navegará directamente a la pantalla principal
     * de la aplicación ([Screen.Main]) después de este estado.
     */
    data object GoogleSuccess                   : SignUpUiState       // ✅ nuevo
}