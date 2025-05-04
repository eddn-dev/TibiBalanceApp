// ui/screens/auth/ForgotPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun ForgotPasswordScreen(
    navController: NavController   // ahora recibe el NavController
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                ImageContainer(
                    resId = R.drawable.password1,
                    contentDescription = "Recuperar contraseña",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(vertical = 16.dp)
                )
                Spacer(Modifier.height(15.dp))
                Description(
                    text = "Ingresa tu correo electrónico y enviaremos \n un link para recuperar tu contraseña",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                FormContainer {
                    var email by remember { mutableStateOf("") }
                    InputEmail(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Correo electrónico"
                    )
                }
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = "Enviar",
                    onClick = { /* Aquí podrías navegar o llamar a vm.resend */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                )
            }
        }

        Header(
            title = "Recuperar Contraseña",
            showBackButton = true,
            onBackClick = { navController.popBackStack() },
            profileImage = null,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 918,
    name = "ForgotPasswordScreen 412x918"
)
@Composable
fun PreviewForgotPasswordScreen() {
    // Para que compile en preview:
    ForgotPasswordScreen(navController = rememberNavController())
}
