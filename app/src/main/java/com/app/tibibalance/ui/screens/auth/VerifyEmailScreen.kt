// ui/screens/auth/VerifyEmailScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.TextButtonLink
import com.app.tibibalance.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    nav: NavController,
    vm : VerifyEmailViewModel = hiltViewModel()
) {
    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {

        /* ---------- contenido central ---------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /* título + descripción */
            Text(
                text = "¡Revisa tu correo!",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hemos enviado un mensaje de verificación a:\n${vm.email}",
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            /* ---------- Botón reenviar ---------- */
            PrimaryButton(
                text = "Reenviar correo",
                onClick = {
                    vm.resend { ok, msg ->
                        if (!ok) scope.launch { snackbar.showSnackbar(msg) }
                        else     scope.launch { snackbar.showSnackbar("Correo reenviado") }
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- Botón ya verifiqué ---------- */
            PrimaryButton(
                text = "Ya lo verifiqué",
                onClick = {
                    vm.check(
                        onVerified = {
                            nav.navigate(Screen.Home.route) {
                                popUpTo(Screen.Launch.route) { inclusive = true }
                            }
                        },
                        onNotYet = {
                            scope.launch { snackbar.showSnackbar("Aún no está verificado") }
                        }
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            /* ---------- Cerrar sesión ---------- */
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

        /* ---------- header + snackbar ---------- */
        Header(
            title          = "Verifica tu correo",
            showBackButton = false,
            modifier       = Modifier.align(Alignment.TopCenter)
        )
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
