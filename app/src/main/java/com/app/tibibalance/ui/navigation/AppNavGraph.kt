// ui/navigation/AppNavGraph.kt
package com.app.tibibalance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.app.tibibalance.ui.screens.auth.ForgotPasswordScreen
import com.app.tibibalance.ui.screens.auth.RecoverPasswordScreen
import com.app.tibibalance.ui.screens.launch.LaunchScreen
import com.app.tibibalance.ui.screens.auth.SignInScreen
import com.app.tibibalance.ui.screens.auth.SignUpScreen
import com.app.tibibalance.ui.screens.auth.VerifyEmailScreen
import com.app.tibibalance.ui.screens.home.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = Screen.Launch.route) {
        composable(Screen.Launch.route)  { LaunchScreen(navController) }
        composable(Screen.SignIn.route)  { SignInScreen(navController) }    // ← NECESARIO
        composable(Screen.SignUp.route)  { SignUpScreen(navController) }
        composable(Screen.Home.route)    { HomeScreen() }
        composable(Screen.Forgot.route)  { ForgotPasswordScreen(navController) }
        composable(Screen.Recover.route) { RecoverPasswordScreen(navController) }
        composable(Screen.VerifyEmail.route) { VerifyEmailScreen(navController) }
        composable(Screen.Forgot.route) { ForgotPasswordScreen(navController) }
    }
}
