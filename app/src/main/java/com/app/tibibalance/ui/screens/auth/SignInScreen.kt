/**
 * @file    SignInScreen.kt
 * @ingroup ui_screens_auth // Grupo para pantallas de autenticación
 * @brief   Define el [Composable] para la pantalla de inicio de sesión de la aplicación.
 *
 * @details Este archivo contiene la implementación de la interfaz de usuario para
 * la pantalla de inicio de sesión. Permite a los usuarios autenticarse mediante
 * correo electrónico y contraseña, o utilizando Google Sign-In (One-Tap).
 *
 * La pantalla gestiona:
 * - Entradas de usuario para email y contraseña ([InputEmail], [InputPassword])
 * dentro de un [FormContainer].
 * - Botones para iniciar sesión ([PrimaryButton]) y continuar con Google ([GoogleSignButton]).
 * - Enlaces para recuperar contraseña ([TextButtonLink] a [Screen.Forgot]) y para
 * registrarse ([TextButtonLink] a [Screen.SignUp]).
 * - Un encabezado ([Header]) con título y botón de retroceso.
 *
 * Utiliza [SignInViewModel] para manejar la lógica de autenticación y el estado de la UI.
 * Los errores globales o de carga se muestran mediante [ModalInfoDialog], mientras que los
 * errores específicos de Google One-Tap (cancelación, token inválido) se muestran en un [Snackbar].
 * Los errores de validación de campos se reflejan directamente en los componentes de entrada.
 *
 * Tras un inicio de sesión exitoso, navega a [Screen.Main] si el correo está verificado,
 * o a [Screen.VerifyEmail] si se requiere verificación.
 *
 * @see SignInViewModel ViewModel que gestiona la lógica y el estado de esta pantalla.
 * @see SignInUiState Estados de la UI para esta pantalla.
 * @see Header Componente reutilizable para la barra superior.
 * @see ImageContainer Componente para mostrar la imagen principal.
 * @see FormContainer Contenedor para los campos de entrada.
 * @see InputEmail Campo de texto para el correo electrónico.
 * @see InputPassword Campo de texto para la contraseña.
 * @see PrimaryButton Botón para la acción principal de inicio de sesión.
 * @see GoogleSignButton Botón para iniciar sesión con Google.
 * @see TextButtonLink Componente para enlaces de texto.
 * @see ModalInfoDialog Diálogo para estados de carga y errores globales.
 * @see androidx.compose.material3.SnackbarHost Para mostrar errores locales de One-Tap.
 * @see androidx.credentials.CredentialManager Para la integración con Google One-Tap.
 * @see com.google.android.libraries.identity.googleid.GoogleIdTokenCredential Para procesar la credencial de Google.
 * @see com.app.tibibalance.ui.navigation.Screen Rutas de navegación.
 */
// ui/screens/auth/SignInScreen.kt
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
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import com.app.tibibalance.ui.components.DialogButton
import com.app.tibibalance.ui.components.buttons.GoogleSignButton
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.inputs.InputEmail
import com.app.tibibalance.ui.components.inputs.InputPassword

/**
 * @brief Constante privada que almacena el Client ID Web para la autenticación con Google.
 * @details Este ID se obtiene de la Google Cloud Console y es necesario para que
 * Google One-Tap y Google Sign-In funcionen correctamente, permitiendo a Firebase
 * verificar el token de ID de Google.
 */
private const val WEB_CLIENT_ID =
    "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com"

/**
 * @brief Composable que define la interfaz de usuario para la pantalla de inicio de sesión.
 *
 * @details Permite al usuario iniciar sesión con correo/contraseña o mediante Google One-Tap.
 * Gestiona el estado de la UI (carga, errores, éxito) a través del [SignInViewModel]
 * y navega a la pantalla correspondiente ([Screen.Main] o [Screen.VerifyEmail])
 * tras un inicio de sesión exitoso.
 *
 * @param nav El [NavController] utilizado para la navegación entre pantallas.
 * @param vm La instancia de [SignInViewModel] (inyectada por Hilt) que maneja la lógica
 * y el estado de esta pantalla.
 */
