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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    nav: NavController,                         // ⬅️  igual que SignIn/Up
    vm : ForgotPasswordViewModel = hiltViewModel()
) {
    /* ---- UI-state & helpers ---- */
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()

    /* Snackbar solo para error */
    LaunchedEffect(uiState) {
        if (uiState is ForgotPasswordUiState.Error) {
            snackbar.showSnackbar((uiState as ForgotPasswordUiState.Error).message)
            vm.clearStatus()
        }
    }

    /* ---------- ModalInfoDialog ---------- */

    val dialogVisible = uiState is ForgotPasswordUiState.Loading ||
            uiState is ForgotPasswordUiState.Success

    ModalInfoDialog(
        visible = dialogVisible,

        /* fase SPINNER */
        loading = uiState is ForgotPasswordUiState.Loading,

        /* fase ÉXITO */
        icon    = if (uiState is ForgotPasswordUiState.Success) Icons.Default.Check else null,
        message = if (uiState is ForgotPasswordUiState.Success)
            "Hemos enviado un enlace para restablecer tu contraseña.\nRevisa tu correo."
        else null,

        /* botón “Aceptar” sólo en éxito */
        primaryButton = if (uiState is ForgotPasswordUiState.Success)
            DialogButton("Aceptar") {
                vm.clearStatus()
                nav.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.Forgot.route) { inclusive = true }
                }
            }
        else null,

        /* bloqueo de back/click-outside mientras carga */
        dismissOnBack         = uiState !is ForgotPasswordUiState.Loading,
        dismissOnClickOutside = uiState !is ForgotPasswordUiState.Loading
    )

    /* ---- Fondo degradado ---- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(Modifier.fillMaxSize()) {

        /* ---------- CONTENIDO ---------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .verticalScroll(rememberScrollState())
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageContainer(
                resId = R.drawable.password1,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 24.dp)
            )

            Description(
                text = "Ingresa tu correo electrónico y enviaremos un link para recuperar tu contraseña",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            FormContainer {
                InputEmail(
                    value = vm.email,
                    onValueChange = vm::onEmailChange,
                    placeholder = "Correo electrónico"
                )
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text    = "Enviar",
                enabled = uiState !is ForgotPasswordUiState.Loading,
                onClick = vm::sendResetLink,
                modifier = Modifier.fillMaxWidth()
            )
        }

        /* ---------- HEADER flotante ---------- */
        Header(
            title          = "Recuperar Contraseña",
            showBackButton = true,
            onBackClick    = { nav.navigateUp() },
            modifier       = Modifier.align(Alignment.TopCenter)
        )

        /* ---------- Snackbar ---------- */
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
