// ui/screens/launch/LaunchScreen.kt
package com.app.tibibalance.ui.screens.launch

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.ui.navigation.Screen

@Composable
fun LaunchScreen(nav: NavController, vm: LaunchViewModel = hiltViewModel()) {
    val loggedIn by vm.isLoggedIn.collectAsState()

    LaunchedEffect(loggedIn) {
        if (loggedIn) {
            nav.navigate(Screen.Home.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { nav.navigate(Screen.SignIn.route) }) {
            Text("Iniciar sesión")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { nav.navigate(Screen.SignUp.route) }) {
            Text("Registrarse")
        }
    }
}
