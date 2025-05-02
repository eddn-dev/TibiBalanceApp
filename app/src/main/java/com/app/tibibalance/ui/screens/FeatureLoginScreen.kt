package com.app.tibibalance.ui.screens


// Jetpack Compose UI
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*  // Incluye remember, mutableStateOf, by, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Recursos (como imágenes)
import com.app.tibibalance.R

// Componentes personalizados de tu app
import com.app.tibibalance.ui.components.*




@Composable
fun FeatureLoginScreen() {
    // Contenido de la pantalla
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackgroundScreen {
            // Contenido de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {


                    ImageContainer(
                        imageResId = R.drawable.ic_login_image,  // Reemplaza con tu imagen
                        contentDescription = "Imagen de inicio", // Descripción de la imagen
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(398.dp)
                            .padding(bottom = 32.dp)
                    )

                    FormContainer(
                        modifier = Modifier
                            .height(185.dp)  // Establece un alto específico
                    ) {
                        var email by remember { mutableStateOf("") }
                        InputEmail(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Correo electrónico"
                        )
                        var pwd by remember { mutableStateOf("") }
                        InputPassword(
                            value = pwd,
                            onValueChange = { pwd = it }
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

                    Spacer(modifier = Modifier.height(15.dp))
                    // Primer PrimaryButton


                    PrimaryButton(
                        text = "Iniciar sesión",
                        onClick = { /* Acción del botón */ },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                        // .padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    ImageContainer(
                        imageResId = R.drawable.ic_separador_image,  // Reemplaza con tu imagen
                        contentDescription = "Imagen de inicio", // Descripción de la imagen
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // Centra horizontalmente en Column
                            .width(240.dp) // Ancho fijo
                            .height(15.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GoogleSignButton(
                        onClick = { /* Acción del botón */ },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Description(
                            text = "¿Aún no tienes una cuenta?",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButtonLink(
                            text = "Regístrate",
                            onClick = {}
                        )
                    }


                }
            }
        }

        // 2. Header encima del gradiente (posición fija arriba)
        Header(
            //profileImage = dummyProfile
            title = "Iniciar Sesión",
            showBackButton = true,
            onBackClick = { },
            profileImage = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}
