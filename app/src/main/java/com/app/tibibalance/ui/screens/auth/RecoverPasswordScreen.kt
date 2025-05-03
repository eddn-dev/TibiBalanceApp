// ui/screens/auth/RecoverPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RecoverPasswordScreen(nav: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Recover-Password • Placeholder", fontSize = 20.sp)
    }
}
