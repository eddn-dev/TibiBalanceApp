// ui/navigation/Screen.kt
package com.app.tibibalance.ui.navigation

sealed class Screen(val route: String) {
    /* auth flow */
    data object Launch       : Screen("launch")
    data object SignIn       : Screen("sign_in")
    data object SignUp       : Screen("sign_up")
    data object Forgot       : Screen("forgot_pass")
    data object Recover      : Screen("recover_pass")
    data object VerifyEmail  : Screen("verify_email")

    /* app con BottomBar */
    data object Main         : Screen("main")          // ← ‹hub›
    data object Home         : Screen("main/home")
    data object Emotions     : Screen("main/emotions")
    data object Habits       : Screen("main/habits")
    data object Profile      : Screen("main/profile")
    data object Settings     : Screen("main/settings")
}
