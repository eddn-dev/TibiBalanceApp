package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import com.app.tibibalance.ui.navigation.Screen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.tibibalance.R
import com.app.tibibalance.domain.util.PasswordValidator
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.inputs.InputPassword
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.screens.profile.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsState()

    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // Estados para mostrar modales
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMsg by remember { mutableStateOf("") }

    // Errores locales en tiempo real
    val strengthError = PasswordValidator.validateStrength(newPass)
    val mismatchError = if (newPass.isNotEmpty() && confirm.isNotEmpty() && newPass != confirm)
        "Las contraseñas no coinciden"
    else null

    // Observa errores de re-autenticación
    LaunchedEffect(ui.error) {
        ui.error?.let { err ->
            if (err == "La contraseña actual es incorrecta") {
                dialogMsg = err
                showErrorDialog = true
                // limpiar campos
                current = ""
                newPass = ""
                confirm = ""
            }
            viewModel.consumeError()
        }
    }

    // Observa éxito
    LaunchedEffect(ui.success) {
        if (ui.success) {
            dialogMsg = "¡Contraseña cambiada con éxito!"
            showSuccessDialog = true
            viewModel.clearSuccess()
        }
    }

    // Fondo degradado
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
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

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .background(
                        color = Color(0xFFdeedf4),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1) Contraseña actual
                InputPassword(
                    value = current,
                    onValueChange = {
                        current = it
                        viewModel.consumeError()
                    },
                    label = "Contraseña Actual",
                    isError = false,
                    supportingText = null
                )
                Spacer(Modifier.height(8.dp))

                // 2) Nueva contraseña
                InputPassword(
                    value = newPass,
                    onValueChange = {
                        newPass = it
                        viewModel.consumeError()
                    },
                    label = "Nueva Contraseña",
                    isError = strengthError != null,
                    supportingText = strengthError
                )
                Spacer(Modifier.height(8.dp))

                // 3) Confirmar contraseña
                InputPassword(
                    value = confirm,
                    onValueChange = {
                        confirm = it
                        viewModel.consumeError()
                    },
                    label = "Confirmar Contraseña",
                    isError = mismatchError != null,
                    supportingText = mismatchError
                )
            }

            Spacer(Modifier.height(32.dp))

            // --- Aquí calculamos cuándo habilitar "Guardar" ---
            val saveEnabled =
                !ui.isLoading &&
                        current.isNotBlank() &&
                        strengthError == null &&
                        mismatchError == null

            PrimaryButton(
                text = if (ui.isLoading) "Cambiando..." else "Guardar",
                onClick = { viewModel.changePassword(current, newPass, confirm) },
                enabled = saveEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        Header(
            title = "Cambiar Contraseña",
            showBackButton = true,
            onBackClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Modal de error
        ModalInfoDialog(
            visible = showErrorDialog,
            message = dialogMsg,
            primaryButton = DialogButton("Aceptar") {
                showErrorDialog = false
            }
        )

        // Modal de éxito
        ModalInfoDialog(
            visible = showSuccessDialog,
            message = dialogMsg,
            primaryButton = DialogButton("Aceptar") {
                showSuccessDialog = false
                // doble pop para volver a SettingsScreen
                navController.popBackStack()
                navController.popBackStack()
            }
        )
    }
}
