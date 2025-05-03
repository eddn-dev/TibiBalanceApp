// ui/screens/auth/ForgotPasswordScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*


import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun ForgotPasswordScreen(){
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )
    Box(modifier = Modifier.fillMaxSize()){
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
                Description(
                    text = "Ingresa tu correo electrónico y enviaremos un link para recuperar tu contraseña",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                FormContainer {
                    var email by remember { mutableStateOf("") }
                    InputEmail(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Correo electrónico"
                    )
                }

                PrimaryButton(
                    text = "Enviar",  // Pasamos el texto al botón
                    onClick = { /* Acción del primer botón */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 16.dp)
                )

            }
        }

        //Encabezado/Header - - - - -> Falta un componente?
        Header(
            title = "Recuperar Contraseña",
            showBackButton = true,
            onBackClick = { }, //Redireccionar a iniciar sesión
            profileImage = null
        )
    }
}
