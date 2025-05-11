/**
 * @file    ForgotPasswordScreen.kt
 * @ingroup ui_screens_auth // Grupo para pantallas de autenticación
 * @brief   Define el [Composable] para la pantalla de recuperación de contraseña.
 *
 * @details Este archivo contiene la implementación de la UI para la funcionalidad de
 * "Olvidé mi contraseña". Permite al usuario ingresar su dirección de correo
 * electrónico para recibir un enlace de restablecimiento.
 *
 * La pantalla utiliza [ForgotPasswordViewModel] para manejar la lógica de negocio y
 * el estado. Muestra:
 * - Un encabezado ([Header]) con título y botón de retroceso.
 * - Una imagen ilustrativa ([ImageContainer]).
 * - Un texto descriptivo ([Description]).
 * - Un campo de entrada para el correo electrónico ([InputEmail]) dentro de un [FormContainer].
 * - Un botón de acción principal ([PrimaryButton]) para enviar la solicitud.
 *
 * Maneja los estados de la UI (Idle, Loading, Success, Error) emitidos por el ViewModel:
 * - **Loading/Success:** Muestra un [com.app.tibibalance.ui.components.dialogs.ModalInfoDialog] para feedback.
 * - **Error:** Muestra un [Snackbar] con el mensaje de error.
 * Tras un envío exitoso, navega a la pantalla de inicio de sesión ([Screen.SignIn]).
 *
 * @see ForgotPasswordViewModel ViewModel que gestiona la lógica de esta pantalla.
 * @see ForgotPasswordUiState Estados de la UI para esta pantalla.
 * @see Header Componente reutilizable para la barra superior.
 * @see ImageContainer Componente para mostrar la imagen.
 * @see Description Componente para el texto instructivo.
 * @see FormContainer Contenedor para el campo de email.
 * @see InputEmail Campo de texto para el correo electrónico.
 * @see PrimaryButton Botón para enviar la solicitud.
 * @see com.app.tibibalance.ui.components.dialogs.ModalInfoDialog Diálogo para estados de carga y éxito.
 * @see androidx.compose.material3.SnackbarHost Para mostrar errores.
 * @see com.app.tibibalance.ui.navigation.Screen Rutas de navegación.
 */
// ui/screens/auth/ForgotPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.components.inputs.InputEmail
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.navigation.Screen

/**
 * @brief Composable que define la interfaz de usuario para la pantalla de recuperación de contraseña.
 *
 * @details Muestra una imagen, un texto instructivo, un campo para ingresar el correo electrónico
 * y un botón para enviar la solicitud de restablecimiento. Utiliza [com.app.tibibalance.ui.components.dialogs.ModalInfoDialog] para
 * indicar el estado de carga y éxito, y un [Snackbar] para errores.
 *
 * @param nav El [NavController] utilizado para la navegación entre pantallas.
 * @param vm La instancia de [ForgotPasswordViewModel] (inyectada por Hilt) que maneja la lógica
 * y el estado de esta pantalla.
 */
