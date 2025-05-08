package com.app.tibibalance.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
@Composable
fun FeatureViewProfileScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackgroundScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(56.dp)) // Espacio para el header superpuesto

                FormContainer(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ProfileContainer(
                        imageResId = R.drawable.imagenprueba,
                        size = 110.dp,
                        contentDescription = "Foto de perfil"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Subtitle(text = "Nora Soto")

                    Spacer(modifier = Modifier.height(20.dp))

                    Subtitle(
                        text = "Fecha de nacimiento:",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    InputText(
                        value = "29/07/2004",
                        onValueChange = { },
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    Subtitle(
                        text = "Correo electrónico:",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    InputText(
                        value = "norasoto5@gmail.com",
                        onValueChange = { },
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 50.dp),
                            horizontalArrangement = Arrangement.spacedBy(30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SecondaryButton(
                                text = "Editar perfil",
                                onClick = {},
                                backgroundColor = Color.White
                            )

                            SecondaryButton(
                                text = "Cerrar sesión",
                                onClick = {},
                                backgroundColor = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Header superpuesto
        Header(
            title = "Visualizar Perfil",
            showBackButton = true,
            onBackClick = { },
            profileImage = null
        )
    }
}

