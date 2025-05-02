package com.app.tibibalance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.R

@Composable
fun FeatureHomeScreen() {
    // Contenido de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf5f5f5)) // Fondo gris claro
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            ImageContainer(
                imageResId = R.drawable.ic_home_image,  // Reemplaza con tu imagen
                contentDescription = "Imagen de inicio", // Descripción de la imagen
                modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(bottom = 32.dp)
            )

            // Primer PrimaryButton
            PrimaryButton(
                text = "Botón 1",  // Pasamos el texto al botón
                onClick = { /* Acción del primer botón */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )


        }
    }
}