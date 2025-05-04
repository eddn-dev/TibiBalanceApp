// ui/screens/auth/VerifyEmailScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.TextButtonLink
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    nav: NavController,
    //vm: VerifyEmailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()

    // degradado vertical del azul claro al blanco
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF3EA8FE).copy(alpha = .25f),
            Color.White
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1) Fondo degradado
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(gradient)
        )

        // 2) Header blanco
        Header(
            title          = "Verificar correo",
            showBackButton = false,
            modifier       = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .align(Alignment.TopCenter)
                .padding(vertical = 16.dp)
        )

        // 3) Contenido central
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text      = "¡Revisa tu correo!",
                style     = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // imagen del sobre
            ImageContainer(
                resId              = R.drawable.messageemailimage,
                contentDescription = "Email enviado",
                modifier           = Modifier.size(300.dp),
            )

            /*Text(
                text = "Hemos enviado un mensaje de verificación a:\n${vm.email}",
                textAlign = TextAlign.Center
            )*/

            // descripción estática
            Text(
                text      = "Se ha enviado a tu correo un enlace\n para recuperar tu contraseña",
                style     = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            PrimaryButton(
                text      = "Reenviar correo",
                onClick   = {
                    /*vm.resend { ok, msg ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (ok) "Correo reenviado" else msg
                            )
                        }
                    }*/
                    scope.launch { snackbarHostState.showSnackbar("Correo reenviado") }
                },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            )

            Spacer(Modifier.height(15.dp))

            PrimaryButton(
                text      = "Ya lo verifiqué",
                onClick   = {
                    /*vm.check(
                        onVerified = {
                            nav.navigate(Screen.Home.route) {
                                popUpTo(Screen.Launch.route) { inclusive = true }
                            }
                        },
                        onNotYet = {
                            scope.launch { snackbarHostState.showSnackbar("Aún no está verificado") }
                        }
                    )*/
                    scope.launch { snackbarHostState.showSnackbar("¡Verificado!") }
                    nav.navigate("home") {
                        popUpTo("launch") { inclusive = true }
                    }
                },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                container = Color(0xFF3EA8FE)
            )

            Spacer(Modifier.height(20.dp))

            TextButtonLink(
                text    = "Cerrar sesión",
                onClick = {
                    /*vm.signOut()
                    nav.navigate(Screen.Launch.route) {
                        popUpTo(Screen.Launch.route) { inclusive = true }
                    }*/
                    nav.popBackStack()
                }
            )
        }

        // 4) Snackbar en la parte inferior
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(
    showBackground = true,
    widthDp  = 412,
    heightDp = 918,
    name     = "VerifyEmail 412x918"
)
@Composable
fun PreviewVerifyEmailScreen() {
    VerifyEmailScreen(nav = rememberNavController())
}