@Composable
fun ForgotPasswordScreen(
    nav: NavController,
    vm : ForgotPasswordViewModel = hiltViewModel()
) {
    /* ---- UI-state & helpers ---- */
    // Observa el estado de la UI emitido por el ViewModel.
    val uiState  by vm.ui.collectAsState()
    // Estado para el Snackbar que mostrará errores.
    val snackbar = remember { SnackbarHostState() }
    // Scope de corrutina para lanzar operaciones asíncronas (como mostrar el Snackbar).
    val scope    = rememberCoroutineScope()

    /* Snackbar solo para error */
    // Efecto lanzado cuando uiState cambia, específicamente para manejar errores.
    LaunchedEffect(uiState) {
        if (uiState is ForgotPasswordUiState.Error) {
            // Muestra el mensaje de error en el Snackbar.
            snackbar.showSnackbar((uiState as ForgotPasswordUiState.Error).message)
            // Limpia el estado de error en el ViewModel para que el Snackbar no se muestre repetidamente.
            vm.clearStatus()
        }
    }

    /* ---------- ModalInfoDialog para Loading y Success ---------- */
    // Determina si el diálogo modal debe ser visible (cuando está cargando o hay un mensaje de éxito).
    val dialogVisible = uiState is ForgotPasswordUiState.Loading ||
            uiState is ForgotPasswordUiState.Success

    ModalInfoDialog(
        visible = dialogVisible,

        /* fase SPINNER */
        loading = uiState is ForgotPasswordUiState.Loading, // Muestra el spinner si está cargando.

        /* fase ÉXITO */
        icon = if (uiState is ForgotPasswordUiState.Success) Icons.Default.Check else null, // Icono de check para éxito.
        message = if (uiState is ForgotPasswordUiState.Success)
            "Hemos enviado un enlace para restablecer tu contraseña.\nRevisa tu correo." // Mensaje de éxito.
        else null,

        /* botón “Aceptar” sólo en éxito */
        primaryButton = if (uiState is ForgotPasswordUiState.Success)
            DialogButton("Aceptar") { // Define el botón primario para el estado de éxito.
                vm.clearStatus() // Limpia el estado de éxito en el ViewModel.
                // Navega a la pantalla de SignIn, eliminando la pantalla actual del backstack.
                nav.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.Forgot.route) { inclusive = true }
                }
            }
        else null,

        /* bloqueo de back/click-outside mientras carga */
        // Permite descartar el diálogo con el botón "Atrás" solo si no está cargando.
        dismissOnBack = uiState !is ForgotPasswordUiState.Loading,
        // Permite descartar el diálogo pulsando fuera solo si no está cargando.
        dismissOnClickOutside = uiState !is ForgotPasswordUiState.Loading
    )

    /* ---- Fondo degradado ---- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor principal que ocupa toda la pantalla.
    Box(Modifier.fillMaxSize()) {

        /* ---------- CONTENIDO PRINCIPAL ---------- */
        // Columna para organizar los elementos verticalmente, con scroll.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient) // Aplica el fondo degradado.
                .verticalScroll(rememberScrollState()) // Permite el desplazamiento vertical.
                // Padding para el contenido, especialmente para dejar espacio al Header flotante.
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente.
        ) {
            // Imagen ilustrativa.
            ImageContainer(
                resId = R.drawable.password1, // ID del recurso drawable.
                contentDescription = null, // Decorativa, el texto explica el propósito.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 24.dp)
            )

            // Texto instructivo.
            Description(
                text = "Ingresa tu correo electrónico y enviaremos un link para recuperar tu contraseña",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center // Texto centrado.
            )

            Spacer(Modifier.height(24.dp)) // Espacio vertical.

            // Contenedor para el campo de email.
            FormContainer {
                InputEmail(
                    value = vm.email, // El valor del email viene del ViewModel.
                    onValueChange = vm::onEmailChange, // Actualiza el email en el ViewModel.
                    label = "Correo electrónico" // Etiqueta para el campo.
                )
            }

            Spacer(Modifier.height(32.dp))

            // Botón de envío.
            PrimaryButton(
                text = "Enviar",
                // Habilita el botón solo si no se está en estado de carga.
                enabled = uiState !is ForgotPasswordUiState.Loading,
                onClick = vm::sendResetLink, // Llama a la función del ViewModel para enviar el enlace.
                modifier = Modifier.fillMaxWidth()
            )
        }

        /* ---------- HEADER flotante ---------- */
        // Se muestra superpuesto en la parte superior.
        Header(
            title          = "Recuperar Contraseña",
            showBackButton = true, // Muestra el botón de retroceso.
            onBackClick    = { nav.navigateUp() }, // Acción para el botón de retroceso.
            modifier       = Modifier.align(Alignment.TopCenter) // Alinea el Header en la parte superior.
        )

        /* ---------- Snackbar para errores ---------- */
        // Se muestra en la parte inferior.
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
