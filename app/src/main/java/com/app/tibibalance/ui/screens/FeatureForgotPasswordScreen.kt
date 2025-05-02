package com.app.tibibalance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.tooling.preview.Preview

import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun FeatureForgotPasswordScreen() {
    //Degradado del background
    GradientBackgroundScreen {
    // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //Encabezado/Header - - - - -> Falta un componente?
            Header(
                title = "Recuperar Contraseña",
                showBackButton = false,
                profileImage = null
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                ImageContainer(
                    imageResId = R.drawable.password1,
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
                    TextField(value = "", onValueChange = {}, label = { Text("Correo") })
                }

                PrimaryButton(
                    text = "Enviar",  // Pasamos el texto al botón
                    onClick = { /* Acción del primer botón */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }

    }
}