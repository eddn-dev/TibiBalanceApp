// ui/screens/auth/SignInScreen.kt
package com.app.tibibalance.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID =
    "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com"   // <-- tu client-id web

@Composable
fun SignInScreen(
    nav: NavController,
    vm : SignInViewModel = hiltViewModel()
) {
    /* ---------- estado local ---------- */
    var email by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }

    /* ---------- state UI global ---------- */
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()

    /* ---------- Credential Manager ---------- */
    val ctx      = LocalContext.current
    val activity = ctx as Activity
    val cm       = remember(activity) { CredentialManager.create(activity) }

    /* Snackbar para errores “genéricos” */
    LaunchedEffect(uiState) {
        if (uiState is SignInUiState.Error) {
            snackbar.showSnackbar((uiState as SignInUiState.Error).message)
            vm.consumeError()
        }
    }

    /* ─── Navegar cuando el login termina ─── */
    LaunchedEffect(uiState) {
        val success = uiState as? SignInUiState.Success ?: return@LaunchedEffect

        if (success.verified) {
            // e-mail verificado → Home
            nav.navigate(Screen.Home.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }  // limpias back-stack
            }
        } else {
            // aún falta verificar correo
            nav.navigate(Screen.VerifyEmail.route) {
                popUpTo(Screen.Launch.route) { inclusive = false }
            }
        }
    }


    /* ---------- diálogo spinner ---------- */
    ModalInfoDialog(
        loading  = uiState is SignInUiState.Loading,
        message  = null,
        onAccept = {}
    )

    /* ---------- función Google One-Tap ---------- */
    fun launchGoogleSignIn() = scope.launch {
        val request: GetCredentialRequest = vm.buildGoogleRequest(WEB_CLIENT_ID)

        try {
            val response = cm.getCredential(activity, request)
            val idToken  = GoogleIdTokenCredential
                .createFrom(response.credential.data)
                .idToken                       // String

            if (idToken.isBlank()) {
                snackbar.showSnackbar("Token vacío, intenta de nuevo"); return@launch
            }
            vm.finishGoogleSignIn(idToken)
        } catch (e: Exception) {
            snackbar.showSnackbar("Google sign-in cancelado o falló: ${e.message}")
        }
    }

    /* ---------- UI ---------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ImageContainer(
                resId = R.drawable.ic_login_image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "¡Ingresa para continuar!",
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            /* ----- formulario ----- */
            val fieldErr = uiState as? SignInUiState.FieldError

            FormContainer {
                InputEmail(
                    value          = email,
                    onValueChange  = { email = it; vm.consumeError() },
                    isError        = fieldErr?.emailError != null,
                    supportingText = fieldErr?.emailError
                )

                InputPassword(
                    value          = pass,
                    onValueChange  = { pass = it; vm.consumeError() },
                    isError        = fieldErr?.passError != null,
                    supportingText = fieldErr?.passError
                )
            }

            Spacer(Modifier.height(12.dp))

            TextButtonLink(
                "¿Olvidaste tu contraseña?",
                onClick = { nav.navigate(Screen.Forgot.route) }
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text    = stringResource(R.string.btn_sign_in),
                enabled = uiState !is SignInUiState.Loading,
                onClick = { vm.signIn(email, pass) }
            )

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  •  ", fontSize = 22.sp)
                Divider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            GoogleSignButton(onClick = ::launchGoogleSignIn)

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Aún no tienes una cuenta? ",
                    style = MaterialTheme.typography.bodySmall)
                TextButtonLink(
                    "Regístrate",
                    onClick = { nav.navigate(Screen.SignUp.route) },
                    underline = false
                )
            }
        }

        Header(
            title          = "Iniciar Sesión",
            showBackButton = true,
            onBackClick    = { nav.navigateUp() },
            modifier       = Modifier.align(Alignment.TopCenter)
        )

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
