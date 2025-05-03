package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.Description
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.InputPassword
import com.app.tibibalance.ui.components.PrimaryButton


@Composable
fun RecoverPasswordScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Fondo con gradiente
        GradientBackgroundScreen {
            // Contenido con gradiente, sin el Header
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp) // Espacio para que el header no lo tape
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    ImageContainer(
                        imageResId = R.drawable.ic_reset_password_image,
                        contentDescription = "Imagen de inicio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(398.dp)
                            .padding(bottom = 32.dp)
                    )

                    Description(
                        text = "Ingresa tu nueva contraseña",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FormContainer(
                        modifier = Modifier.height(170.dp)
                    ) {
                        var newpassword by remember { mutableStateOf("") }

                        InputPassword(
                            value = newpassword,
                            onValueChange = { newpassword = it },
                            placeholder = "Nueva Contraseña"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        var confirmPassword by remember { mutableStateOf("") }

                        InputPassword(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Confirmar Contraseña"
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    PrimaryButton(
                        text = "Enviar",
                        onClick = { /* Acción del botón */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // 2. Header encima del gradiente (posición fija arriba)
        Header(
            title = "Reestablecer Contraseña",
            showBackButton = false,
            profileImage = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}
