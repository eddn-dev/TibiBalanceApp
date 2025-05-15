package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.inputs.InputPassword
import com.app.tibibalance.ui.components.texts.Description
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun ChangePasswordScreenPreviewOnly(
    navController: NavHostController
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    var Password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val showMismatchError = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .verticalScroll(rememberScrollState())
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageContainer(
                resId = R.drawable.ic_reset_password_image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 24.dp)
            )

            Description(
                text = "Ingresa tu nueva contraseña",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            FormContainer {
                InputPassword(
                    value = Password,
                    onValueChange = { Password = it },
                    label = "Contraseña Actual"
                )
                InputPassword(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Nueva Contraseña",
                    isError = showMismatchError,
                    supportingText = if (showMismatchError) "Las contraseñas no coinciden" else null
                )
                InputPassword(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar Contraseña",
                    isError = showMismatchError,
                    supportingText = if (showMismatchError) "Las contraseñas no coinciden" else null
                )
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text = "Guardar",
                onClick = { /* Sin lógica */ }
            )
        }

        Header(
            title = "Cambiar Contraseña",
            showBackButton = true,
            onBackClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    val navController = rememberNavController()
    ChangePasswordScreenPreviewOnly(navController)
}
