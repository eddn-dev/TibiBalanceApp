package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
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
import com.app.tibibalance.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    nav: NavController,
    vm : VerifyEmailViewModel = hiltViewModel()
) {
    val uiState by vm.ui.collectAsState()
    val scope   = rememberCoroutineScope()

    /* ---------- Dialog decisions ---------- */
    val loading  = uiState is VerifyEmailUiState.Loading
    val success  = uiState as? VerifyEmailUiState.Success
    val error    = uiState as? VerifyEmailUiState.Error
    val showDialog = loading || success != null || error != null

    ModalInfoDialog(
        visible = showDialog,
        loading = loading,
        icon    = when {
            success != null -> Icons.Default.Check
            error   != null -> Icons.Default.Error
            else            -> null
        },
        iconColor = when {
            success != null -> MaterialTheme.colorScheme.onPrimaryContainer
            error   != null -> MaterialTheme.colorScheme.error
            else            -> MaterialTheme.colorScheme.onPrimaryContainer
        },
        iconBgColor = when {
            success != null -> MaterialTheme.colorScheme.primaryContainer
            error   != null -> MaterialTheme.colorScheme.errorContainer
            else            -> MaterialTheme.colorScheme.primaryContainer
        },
        title = when {
            success != null -> "Listo"
            error   != null -> "Error"
            else            -> null
        },
        message = success?.message ?: error?.message,
        primaryButton = when {
            success != null -> DialogButton("Aceptar") {
                vm.clear()
                if (success.goHome) {
                    nav.navigate(Screen.Main.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }
                }
            }
            error != null -> DialogButton("Aceptar") { vm.clear() }
            else -> null
        },
        dismissOnBack         = !loading,
        dismissOnClickOutside = !loading
    )

    /* ---------- fondo ---------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        Header(
            title          = "Verificar correo",
            showBackButton = false,
            modifier       = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(56.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "¡Revisa tu correo!",
                style     = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            ImageContainer(
                resId = R.drawable.messageemailimage,
                contentDescription = "Email enviado",
                modifier = Modifier.size(300.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Se ha enviado a tu correo un enlace\npara verificar tu cuenta",
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            PrimaryButton(
                text = "Reenviar correo",
                onClick = { vm.resend() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(Modifier.height(15.dp))

            PrimaryButton(
                text      = "Ya lo verifiqué",
                container = Color(0xFF3EA8FE),
                onClick   = { vm.verify() },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(Modifier.height(20.dp))

            TextButtonLink(
                text = "Cerrar sesión",
                onClick = {
                    vm.signOut()
                    nav.navigate(Screen.Launch.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
