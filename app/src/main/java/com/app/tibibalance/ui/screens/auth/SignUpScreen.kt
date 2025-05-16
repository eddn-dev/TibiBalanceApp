/**
 * @file    SignUpScreen.kt
 * @ingroup ui_screens_auth // Grupo para pantallas de autenticación
 * @brief   Define el [Composable] para la pantalla de registro de nuevos usuarios.
 *
 * @details Este archivo implementa la interfaz de usuario para el proceso de creación de cuentas.
 * Los usuarios pueden registrarse proporcionando su nombre de usuario, fecha de nacimiento,
 * correo electrónico y una contraseña (que debe ser confirmada). Adicionalmente,
 * se ofrece la opción de registro/inicio de sesión mediante Google One-Tap.
 *
 * La pantalla interactúa con [SignUpViewModel] para:
 * - Validar los datos del formulario localmente.
 * - Gestionar el proceso de registro con correo/contraseña.
 * - Manejar el flujo de registro/inicio de sesión con Google.
 * - Actualizar la UI según el estado emitido por el ViewModel ([SignUpUiState]).
 *
 * El feedback al usuario se proporciona de la siguiente manera:
 * - **Errores de campo:** Se muestran directamente en los componentes de entrada ([InputText], [InputDate], etc.)
 * mediante sus parámetros `isError` y `supportingText`.
 * - **Errores globales de formulario o carga:** Se utiliza un [ModalInfoDialog] para indicar estados
 * de carga (`Loading`), éxito en el registro por formulario (`Success`), o errores generales (`Error`).
 * - **Errores locales de Google One-Tap:** Se muestran en un [Snackbar] (e.g., si el usuario cancela la hoja de Google).
 * - **Navegación:**
 * - Tras un registro exitoso con correo/contraseña ([SignUpUiState.Success]), navega a [Screen.VerifyEmail].
 * - Tras un registro/inicio de sesión exitoso con Google ([SignUpUiState.GoogleSuccess]), navega directamente a [Screen.Main].
 *
 * Componentes de UI reutilizables empleados:
 * - [Header]: Barra superior con título y botón de retroceso.
 * - [ImageContainer]: Para mostrar la imagen principal de la pantalla.
 * - [FormContainer]: Para agrupar visualmente los campos del formulario.
 * - [InputText], [InputDate], [InputEmail], [InputPassword]: Campos de entrada específicos.
 * - [PrimaryButton]: Para la acción principal de "Registrarse".
 * - [GoogleSignButton]: Para la opción de "Continuar con Google".
 * - [TextButtonLink]: Para el enlace "Iniciar sesión" si el usuario ya tiene cuenta.
 * - [ModalInfoDialog]: Para mostrar diálogos de carga, éxito o error global.
 * - [SnackbarHost]: Para mensajes de error de Google One-Tap.
 *
 * @see SignUpViewModel ViewModel que gestiona la lógica y el estado de esta pantalla.
 * @see SignUpUiState Define los diferentes estados por los que puede pasar la UI.
 * @see Screen Define las rutas de navegación de la aplicación.
 * @see CredentialManager Utilizado para interactuar con Google One-Tap.
 * @see DatePickerDialog Utilizado para la selección de fecha de nacimiento.
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
import com.app.tibibalance.ui.components.buttons.GoogleSignButton
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.inputs.InputDate
import com.app.tibibalance.ui.components.inputs.InputEmail
import com.app.tibibalance.ui.components.inputs.InputPassword
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.navigation.Screen
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @brief ID de cliente web de OAuth 2.0 para la integración con Google Sign-In.
 * @details Este ID se obtiene desde Google Cloud Console y es esencial para que
 * Google Identity Services (GIS) funcione correctamente y pueda generar un ID Token
 * válido para la autenticación con Firebase.
 */
private const val WEB_CLIENT_ID =
    "467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com"

