/*
 * ui/screens/auth/SignUpScreen.kt
 *
 * • Diálogo ModalInfoDialog para LOADING, SUCCESS y ERROR
 * • Snackbar solo para incidentes locales (Google One-Tap)
 * • Inputs con errores de campo vía SignUpUiState.FieldError
 * • Flujo One-Tap, DatePicker y Credential Manager
 */

package com.app.tibibalance.ui.screens.auth

import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val WEB_CLIENT_ID =
    "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com"

@Composable
fun SignUpScreen(
    nav: NavController,
    vm : SignUpViewModel = hiltViewModel()
) {
    /* ------------- estado local de inputs ------------- */
    var username  by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var email     by remember { mutableStateOf("") }
    var pass1     by remember { mutableStateOf("") }
    var pass2     by remember { mutableStateOf("") }

    /* ------------- estado global ------------- */
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }         // solo Google One-Tap
    val scope    = rememberCoroutineScope()

    /* ------------- Android helpers ------------- */
    val ctx      = LocalContext.current
    val activity = ctx as Activity
    val cm       = remember(activity) { CredentialManager.create(activity) }

    /* ------------- One-Tap helper ------------- */
    fun launchGoogleSignIn() = scope.launch {
        try {
            val response = cm.getCredential(
                activity,
                vm.buildGoogleRequest(WEB_CLIENT_ID)
            )
            val idToken = GoogleIdTokenCredential
                .createFrom(response.credential.data).idToken
            if (idToken.isBlank()) {
                snackbar.showSnackbar("Token vacío, intenta de nuevo"); return@launch
            }
            vm.finishGoogleSignUp(idToken)
        } catch (e: Exception) {
            snackbar.showSnackbar("Google cancelado o falló: ${e.message}")
        }
    }

    /* ------------- DatePicker ------------- */
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun openDatePicker() {
        val c = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, y, m, d -> birthDate = LocalDate.of(y, m + 1, d) },
            c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]
        ).show()
    }

    /* ------------- ModalInfoDialog ------------- */
    val isLoading = uiState is SignUpUiState.Loading
    val isSuccess = uiState is SignUpUiState.Success
    val isError   = uiState is SignUpUiState.Error
    val dialogVisible = isLoading || isSuccess || isError

    ModalInfoDialog(
        visible = dialogVisible,

        /* LOADING */
        loading = isLoading,

        /* SUCCESS || ERROR */
        icon    = when {
            isSuccess -> Icons.Default.Check
            isError   -> Icons.Default.Error
            else      -> null
        },
        iconColor = when {
            isSuccess -> MaterialTheme.colorScheme.onPrimaryContainer
            isError   -> MaterialTheme.colorScheme.error
            else      -> MaterialTheme.colorScheme.onPrimaryContainer
        },
        iconBgColor = when {
            isSuccess -> MaterialTheme.colorScheme.primaryContainer
            isError   -> MaterialTheme.colorScheme.errorContainer
            else      -> MaterialTheme.colorScheme.primaryContainer
        },
        title = when {
            isSuccess -> "Cuenta creada"
            isError   -> "Error"
            else      -> null
        },
        message = when {
            isSuccess -> "Te enviamos un enlace para verificar tu correo."
            isError   -> (uiState as SignUpUiState.Error).message
            else      -> null
        },
        primaryButton = when {
            isSuccess -> DialogButton("Continuar") {
                vm.dismissSuccess()
                nav.navigate(Screen.VerifyEmail.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            }
            isError -> DialogButton("Aceptar") { vm.consumeError() }
            else -> null
        },
        dismissOnBack         = !isLoading,
        dismissOnClickOutside = !isLoading
    )

    /* ------------- errores de campo ------------- */
    val fieldErr = uiState as? SignUpUiState.FieldError

    /* ------------- UI principal ------------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        /* -------- contenido scrollable -------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ImageContainer(
                resId = R.drawable.registro_logo,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 24.dp)
            )

            /* ----- formulario ----- */
            FormContainer {
                InputText(
                    value = username,
                    onValueChange = { username = it; vm.consumeFieldError() },
                    placeholder = "Nombre de usuario*",
                    isError = fieldErr?.userNameError != null,
                    supportingText = fieldErr?.userNameError
                )
                InputDate(
                    value   = birthDate?.format(formatter) ?: "",
                    onClick = { openDatePicker(); vm.consumeFieldError() },
                    isError = fieldErr?.birthDateError != null,
                    supportingText = fieldErr?.birthDateError
                )
                InputEmail(
                    value = email,
                    onValueChange = { email = it; vm.consumeFieldError() },
                    placeholder = "Correo*",
                    isError = fieldErr?.emailError != null,
                    supportingText = fieldErr?.emailError
                )
                InputPassword(
                    value = pass1,
                    onValueChange = { pass1 = it; vm.consumeFieldError() },
                    placeholder = "Contraseña*",
                    isError = fieldErr?.pass1Error != null,
                    supportingText = fieldErr?.pass1Error
                )
                InputPassword(
                    value = pass2,
                    onValueChange = { pass2 = it; vm.consumeFieldError() },
                    placeholder = "Confirmar contraseña*",
                    isError = fieldErr?.pass2Error != null,
                    supportingText = fieldErr?.pass2Error
                )
            }

            Spacer(Modifier.height(32.dp))

            /* ---------- botón Registrar ---------- */
            PrimaryButton(
                text    = stringResource(R.string.btn_sign_up),
                enabled = !isLoading,
                onClick = {
                    vm.signUp(
                        userName  = username,
                        birthDate = birthDate,
                        email     = email,
                        password  = pass1,
                        confirm   = pass2
                    )

                    /* guardar en Credential Manager si luego hay éxito */
                    scope.launch {
                        try {
                            cm.createCredential(
                                activity,
                                CreatePasswordRequest(id = username, password = pass1)
                            )
                        } catch (_: Exception) { /* ignorar fallos locales */ }
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  •  ", fontSize = 22.sp, textAlign = TextAlign.Center)
                Divider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            GoogleSignButton(onClick = ::launchGoogleSignIn)

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta? ",
                    style = MaterialTheme.typography.bodySmall)
                TextButtonLink(
                    text = "Iniciar sesión",
                    onClick = { nav.navigate(Screen.SignIn.route) },
                    underline = false
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Header(
            title          = stringResource(R.string.sign_up_title),
            showBackButton = true,
            onBackClick    = { nav.navigateUp() },
            modifier       = Modifier.align(Alignment.TopCenter)
        )

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}
