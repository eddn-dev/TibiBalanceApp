package com.app.tibibalance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.app.tibibalance.ui.screens.launch.LaunchScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = Screen.Launch.route) {
        composable(Screen.Launch.route) { LaunchScreen(navController) }
        // composable(Screen.SignIn.route) { SignInScreen(navController) }
        // composable(Screen.SignUp.route) { SignUpScreen(navController) }
        // composable(Screen.Forgot.route) { ForgotPasswordScreen(navController) }
        // composable(Screen.Home.route)  { HomeScreen() }
    }
}
