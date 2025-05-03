// ui/screens/auth/ForgotPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.screens.auth.ForgotPasswordUiState

@Composable
fun ForgotPasswordScreen(
    nav: NavController,
    vm: ForgotPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val uiState by vm.ui
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ForgotPasswordUiState.Error -> {
                snackbar.showSnackbar(state.message)
                vm.consumeError()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
            ))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageContainer(
                resId = R.drawable.password1, // Asegúrate de tener esta imagen
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Ingresa tu correo electrónico y\nenviaremos un link para\nrecuperar tu contraseña",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            FormContainer {
                InputEmail(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Enviar",
                enabled = uiState !is ForgotPasswordUiState.Loading,
                onClick = { vm.sendResetEmail(email) }
            )
        }

        Header(
            title = "Recuperar Contraseña",
            showBackButton = true,
            onBackClick = { nav.navigateUp() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (uiState is ForgotPasswordUiState.Success) {
            ModalInfoContainer(
                type = ModalType.Success,
                title = "",
                message = "Se ha enviado a tu correo un enlace para recuperar tu contraseña.",
                onDismiss = {
                    vm.dismissSuccess()
                    nav.navigateUp()
                }
            )
        }

        if (uiState is ForgotPasswordUiState.Error) {
            val message = (uiState as ForgotPasswordUiState.Error).message
            ModalInfoContainer(
                type = ModalType.Error,
                title = "Error",
                message = message,
                onDismiss = {
                    vm.consumeError()
                }
            )
        }
    }
}
