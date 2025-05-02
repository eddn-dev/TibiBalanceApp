package com.app.tibibalance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.app.tibibalance.ui.components.TextButtonLink

@Composable
fun FeatureResetPasswordScreen() {
    // Contenido de la pantalla
    // Contenido de la pantalla
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


                Header(
                    title = "Reestablecer Contraseña",
                    showBackButton = false,
                    profileImage = null
                )




                ImageContainer(
                    imageResId = R.drawable.ic_reset_password_image,  // Reemplaza con tu imagen
                    contentDescription = "Imagen de inicio", // Descripción de la imagen
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

                FormContainer (
                    modifier = Modifier
                        .height(190.dp)  // Establece un alto específico
                ){
                    var newpassword by remember { mutableStateOf("") }

                    InputPassword(
                        value = newpassword,
                        onValueChange = { newpassword = it },
                        placeholder = "Nueva Contraseña" // ← placeholder personalizado en inglés
                    )

                    var confirmPassword by remember { mutableStateOf("") }

                    InputPassword(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirmar Contraseña" // ← placeholder personalizado en inglés
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Description(
                            text = "¿Olvidaste tu contraseña? ",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButtonLink(
                            text = "Clic aquí",
                            onClick = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // Primer PrimaryButton
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