package com.app.tibibalance.ui.screens.auth

import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val WEB_CLIENT_ID = "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com" // ← pon tu client-id web

@Composable
fun SignUpScreen(
    nav: NavController,
    vm: SignUpViewModel = hiltViewModel()
) {
    /* -------- Form-fields -------- */
    var username  by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var email     by remember { mutableStateOf("") }
    var pass1     by remember { mutableStateOf("") }
    var pass2     by remember { mutableStateOf("") }

    /* -------- State & helpers -------- */
    val uiState  by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope    = rememberCoroutineScope()

    /* -------- Credential Manager -------- */
    val ctx       = LocalContext.current
    val activity  = ctx as Activity
    val cm        = remember(activity) { CredentialManager.create(activity) }

    /* Snackbar para errores */
    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Error) {
            snackbar.showSnackbar((uiState as SignUpUiState.Error).message)
            vm.consumeError()
        }
    }

    /* ───── Navegar tras crear la cuenta (e-mail enviado) ───── */
    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) {
            // (opcional) deja visible el modal 1200 ms
            kotlinx.coroutines.delay(1_200)

            nav.navigate(Screen.VerifyEmail.route) {
                // Mantén en back-stack Launch para que el botón “Cerrar sesión”
                // pueda regresar allí; evita múltiples copias de SignUp.
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }

            vm.dismissSuccess()   // limpia el estado para futuros reintentos
        }
    }


    /* -------- Date-picker nativo -------- */
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, y, m, d -> birthDate = LocalDate.of(y, m + 1, d) },
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        ).show()
    }

    fun launchGoogleSignIn() = scope.launch {
        val request = vm.buildGoogleRequest(WEB_CLIENT_ID)

        try {
            val response   = cm.getCredential(activity, request)
            val idToken    = GoogleIdTokenCredential
                .createFrom(response.credential.data).idToken
            if (idToken.isEmpty()) {
                snackbar.showSnackbar("Token vacío, inténtalo de nuevo"); return@launch
            }
            vm.finishGoogleSignUp(idToken)
        } catch (ex: Exception) {
            snackbar.showSnackbar("No se pudo iniciar sesión: ${ex.message}")
        }
    }



    /* -------- UI -------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        ModalInfoDialog(
            loading  = uiState is SignUpUiState.Loading,
            message  = null,              // no mostramos éxito aquí
            onAccept = {}                 // nunca se llama (no hay mensaje)
        )

        /* Contenido scrollable */
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

            FormContainer {
                InputText(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Nombre de usuario*"
                )
                InputDate(
                    value  = birthDate?.format(formatter) ?: "",
                    onClick = ::openDatePicker
                )
                InputEmail(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo*"
                )
                InputPassword(
                    value = pass1,
                    onValueChange = { pass1 = it },
                    placeholder = "Contraseña*"
                )
                InputPassword(
                    value = pass2,
                    onValueChange = { pass2 = it },
                    placeholder = "Confirmar contraseña*"
                )
            }

            Spacer(Modifier.height(32.dp))

            /* -------- Registro tradicional -------- */
            PrimaryButton(
                text    = stringResource(R.string.btn_sign_up),
                enabled = uiState !is SignUpUiState.Loading,
                onClick = {
                    vm.signUp(
                        userName  = username,
                        birthDate = birthDate,
                        email     = email,
                        password  = pass1,
                        confirm   = pass2
                    )

                    /* Guardar password en el gestor (solo si el backend respondió OK) */
                    scope.launch {
                        try {
                            cm.createCredential(
                                activity,
                                CreatePasswordRequest(id = username, password = pass1)
                            )
                        } catch (_: Exception) { /* usuario rechazó o no soportado */ }
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

            /* -------- Registro / inicio con Google -------- */
            GoogleSignButton(onClick = ::launchGoogleSignIn)

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "¿Ya tienes cuenta? ",
                    style = MaterialTheme.typography.bodySmall
                )
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
