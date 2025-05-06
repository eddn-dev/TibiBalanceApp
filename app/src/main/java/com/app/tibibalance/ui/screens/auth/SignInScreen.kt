/*
 * ui/screens/auth/SignInScreen.kt
 *
 * Versión completa con:
 *   • Dialogo ModalInfoDialog para LOADING y ERROR (sin snackbars de ViewModel)
 *   • Snackbar únicamente para errores locales de Google One-Tap
 *   • Manejo de errores por-campo en los inputs
 */

package com.app.tibibalance.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import com.app.tibibalance.ui.components.DialogButton

private const val WEB_CLIENT_ID =
    "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com"

@Composable
fun SignInScreen(
    nav: NavController,
    vm : SignInViewModel = hiltViewModel()
) {
    /* ---------- estado local ---------- */
    var email by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }

    /* ---------- estado global ---------- */
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }          // sólo Google-OneTap
    val scope    = rememberCoroutineScope()

    /* ---------- Credential Manager ---------- */
    val ctx      = LocalContext.current
    val activity = ctx as Activity
    val cm       = remember(activity) { CredentialManager.create(activity) }

    /* ───── Navegar cuando el login termina ───── */
    LaunchedEffect(uiState) {
        (uiState as? SignInUiState.Success)?.let { success ->
            if (success.verified) {
                nav.navigate(Screen.Main.route) {
                    popUpTo(Screen.Launch.route) { inclusive = true }
                }
            } else {
                nav.navigate(Screen.VerifyEmail.route) {
                    popUpTo(Screen.Launch.route) { inclusive = false }
                }
            }
        }
    }

    /* ---------- One-Tap helper ---------- */
    fun launchGoogleSignIn() = scope.launch {
        val request: GetCredentialRequest = vm.buildGoogleRequest(WEB_CLIENT_ID)

        try {
            val response = cm.getCredential(activity, request)
            val idToken  = GoogleIdTokenCredential
                .createFrom(response.credential.data).idToken

            if (idToken.isBlank()) {
                snackbar.showSnackbar("Token vacío, intenta de nuevo"); return@launch
            }
            vm.finishGoogleSignIn(idToken)
        } catch (e: Exception) {
            snackbar.showSnackbar("Google cancelado o falló: ${e.message}")
        }
    }

    /* ---------- dialogo modal ---------- */
    val isLoading  = uiState is SignInUiState.Loading
    val isError    = uiState is SignInUiState.Error
    val showDialog = isLoading || isError

    ModalInfoDialog(
        visible = showDialog,
        loading = isLoading,

        icon    = if (isError) Icons.Default.Error else null,
        iconColor   = MaterialTheme.colorScheme.error,
        iconBgColor = MaterialTheme.colorScheme.errorContainer,
        title   = if (isError) "Error" else null,
        message = (uiState as? SignInUiState.Error)?.message,

        primaryButton = if (isError)
            DialogButton("Aceptar") { vm.consumeError() } else null,

        dismissOnBack         = !isLoading,
        dismissOnClickOutside = !isLoading
    )

    /* ---------- errores de campo ---------- */
    val fieldErr = uiState as? SignInUiState.FieldError

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
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )  {

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

            FormContainer {
                InputEmail(
                    value          = email,
                    onValueChange  = { email = it; vm.consumeError() },
                    label          = "Correo electrónico",          // ⬅️  NUEVO
                    isError        = fieldErr?.emailError != null,
                    supportingText = fieldErr?.emailError
                )

                InputPassword(
                    value          = pass,
                    onValueChange  = { pass = it; vm.consumeError() },
                    label          = "Contraseña",                  // ⬅️  NUEVO
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
            Spacer(Modifier.height(24.dp))
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
