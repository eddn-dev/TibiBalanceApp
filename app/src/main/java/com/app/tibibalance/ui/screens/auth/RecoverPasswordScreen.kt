package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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

@Composable
fun RecoverPasswordScreen(
    nav: NavController,
    vm: RecoverPasswordViewModel = hiltViewModel() // Asegúrate de tener este ViewModel implementado
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by vm.ui

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3EA8FE).copy(alpha = 0.25f), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageContainer(
                resId = R.drawable.recover, // Usa la imagen que subiste
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Ingresa tu nueva contraseña",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            FormContainer {
                InputPassword(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Nueva Contraseña"
                )
                Spacer(modifier = Modifier.height(16.dp))
                InputPassword(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirmar Contraseña"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Enviar",
                enabled = uiState !is RecoverPasswordUiState.Loading,
                onClick = {
                    vm.resetPassword(password, confirmPassword)
                }
            )
        }

        Header(
            title = "Reestablecer Contraseña",
            showBackButton = true,
            onBackClick = { nav.navigateUp() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (uiState is RecoverPasswordUiState.Success) {
            ModalInfoContainer(
                type = ModalType.Success,
                title = "",
                message = "Tu contraseña ha sido actualizada correctamente.",
                onDismiss = {
                    vm.dismissSuccess()
                    nav.navigate("login") // Redirige al login u otra pantalla
                }
            )
        }

        if (uiState is RecoverPasswordUiState.Error) {
            val message = (uiState as RecoverPasswordUiState.Error).message
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
