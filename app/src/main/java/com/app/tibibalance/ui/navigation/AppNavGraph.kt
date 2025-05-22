// src/main/java/com/app/tibibalance/ui/navigation/AppNavGraph.kt
package com.app.tibibalance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.tibibalance.ui.screens.auth.ForgotPasswordScreen
import com.app.tibibalance.ui.screens.auth.SignInScreen
import com.app.tibibalance.ui.screens.auth.SignUpScreen
import com.app.tibibalance.ui.screens.auth.VerifyEmailScreen
import com.app.tibibalance.ui.screens.habits.ConfigureNotificationScreen
import com.app.tibibalance.ui.screens.launch.LaunchScreen
import com.app.tibibalance.ui.screens.profile.EditProfileScreen
import com.app.tibibalance.ui.screens.main.MainScreen
import com.app.tibibalance.ui.screens.settings.ChangePasswordScreenPreviewOnly
import com.app.tibibalance.ui.screens.settings.DeleteAccountScreen
import com.app.tibibalance.ui.screens.conection.ConnectedDeviceRoute

/**
 * @file AppNavGraph.kt
 * @brief Define el grafo de navegación principal de la aplicación TibiBalance.
 *
 * @details
 * - Utiliza NavHost para gestionar las rutas declaradas en Screen.
 * - Cada ruta se asocia a su Composable correspondiente.
 * - La ruta inicial es Screen.Launch.
 * - Se incluye la pantalla de gestión de dispositivos Wear OS bajo Screen.ManageDevices.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route
    ) {
        // Pantalla de lanzamiento / splash
        composable(Screen.Launch.route) {
            LaunchScreen(navController)
        }

        // Autenticación
        composable(Screen.SignIn.route) {
            SignInScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(navController)
        }
        composable(Screen.Forgot.route) {
            ForgotPasswordScreen(navController)
        }

        // Pantalla principal de la app (post-login)
        composable(Screen.Main.route) {
            MainScreen(rootNav = navController)
        }

        // Administración de dispositivos Wear OS / conexión
        composable(Screen.ManageDevices.route) {
            ConnectedDeviceRoute()  // Se elimina el parámetro navController
        }

        // Configuración de notificaciones
        composable(Screen.NotificationSettings.route) {
            ConfigureNotificationScreen(
                onNavigateUp = { navController.popBackStack() }
            )
        }

        // Editar perfil personal
        composable(Screen.EditPersonal.route) {
            EditProfileScreen(navController)
        }

        // Cambio de contraseña (vista previa)
        composable("changePassword") {
            ChangePasswordScreenPreviewOnly(navController)
        }

        // Eliminar cuenta
        composable("delete_account/{isGoogleUser}") { backStackEntry ->
            val isGoogleUser = backStackEntry.arguments
                ?.getString("isGoogleUser")
                ?.toBooleanStrictOrNull() ?: false

            DeleteAccountScreen(
                navController = navController,
                isGoogleUser = isGoogleUser
            )
        }
    }
}

// Nota: Generalmente no se crea una @Preview para el AppNavGraph completo, ya que
// su propósito es definir la estructura de navegación y no una UI visual específica
// en sí misma. Las previews se crean para las pantallas individuales (LaunchScreen, etc.).