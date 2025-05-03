// ui/navigation/Screen.kt
package com.app.tibibalance.ui.navigation

sealed class Screen(val route: String) {
    data object Launch : Screen("launch")
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object Forgot : Screen("forgot_pass")
    data object Home  : Screen("home")
    data object Recover : Screen("recover_pass")
    data object VerifyEmail  : Screen("verify_email")
}
