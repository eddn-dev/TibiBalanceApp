// src/main/java/com/app/tibibalance/ui/screens/ViewProfileScreen.kt
package com.app.tibibalance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun ViewProfileScreen() {
    // mismo degradado que HomeScreen
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            FormContainer {
                ProfileContainer(
                    imageResId         = R.drawable.imagenprueba,
                    size               = 110.dp,
                    contentDescription = "Foto de perfil"
                )
                Spacer(Modifier.height(12.dp))

                Subtitle(text = "Nora Soto")

                Spacer(Modifier.height(20.dp))
                // Fecha de nacimiento
                Subtitle(text = "Fecha de nacimiento:")
                InputText(
                    value         = "29/07/2004",
                    onValueChange = {},
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Spacer(Modifier.height(20.dp))
                // Correo electrónico
                Subtitle(text = "Correo electrónico:")
                InputText(
                    value         = "norasoto5@gmail.com",
                    onValueChange = {},
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Spacer(Modifier.height(32.dp))
                // Botones
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text     = "Editar perfil",
                        onClick  = { /* TODO */ },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text     = "Cerrar sesión",
                        onClick  = { /* TODO */ },
                        modifier = Modifier
                            .width(150.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewViewProfileScreen() {
    ViewProfileScreen()
}
