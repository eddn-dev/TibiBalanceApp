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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.navigation.Screen

@Composable
fun LaunchScreen(
    nav: NavController,
    vm : LaunchViewModel = hiltViewModel()
) {
    val session by vm.sessionState.collectAsState()

    /* ── Navegación reactiva ─────────────────────────────── */
    LaunchedEffect(session) {
        when {
            !session.loggedIn               -> Unit                // sigue en Launch
            session.verified                -> nav.navigate(Screen.Home.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
            else /* logged && !verified */  -> nav.navigate(Screen.VerifyEmail.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
        }
    }

    /* ── UI de bienvenida (idéntica salvo el LaunchedEffect) ── */
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = .15f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        containerColor = Color.Transparent
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            /* Ilustración */
            ImageContainer(
                resId = R.drawable.launch,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            )

            /* Sub-título */
            Text(
                text = stringResource(R.string.launch_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            /* Botones */
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrimaryButton(
                    text = stringResource(R.string.btn_sign_in),
                    onClick = { nav.navigate(Screen.SignIn.route) }
                )
                Spacer(Modifier.height(24.dp))
                PrimaryButton(
                    text = stringResource(R.string.btn_sign_up),
                    onClick = { nav.navigate(Screen.SignUp.route) }
                )
            }
        }
    }
}
