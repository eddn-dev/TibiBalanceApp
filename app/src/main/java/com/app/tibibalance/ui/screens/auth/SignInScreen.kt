package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    nav: NavController,
    vm: SignInViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }

    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()          // ← para lanzar coroutines

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        /* ---------- Contenido ---------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ImageContainer(
                resId = R.drawable.ic_login_image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "¡Ingresa para continuar!",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            FormContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                cornerRadiusDp = 16
            ) {
                InputEmail(
                    value = email,
                    onValueChange = { email = it }
                )
                Spacer(Modifier.height(12.dp))
                InputPassword(
                    value = pass,
                    onValueChange = { pass = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButtonLink(
                text = "¿Olvidaste tu contraseña? Clic aquí",
                onClick = { nav.navigate(Screen.Forgot.route) }
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.btn_sign_in),
                onClick = {
                    vm.signIn(email, pass) { msg ->
                        scope.launch { snackbar.showSnackbar(msg) }   // ✅
                    }
                }
            )

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  •  ", fontSize = 22.sp)
                Divider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            GoogleSignButton(onClick = { vm.signInWithGoogle() })

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "¿Aún no tienes una cuenta? ",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
                TextButtonLink(
                    text = "Regístrate",
                    onClick = { nav.navigate(Screen.SignUp.route) },
                    underline = false
                )
            }
        }

        /* Header */
        Header(
            title = "Iniciar Sesión",
            showBackButton = true,
            onBackClick = { nav.navigateUp() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        /* Snackbar */
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