@Composable
fun SignInScreen(
    nav: NavController,
    vm : SignInViewModel = hiltViewModel()
) {
    /* ---------- estado local de los campos de texto ---------- */
    var email by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }

    /* ---------- estado global de la UI y helpers de Compose ---------- */
    // Observa el estado de la UI emitido por el ViewModel.
    val uiState  by vm.ui.collectAsState()
    // Estado para el Snackbar (solo para errores locales de Google One-Tap).
    val snackbar = remember { SnackbarHostState() }
    // Scope de corrutina para lanzar operaciones asíncronas (e.g., mostrar Snackbar, One-Tap).
    val scope    = rememberCoroutineScope()

    /* ---------- Credential Manager para Google One-Tap ---------- */
    val ctx      = LocalContext.current // Contexto actual.
    val activity = ctx as Activity    // Actividad actual, necesaria para CredentialManager.
    // Instancia de CredentialManager, recordada para la vida del Composable en la Activity.
    val cm       = remember(activity) { CredentialManager.create(activity) }

    /* ───── Navegación reactiva tras éxito de inicio de sesión ───── */
    // Efecto lanzado cuando uiState cambia. Si es Success, navega.
    LaunchedEffect(uiState) {
        (uiState as? SignInUiState.Success)?.let { success ->
            if (success.verified) { // Si el correo está verificado
                // Navega a la pantalla principal, limpiando la pila hasta LaunchScreen.
                nav.navigate(Screen.Main.route) {
                    popUpTo(Screen.Launch.route) { inclusive = true }
                }
            } else { // Si el correo no está verificado
                // Navega a la pantalla de verificación de correo.
                nav.navigate(Screen.VerifyEmail.route) {
                    // Se mantiene LaunchScreen en la pila por si el usuario vuelve atrás.
                    popUpTo(Screen.Launch.route) { inclusive = false }
                }
            }
            // No se llama a vm.consumeError() o similar aquí porque este es un estado de éxito.
            // El ViewModel debería manejar la transición de Success a Idle si es necesario.
        }
    }

    /* ---------- Helper para iniciar Google One-Tap ---------- */
    // Función local para encapsular la lógica de Google Sign-In.
    fun launchGoogleSignIn() = scope.launch {
        // Construye la solicitud de credenciales de Google.
        val request: GetCredentialRequest = vm.buildGoogleRequest(WEB_CLIENT_ID)

        try {
            // Solicita la credencial al CredentialManager (muestra la hoja de One-Tap).
            val response = cm.getCredential(activity, request)
            // Extrae el ID Token de la credencial.
            val idToken  = GoogleIdTokenCredential
                .createFrom(response.credential.data).idToken

            // Valida que el token no esté vacío.
            if (idToken.isBlank()) {
                snackbar.showSnackbar("Token vacío, intenta de nuevo"); return@launch
            }
            // Finaliza el inicio de sesión con Google a través del ViewModel.
            vm.finishGoogleSignIn(idToken)
        } catch (e: Exception) {
            // Maneja excepciones (e.g., usuario cancela One-Tap, error de red).
            snackbar.showSnackbar("Google cancelado o falló: ${e.message}")
        }
    }

    /* ---------- Diálogo modal para Loading y Errores Globales ---------- */
    // Determina si se debe mostrar el diálogo (si está cargando o hay un error global).
    val isLoading  = uiState is SignInUiState.Loading
    val isError    = uiState is SignInUiState.Error
    val showDialog = isLoading || isError

    ModalInfoDialog(
        visible = showDialog,
        loading = isLoading, // Muestra spinner si está cargando.

        icon    = if (isError) Icons.Default.Error else null, // Icono de error para errores.
        iconColor   = MaterialTheme.colorScheme.error,
        iconBgColor = MaterialTheme.colorScheme.errorContainer,
        title   = if (isError) "Error" else null, // Título para errores.
        message = (uiState as? SignInUiState.Error)?.message, // Mensaje del error.

        // Botón "Aceptar" solo si hay un error global, para limpiar el estado.
        primaryButton = if (isError)
            DialogButton("Aceptar") { vm.consumeError() } else null,

        // Controla si el diálogo se puede descartar.
        dismissOnBack         = !isLoading,
        dismissOnClickOutside = !isLoading
    )

    /* ---------- Errores de campo (para InputEmail e InputPassword) ---------- */
    // Extrae los errores de campo del estado de la UI si existen.
    val fieldErr = uiState as? SignInUiState.FieldError

    /* ---------- UI Principal de la pantalla ---------- */
    // Fondo degradado.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor Box que ocupa toda la pantalla y aplica el fondo.
    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Columna principal para el contenido, desplazable verticalmente.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                // Padding para dejar espacio al Header y para los bordes.
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centra elementos horizontalmente.
        )  {

            // Imagen principal de la pantalla.
            ImageContainer(
                resId = R.drawable.ic_login_image,
                contentDescription = null, // Decorativa.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Texto de bienvenida/instrucción.
            Text(
                "¡Ingresa para continuar!",
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Contenedor para los campos del formulario.
            FormContainer {
                InputEmail(
                    value = email,
                    onValueChange = { email = it; vm.consumeError() }, // Limpia errores al escribir.
                    label = "Correo electrónico",
                    isError = fieldErr?.emailError != null, // Muestra estado de error.
                    supportingText = fieldErr?.emailError // Muestra mensaje de error de campo.
                )

                InputPassword(
                    value = pass,
                    onValueChange = { pass = it; vm.consumeError() },
                    label = "Contraseña",
                    isError = fieldErr?.passError != null,
                    supportingText = fieldErr?.passError
                )
            }

            Spacer(Modifier.height(12.dp))

            // Enlace para recuperar contraseña.
            TextButtonLink(
                "¿Olvidaste tu contraseña?",
                onClick = { nav.navigate(Screen.Forgot.route) }
            )

            Spacer(Modifier.height(24.dp))

            // Botón principal de inicio de sesión.
            PrimaryButton(
                text = stringResource(R.string.btn_sign_in),
                enabled = uiState !is SignInUiState.Loading, // Deshabilitado si está cargando.
                onClick = { vm.signIn(email, pass) } // Llama al ViewModel.
            )

            Spacer(Modifier.height(20.dp))
            // Separador visual "•".
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  •  ", fontSize = 22.sp)
                Divider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            // Botón de inicio de sesión con Google.
            GoogleSignButton(onClick = ::launchGoogleSignIn)

            Spacer(Modifier.height(24.dp))
            // Enlace para ir a la pantalla de registro.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Aún no tienes una cuenta? ",
                    style = MaterialTheme.typography.bodySmall)
                TextButtonLink(
                    "Regístrate",
                    onClick = { nav.navigate(Screen.SignUp.route) },
                    underline = false // Sin subrayado.
                )
            }
            Spacer(Modifier.height(24.dp)) // Espacio al final.
        }

        // Header flotante en la parte superior.
        Header(
            title          = "Iniciar Sesión",
            showBackButton = true, // Muestra botón de retroceso.
            onBackClick    = { nav.navigateUp() }, // Acción de retroceso.
            modifier       = Modifier.align(Alignment.TopCenter)
        )

        // Host para el Snackbar, alineado en la parte inferior.
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}