/**
 * @brief Composable principal para la pantalla de registro de usuarios.
 *
 * @details Esta función define la interfaz de usuario completa para el proceso de registro.
 * Gestiona el estado de los campos de entrada, interactúa con [SignUpViewModel] para
 * la lógica de negocio y la validación, y maneja la navegación basada en el resultado
 * de las operaciones de registro.
 *
 * @param nav El [NavController] utilizado para la navegación entre pantallas.
 * @param vm La instancia de [SignUpViewModel] (inyectada por Hilt) que proporciona
 * la lógica de negocio y el estado de la UI.
 */
@Composable
fun SignUpScreen(
    nav: NavController,
    vm : SignUpViewModel = hiltViewModel()
) {
    /* -------- estado local de inputs -------- */
    // Estados para cada campo del formulario, recordados a través de recomposiciones.
    var username  by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) } // Nullable para fecha no seleccionada
    var email     by remember { mutableStateOf("") }
    var pass1     by remember { mutableStateOf("") }
    var pass2     by remember { mutableStateOf("") }

    /* -------- estado global y helpers de Compose -------- */
    // Observa el estado de la UI desde el ViewModel.
    val uiState  by vm.ui.collectAsState()
    // Estado para el Snackbar, usado para errores locales de Google One-Tap.
    val snackbar = remember { SnackbarHostState() }
    // Scope de corrutina para operaciones asíncronas iniciadas desde la UI (e.g., Google Sign-In).
    val scope    = rememberCoroutineScope()

    /* -------- Android helpers -------- */
    val ctx      = LocalContext.current // Contexto actual de la aplicación.
    val activity = ctx as Activity    // Actividad contenedora, necesaria para CredentialManager.
    // CredentialManager para interactuar con Google One-Tap y Password Manager.
    val cm       = remember(activity) { CredentialManager.create(activity) }

    /* -------- Helper para lanzar Google One-Tap -------- */
    /**
     * @brief Inicia el flujo de Google One-Tap para registro o inicio de sesión.
     * @details Utiliza [CredentialManager] para solicitar una credencial de Google.
     * Si tiene éxito, extrae el ID Token y lo pasa al ViewModel para finalizar
     * la autenticación con Firebase. Los errores se reportan al ViewModel
     * para ser mostrados en el Snackbar.
     */
    fun launchGoogleSignIn() = scope.launch {
        try {
            // 1. Mostrar el selector de cuentas (One-Tap) o la solicitud de permiso.
            //    Utiliza el WEB_CLIENT_ID para asegurar que se genere un ID Token para el backend de Firebase.
            val result  = cm.getCredential(activity, vm.buildGoogleRequest(WEB_CLIENT_ID))
            // 2. Extraer el ID Token de la credencial de Google.
            val idToken = GoogleIdTokenCredential
                .createFrom(result.credential.data)
                .idToken

            // 3. Validaciones básicas del token. Si es nulo o vacío, reportar error y salir.
            if (idToken.isNullOrBlank()) {
                vm.reportGoogleError("Token vacío, intenta de nuevo") // Informa al ViewModel.
                return@launch
            }

            // 4. Finalizar el registro/inicio de sesión con el ViewModel usando el ID Token.
            vm.finishGoogleSignUp(idToken)

        } catch (e: Exception) {
            // 5. Capturar cualquier excepción (e.g., usuario cancela, error de red, credencial no disponible)
            //    e informar al ViewModel.
            vm.reportGoogleError("Google cancelado o falló: ${e.message}")
        }
    }


    /* -------- Efecto para navegación según estado de la UI -------- */
    // Se ejecuta cuando `uiState` cambia.
    // Detectar cambios en el estado de la UI
    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is SignUpUiState.VerificationEmailSent -> {
                // Navega automáticamente a la pantalla de verificación de correo
                nav.navigate(Screen.VerifyEmail.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
                vm.dismissSuccess() // Limpia el estado
            }
            is SignUpUiState.Error -> {
                scope.launch {
                    snackbar.showSnackbar(currentState.message)
                }
            }
            else -> Unit // No hacer nada para otros estados
        }
    }

    /* -------- Flags para controlar la visibilidad y contenido del ModalInfoDialog -------- */
    val isLoading = uiState is SignUpUiState.Loading     // True si está cargando.
    val isSuccess = uiState is SignUpUiState.Success     // True si registro por formulario fue exitoso.
    val isError   = uiState is SignUpUiState.Error       // True si hay un error global.
    // El diálogo se muestra si está cargando, si hubo éxito por formulario, o si hay error global.
    val dialogVisible = isLoading || isSuccess || isError

    /* -------- Configuración y lógica para el DatePickerDialog -------- */
    // Formateador para mostrar la fecha en formato dd/MM/yyyy.
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    /**
     * @brief Abre el [DatePickerDialog] estándar de Android para la selección de fecha de nacimiento.
     * @details Al confirmar, actualiza el estado `birthDate` y notifica al ViewModel para
     * limpiar cualquier error de campo previo relacionado con la fecha.
     */
    fun openDatePicker() {
        val c = Calendar.getInstance() // Calendario para obtener el año, mes y día actuales.
        DatePickerDialog(
            ctx, // Contexto actual.
            { _, year, month, day -> // Callback cuando se selecciona una fecha.
                birthDate = LocalDate.of(year, month + 1, day) // Actualiza el estado local.
            },
            c[Calendar.YEAR], // Año inicial.
            c[Calendar.MONTH], // Mes inicial.
            c[Calendar.DAY_OF_MONTH] // Día inicial.
        ).show() // Muestra el diálogo.
    }

    /* -------- Diálogo modal para feedback (Loading, Success de formulario, Error global) -------- */
    ModalInfoDialog(
        visible = dialogVisible, // Controla la visibilidad.
        loading = isLoading,     // Muestra spinner si está cargando.
        icon    = when {         // Icono según el estado.
            isSuccess -> Icons.Default.Check
            isError   -> Icons.Default.Error
            else      -> null
        },
        iconColor = when {      // Color del icono.
            isSuccess -> MaterialTheme.colorScheme.onPrimaryContainer
            isError   -> MaterialTheme.colorScheme.error
            else      -> MaterialTheme.colorScheme.onPrimaryContainer
        },
        iconBgColor = when {    // Color de fondo del icono.
            isSuccess -> MaterialTheme.colorScheme.primaryContainer
            isError   -> MaterialTheme.colorScheme.errorContainer
            else      -> MaterialTheme.colorScheme.primaryContainer
        },
        title = when {          // Título del diálogo.
            isSuccess -> "Cuenta creada"
            isError   -> "Error"
            else      -> null
        },
        message = when {        // Mensaje principal del diálogo.
            isSuccess -> "Te enviamos un enlace para verificar tu correo."
            isError   -> (uiState as SignUpUiState.Error).message
            else      -> null
        },
        primaryButton = when {  // Botón primario.
            isSuccess -> DialogButton("Continuar") { // Para éxito de formulario:
                vm.dismissSuccess() // Limpia estado en VM.
                nav.navigate(Screen.VerifyEmail.route) { // Navega a verificar email.
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            }
            isError -> DialogButton("Aceptar") { vm.consumeError() } // Para error global: limpia estado.
            else -> null
        },
        // Permite descartar el diálogo solo si no está cargando.
        dismissOnBack         = !isLoading,
        dismissOnClickOutside = !isLoading
    )

    /* -------- Obtener errores de campo específicos desde el uiState -------- */
    val fieldErr = uiState as? SignUpUiState.FieldError

    /* -------- UI principal de la pantalla de Registro -------- */
    // Degradado de fondo.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient) // Aplica el fondo degradado.
    ) {
        /* Contenido desplazable verticalmente */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite scroll si el contenido excede la altura.
                // Padding para el Header y los bordes de la pantalla.
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido de la columna.
        ) {

            // Imagen del logo o ilustración de registro.
            ImageContainer(
                resId = R.drawable.registro_logo, // ID del recurso drawable.
                contentDescription = null, // Decorativa.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(bottom = 24.dp)
            )

            /* Contenedor del formulario de registro */
            FormContainer {
                InputText(
                    value = username,
                    onValueChange = { username = it; vm.consumeFieldError() }, // Actualiza estado y limpia errores.
                    placeholder = "Nombre de usuario*", // Placeholder/label.
                    isError = fieldErr?.userNameError != null, // Estado de error del campo.
                    supportingText = fieldErr?.userNameError // Mensaje de error del campo.
                )

                InputDate(
                    value = birthDate?.format(formatter) ?: "", // Muestra fecha formateada o vacío.
                    onClick = { openDatePicker(); vm.consumeFieldError() }, // Abre DatePicker y limpia errores.
                    isError = fieldErr?.birthDateError != null,
                    supportingText = fieldErr?.birthDateError
                )

                InputEmail(
                    value = email,
                    onValueChange = { email = it; vm.consumeFieldError() },
                    label = "Correo*", // Usa 'label' en lugar de 'placeholder' para estos campos.
                    isError = fieldErr?.emailError != null,
                    supportingText = fieldErr?.emailError
                )

                InputPassword(
                    value = pass1,
                    onValueChange = { pass1 = it; vm.consumeFieldError() },
                    label = "Contraseña*",
                    isError = fieldErr?.pass1Error != null,
                    supportingText = fieldErr?.pass1Error
                )

                InputPassword(
                    value = pass2,
                    onValueChange = { pass2 = it; vm.consumeFieldError() },
                    label = "Confirmar contraseña*",
                    isError = fieldErr?.pass2Error != null,
                    supportingText = fieldErr?.pass2Error
                )
            }

            Spacer(Modifier.height(32.dp))

            /* Botón principal de "Registrarse" */
            PrimaryButton(
                text = stringResource(R.string.btn_sign_up), // Texto del botón.
                enabled = !isLoading, // Deshabilitado si está cargando.
                onClick = { // Acción al pulsar:
                    vm.signUpAndSendEmail(
                        userName = username,
                        birthDate = birthDate,
                        email = email,
                        password = pass1,
                        confirm = pass2,
                        onSuccess = {
                            nav.navigate(Screen.VerifyEmail.route)
                        },
                        onError = { error ->
                            scope.launch {
                                snackbar.showSnackbar(error)
                            }
                        }
                    )
                }
            )

            Spacer(Modifier.height(24.dp))
            // Separador visual "o".
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text("  •  ", fontSize = 22.sp, textAlign = TextAlign.Center)
                Divider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            // Botón para registrarse/iniciar sesión con Google.
            GoogleSignButton(onClick = ::launchGoogleSignIn)

            Spacer(Modifier.height(16.dp))
            // Enlace para ir a la pantalla de inicio de sesión si ya se tiene cuenta.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta? ",
                    style = MaterialTheme.typography.bodySmall)
                TextButtonLink(
                    text = "Iniciar sesión",
                    onClick = { nav.navigate(Screen.SignIn.route) }, // Navega a SignIn.
                    underline = false // Sin subrayado.
                )
            }
            Spacer(Modifier.height(24.dp)) // Espacio al final del contenido scrollable.
        }

        // Header fijo en la parte superior de la pantalla.
        Header(
            title          = stringResource(R.string.sign_up_title), // Título de la pantalla.
            showBackButton = true, // Muestra el botón de retroceso.
            onBackClick    = { nav.navigateUp() }, // Acción para el botón de retroceso.
            modifier       = Modifier.align(Alignment.TopCenter) // Alinea el Header.
        )

        // Host para el Snackbar, alineado en la parte inferior.
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}