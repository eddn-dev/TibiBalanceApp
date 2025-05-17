// ui/navigation/AppNavGraph.kt
package com.app.tibibalance.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController // Usar NavHostController específicamente
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.tibibalance.R
import com.app.tibibalance.data.preferences.OnboardingPreferences
import com.app.tibibalance.ui.navigation.Screen
import com.app.tibibalance.ui.screens.onboarding.OnboardingPage
import com.app.tibibalance.ui.screens.onboarding.OnboardingScreen
import com.app.tibibalance.ui.screens.auth.ForgotPasswordScreen
import com.app.tibibalance.ui.screens.auth.SignInScreen
import com.app.tibibalance.ui.screens.auth.SignUpScreen
import com.app.tibibalance.ui.screens.auth.VerifyEmailScreen
import com.app.tibibalance.ui.screens.habits.ConfigureNotificationScreen
import com.app.tibibalance.ui.screens.launch.LaunchScreen
import com.app.tibibalance.ui.screens.main.MainScreen
import com.app.tibibalance.ui.screens.profile.EditProfileScreen
import com.app.tibibalance.ui.screens.settings.ChangePasswordScreenPreviewOnly
import com.app.tibibalance.ui.screens.settings.DeleteAccountScreen
import kotlinx.coroutines.launch

/**
 * @brief Configura y muestra el [NavHost] principal que gestiona la navegación entre las diferentes pantallas de la aplicación.
 *
 * @details
 * - Utiliza [rememberNavController] para obtener o crear el controlador de navegación si no se proporciona uno externamente.
 * - Añade un flujo de onboarding basado en Lottie + Pager Compose, que se muestra sólo la primera vez.
 * - Define, mediante la función `composable`, la asociación entre cada ruta definida en [Screen]
 *   (e.g., `Screen.Launch.route`) y el [Composable] de pantalla correspondiente (e.g., `LaunchScreen`).
 * - La pantalla inicial del grafo se calcula dinámicamente según la preferencia almacenada
 *   que indica si el usuario ya vio el onboarding.
 *
 * @param navController El [NavHostController] que gestionará la navegación dentro de este grafo.
 *                      Si no se proporciona uno, se creará y recordará uno automáticamente usando [rememberNavController].
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // 1) Obtiene el contexto y la instancia de preferencias de onboarding
    val context = LocalContext.current
    val prefs = remember { OnboardingPreferences(context) }

    // 2) Observa si el usuario ya completó el onboarding (default = false)
    val hasSeenOnboarding by prefs.hasSeenOnboarding.collectAsState(initial = false)

    // 3) Decide la pantalla inicial: Onboarding o Launch según la preferencia
    val startDestination = if (hasSeenOnboarding) {
        Screen.Launch.route
    } else {
        Screen.Onboarding.route
    }

    // 4) Define el NavHost con todas las rutas
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- Onboarding ---
        /**
         * Ruta "onboarding": flujo de pantallas de introducción con animaciones Lottie y Pager Compose.
         * Al completarse, marca la preferencia y navega a LaunchScreen limpiando el backstack.
         */
        composable(Screen.Onboarding.route) {
            // a) Define las páginas de onboarding
            val pages = listOf(
                OnboardingPage(
                    titleRes     = R.string.onb_title_1,
                    descRes      = R.string.onb_desc_1,
                    lottieRawRes = R.raw.anim_health
                ),
                OnboardingPage(
                    titleRes     = R.string.onb_title_2,
                    descRes      = R.string.onb_desc_2,
                    lottieRawRes = R.raw.anim_habit
                ),
                OnboardingPage(
                    titleRes     = R.string.onb_title_3,
                    descRes      = R.string.onb_desc_3,
                    lottieRawRes = R.raw.anim_stats
                )
            )

            // b) Bandera para disparar el efecto de persistencia y navegación
            var onboardingFinished by remember { mutableStateOf(false) }

            // c) Lanza la UI de Onboarding; al completar, sube la bandera
            OnboardingScreen(
                pages = pages,
                onComplete = { onboardingFinished = true }
            )

            // d) Una vez completado (bandera = true), guarda la preferencia y navega
            if (onboardingFinished) {
                LaunchedEffect(Unit) {
                    prefs.setSeenOnboarding(true)
                    navController.navigate(Screen.Launch.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }
        }

        // --- Launch / SplashScreen ---
        /**
         * Ruta "launch": pantalla de entrada que puede redirigir a SignIn o al flujo principal.
         */
        composable(Screen.Launch.route) {
            LaunchScreen(navController)
        }

        // --- Autenticación ---
        composable(Screen.SignIn.route)      { SignInScreen(navController) }
        composable(Screen.SignUp.route)      { SignUpScreen(navController) }
        composable(Screen.VerifyEmail.route) { VerifyEmailScreen(navController) }
        composable(Screen.Forgot.route)      { ForgotPasswordScreen(navController) }

        // --- Pantalla principal (post-login) ---
        /**
         * Ruta "main": contiene el flujo principal de la app tras autenticarse.
         */
        composable(Screen.Main.route) {
            MainScreen(rootNav = navController)
        }

        // --- Otras pantallas de configuración y perfil ---
        composable(Screen.NotificationSettings.route) {
            ConfigureNotificationScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable(Screen.EditPersonal.route) {
            EditProfileScreen(navController)
        }
        composable("changePassword") {
            ChangePasswordScreenPreviewOnly(navController = navController)
        }
        composable("delete_account/{isGoogleUser}") { backStackEntry ->
            val isGoogleUserArg = backStackEntry
                .arguments
                ?.getString("isGoogleUser")
                ?.toBooleanStrictOrNull()
                ?: false

            DeleteAccountScreen(
                navController = navController,
                isGoogleUser   = isGoogleUserArg
            )
        }
    }
}

// Nota: Generalmente no se crea una @Preview para el AppNavGraph completo, ya que
// su propósito es definir la estructura de navegación y no una UI visual específica
// en sí misma. Las previews se crean para las pantallas individuales (LaunchScreen, etc.).
