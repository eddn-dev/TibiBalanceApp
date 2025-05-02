package com.app.tibibalance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.*

import com.app.tibibalance.R

@Composable
fun GradientBackgroundScreen(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB3E5FC), // Azul claro (puedes ajustar el tono)
                        Color.White
                    )
                )
            )
            .padding(16.dp) // Opcional
    ) {
        content()
    }
}

@Composable
fun FeatureHomeScreen() {
    GradientBackgroundScreen {
        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                //.background(Color(0xFFf5f5f5)) // Fondo gris claro
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Title(
                    text = "Bienvenido/ a/ e",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                ImageContainer(
                    imageResId = R.drawable.ic_home,  // Reemplaza con tu imagen
                    contentDescription = "Imagen de inicio", // Descripción de la imagen
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .padding(bottom = 32.dp)
                )
                Description(
                    text = "“La app que te ayuda a mantener un estilo de  vida saludable”",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Primer PrimaryButton

                Spacer(modifier = Modifier.height(30.dp))


                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = { /* Acción del botón */ },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                    // .padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(25.dp))

                PrimaryButton(
                    text = "Registrarse",
                    onClick = { /* Acción del botón */ },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )



            }
        }
    }
}