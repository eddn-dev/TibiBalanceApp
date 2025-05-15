// ui/navigation/AppNavGraph.kt
package com.app.tibibalance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController // Usar NavHostController específicamente
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Importar todas las pantallas referenciadas
import com.app.tibibalance.ui.screens.auth.ForgotPasswordScreen
import com.app.tibibalance.ui.screens.auth.SignInScreen
import com.app.tibibalance.ui.screens.auth.SignUpScreen
import com.app.tibibalance.ui.screens.auth.VerifyEmailScreen
import com.app.tibibalance.ui.screens.habits.ConfigureNotificationScreen
import com.app.tibibalance.ui.screens.launch.LaunchScreen
import com.app.tibibalance.ui.screens.profile.EditProfileScreen
import com.app.tibibalance.ui.screens.main.MainScreen
import com.app.tibibalance.ui.screens.settings.ChangePasswordScreenPreviewOnly

/**
 * @brief Configura y muestra el [NavHost] principal que gestiona la navegación entre las diferentes pantallas de la aplicación.
 *
 * @details Utiliza [rememberNavController] para obtener o crear el controlador de navegación si no se proporciona uno externamente.
 * Define, mediante la función `composable`, la asociación entre cada ruta definida en [Screen]
 * (e.g., `Screen.Launch.route`) y el [Composable] de pantalla correspondiente (e.g., `LaunchScreen`).
 * La pantalla inicial del grafo se establece en [Screen.Launch]. El `navController` se pasa
 * a cada pantalla hija para permitirles iniciar acciones de navegación.
 *
 * @param navController El [NavHostController] que gestionará la navegación dentro de este grafo.
 * Si no se proporciona uno, se creará y recordará uno automáticamente usando [rememberNavController].
 */
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    // NavHost es el contenedor que intercambia los Composables según la ruta actual.
    NavHost(
        navController = navController, // El controlador que gestiona la pila de backstack
        startDestination = Screen.Launch.route // La ruta de la pantalla inicial
    ) {
        // Define cada destino del grafo de navegación:
        // Asocia la ruta "launch" con el Composable LaunchScreen
        composable(Screen.Launch.route)      { LaunchScreen(navController) }
        // Asocia la ruta "sign_in" con el Composable SignInScreen
        composable(Screen.SignIn.route)      { SignInScreen(navController) }
        // Asocia la ruta "sign_up" con el Composable SignUpScreen
        composable(Screen.SignUp.route)      { SignUpScreen(navController) }
        // Asocia la ruta "verify_email" con el Composable VerifyEmailScreen
        composable(Screen.VerifyEmail.route) { VerifyEmailScreen(navController) }
        // Asocia la ruta "forgot_pass" con el Composable ForgotPasswordScreen
        composable(Screen.Forgot.route)      { ForgotPasswordScreen(navController) }
        // Asocia la ruta "main" con el Composable MainScreen (que probablemente contiene su propio NavHost o Pager)
        composable(Screen.Main.route)        { MainScreen(navController) }

        // Aquí se podrían añadir más destinos (pantallas) a medida que la aplicación crezca.
        // Por ejemplo:
        // composable(Screen.ProfileEdit.route) { EditProfileScreen(navController) }
        // composable("details/{itemId}") { backStackEntry ->
        //     val itemId = backStackEntry.arguments?.getString("itemId")
        //     DetailsScreen(navController, itemId)
        // }


        //Direcciona a la pantalla de norificaciones
        composable(Screen.NotificationSettings.route) { ConfigureNotificationScreen(onNavigateUp = { navController.popBackStack() }) }
        //Direcciona a la pantalla de editar perfil
        composable(Screen.EditPersonal.route) {
            EditProfileScreen(navController)
        }
        //Direcciona a la pantalla de cambiar contraseña
        composable("changePassword") {
            ChangePasswordScreenPreviewOnly(navController = navController)
        }

    }
}

// Nota: Generalmente no se crea una @Preview para el AppNavGraph completo, ya que
// su propósito es definir la estructura de navegación y no una UI visual específica
// en sí misma. Las previews se crean para las pantallas individuales (LaunchScreen, etc.).