// ui/screens/auth/ForgotPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
    nav: NavController,
    vm : ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()

    /* ─── Snackbar para errores ─── */
    LaunchedEffect(uiState) {
        if (uiState is ForgotUiState.Error) {
            snackbar.showSnackbar((uiState as ForgotUiState.Error).message)
            vm.clearStatus()
        }
    }

    /* ─── Dialogo genérico (spinner + éxito) ─── */
    ModalInfoDialog(                     // NEW
        loading  = uiState is ForgotUiState.Loading,
        message  = if (uiState is ForgotUiState.Success)
            "Hemos enviado un enlace de recuperación a tu correo."
        else null,
        onAccept = vm::clearStatus
    )

    /* ─── Fondo con degradado ─── */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ImageContainer(
                    resId = R.drawable.password1,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .padding(bottom = 24.dp)
                )

                Description(
                    text = "Ingresa tu correo electrónico y enviaremos un link para recuperar tu contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                FormContainer {
                    InputEmail(
                        value = vm.email,
                        onValueChange = vm::onEmailChange,
                        placeholder   = "Correo electrónico"
                    )
                }

                Spacer(Modifier.height(24.dp))

                PrimaryButton(
                    text    = "Enviar",
                    enabled = uiState !is ForgotUiState.Loading,
                    onClick = vm::sendResetLink,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Header(
            title          = "Recuperar Contraseña",
            showBackButton = true,
            onBackClick    = { nav.navigateUp() },
            modifier       = Modifier.align(Alignment.TopCenter)
        )
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
