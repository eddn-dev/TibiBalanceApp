/**
 * @file    LaunchScreen.kt
 * @ingroup ui_screens_launch // Grupo específico para la pantalla de inicio/lanzamiento
 * @brief   Composable para la pantalla inicial de la aplicación, que actúa como punto de entrada y decisión de navegación.
 *
 * @details
 * Esta pantalla se muestra al iniciar la aplicación. Su principal responsabilidad es observar
 * el estado de la sesión del usuario ([SessionState] del [LaunchViewModel]) y redirigir
 * automáticamente al flujo adecuado:
 * - **Usuario no logueado:** Permanece en esta pantalla, mostrando opciones para
 * "Iniciar sesión" ([Screen.SignIn]) o "Registrarse" ([Screen.SignUp]).
 * - **Usuario logueado y verificado:** Navega a la pantalla principal de la app ([Screen.Main]),
 * eliminando el flujo de autenticación de la pila trasera.
 * - **Usuario logueado pero no verificado:** Navega a la pantalla de verificación de correo
 * ([Screen.VerifyEmail]), eliminando esta pantalla de Launch de la pila trasera.
 *
 * La UI presenta:
 * - Un fondo degradado.
 * - Una imagen representativa de la aplicación ([ImageContainer]).
 * - Una animación Lottie ([LottieAnimation]) para atractivo visual.
 * - Un subtítulo o lema ([Subtitle]).
 * - Botones de acción principal ([PrimaryButton]) para "Iniciar sesión" y "Registrarse".
 *
 * @see LaunchViewModel ViewModel que provee el estado de sesión ([SessionState]).
 * @see SessionState Data class que encapsula el estado de autenticación y verificación.
 * @see Screen Sealed class que define las rutas de navegación.
 * @see ImageContainer, Subtitle, PrimaryButton Componentes de UI reutilizados.
 * @see LottieAnimation Componente para mostrar animaciones Lottie.
 * @see NavController Controlador de navegación para realizar las redirecciones.
 * @see LaunchedEffect Hook de Compose para reaccionar a cambios en el estado de sesión.
 */
// ui/screens/launch/LaunchScreen.kt
package com.app.tibibalance.ui.screens.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation // Import para Lottie
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.navigation.Screen
// Import no usado directamente aquí, pero relevante para el contexto
// import com.app.tibibalance.ui.screens.emotional.EmotionalCalentarScreen

/**
 * @brief Composable principal para la pantalla de lanzamiento (Launch).
 *
 * @details Esta pantalla actúa como el punto de entrada inicial. Observa el estado de la sesión
 * y redirige al usuario al flujo apropiado (Login/SignUp, Main, o VerifyEmail).
 * Muestra el logo/animación de la app y los botones para iniciar sesión o registrarse
 * si el usuario no está autenticado.
 *
 * @param nav El [NavController] utilizado para la navegación entre pantallas.
 * @param vm La instancia de [LaunchViewModel] (inyectada por Hilt) que proporciona
 * el estado de la sesión.
 */
@Composable
fun LaunchScreen(
    nav: NavController,
    vm : LaunchViewModel = hiltViewModel()
) {
    // Observa el estado de la sesión (logueado, verificado) desde el ViewModel.
    val session by vm.sessionState.collectAsState()

    /* ── Navegación reactiva basada en el estado de sesión ─────────────────────────── */
    // Este LaunchedEffect se ejecuta cuando el estado 'session' cambia.
    LaunchedEffect(session) {
        when {
            // Si no está logueado, no hace nada, permanece en LaunchScreen.
            !session.loggedIn               -> Unit
            // Si está logueado Y verificado, navega a MainScreen.
            session.verified                -> nav.navigate(Screen.Main.route) {
                // Limpia la pila de navegación hasta LaunchScreen (inclusive)
                // para que el usuario no pueda volver a Launch al presionar "Atrás" desde Main.
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
            // Si está logueado PERO NO verificado, navega a VerifyEmailScreen.
            else /* logged && !verified */  -> nav.navigate(Screen.VerifyEmail.route) {
                // Limpia la pila hasta LaunchScreen (inclusive).
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
        }
    }

    // Define un fondo degradado para la pantalla.
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = .15f), // Color primario con transparencia
            MaterialTheme.colorScheme.background // Color de fondo del tema
        )
    )

    // Scaffold proporciona estructura básica de Material Design (aquí solo se usa para padding).
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient), // Aplica el fondo degradado.
        containerColor = Color.Transparent // Hace el fondo del Scaffold transparente para ver el degradado.
    ) { innerPadding -> // Padding proporcionado por Scaffold (para barras de sistema, etc.)
        // Columna principal que organiza el contenido verticalmente.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplica el padding del Scaffold.
                .padding(horizontal = 20.dp), // Padding horizontal adicional.
            horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos horizontalmente.
            // Distribuye el espacio vertical uniformemente entre los elementos.
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // Contenedor para la imagen estática de lanzamiento.
            ImageContainer(
                resId = R.drawable.launch, // ID del recurso drawable.
                contentDescription = null, // Imagen decorativa.
                modifier = Modifier
                    .height(200.dp) // Altura fija para la imagen.
            )

            // Carga la composición de la animación Lottie desde recursos raw.
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    resId = R.raw.tibianimation // ID del archivo JSON de Lottie.
                )
            )
            // Muestra la animación Lottie.
            LottieAnimation(
                composition = composition, // La composición cargada.
                iterations = LottieConstants.IterateForever, // Repite la animación indefinidamente.
                modifier = Modifier
                    .size(150.dp) // Tamaño de la animación.
            )

            // Subtítulo o lema de la aplicación.
            Subtitle(
                text = stringResource(R.string.launch_subtitle), // Texto desde strings.xml.
                textAlign = TextAlign.Center, // Texto centrado.
                modifier = Modifier.padding(horizontal = 10.dp) // Padding horizontal para el texto.
            )

            /* Contenedor para los botones de acción */
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Botón para ir a la pantalla de inicio de sesión.
                PrimaryButton(
                    text = stringResource(R.string.btn_sign_in), // Texto desde strings.xml.
                    onClick = { nav.navigate(Screen.SignIn.route) } // Navega a SignIn.
                )
                Spacer(Modifier.height(24.dp)) // Espacio entre botones.
                // Botón para ir a la pantalla de registro.
                PrimaryButton(
                    text = stringResource(R.string.btn_sign_up), // Texto desde strings.xml.
                    onClick = { nav.navigate(Screen.SignUp.route) } // Navega a SignUp.
                )
            }
        } // Fin Column principal
    } // Fin Scaffold
} // Fin LaunchScreen