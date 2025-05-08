/*package com.app.tibibalance.ui.screens

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
import com.app.tibibalance.ui.components.GoogleSignButton
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.InputDate
import com.app.tibibalance.ui.components.InputEmail
import com.app.tibibalance.ui.components.InputPassword
import com.app.tibibalance.ui.components.InputText
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.TextButtonLink
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun FeatureRegisterScreen() {
    GradientBackgroundScreen {
        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxSize()
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(5.dp),
            )
            {
                Header(
                    title = "Registrasrse",
                    showBackButton = true,
                    onBackClick = {
                        // Lógica al hacer clic en la flecha de retroceso
                    },
                    profileImage = null // No mostrar imagen de perfil
                )

                ImageContainer(
                    imageResId = R.drawable.registro_logo,  // Reemplaza con tu imagen
                    contentDescription = "Imagen registro", // Descripción de la imagen
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(398.dp)
                        .padding(bottom = 32.dp)
                )

                FormContainer {

                    var username by remember { mutableStateOf("") }
                    InputText(
                        value = username,
                        onValueChange = { username = it },
                    )

                    var date by remember { mutableStateOf("") }
                    InputDate(
                        selectedDate = date,
                        onDateSelected = { date = it }
                    )

                    var email by remember { mutableStateOf("") }
                    InputEmail(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Correo electrónico"
                    )

                    var password by remember { mutableStateOf("") }
                    InputPassword(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Contraseña"
                    )

                    var confirmPwd by remember { mutableStateOf("") }
                    InputPassword(
                        value = confirmPwd,
                        onValueChange = { confirmPwd = it },
                        placeholder = "Confirmar contraseña"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                // Primer PrimaryButton
                PrimaryButton(
                    text = "Registrarse",  // Pasamos el texto al botón
                    onClick = { /* Acción del primer botón */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                GoogleSignButton(
                    onClick = { /* Acción del botón */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Ajusta el espacio entre botones
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Description(
                        text = "¿Ya tienes una cuenta? ",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButtonLink(
                        text = "Iniciar Sesion",
                        onClick = {}
                    )
                }
            }
        }
    }
}*/
package com.app.tibibalance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope

@Composable
fun FeatureRegisterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackgroundScreen {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                ) {
                    item {
                        Spacer(modifier = Modifier.height(35.dp)) // Deja espacio para el header superpuesto

                        ImageContainer(
                            imageResId = R.drawable.registro_logo,
                            contentDescription = "Imagen registro",
                            modifier = Modifier
                                //.fillMaxWidth()
                                .height(340.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    item {
                        FormContainer {
                            var nombredeususrio by remember { mutableStateOf("") }
                            InputEmail(value = nombredeususrio, onValueChange = { nombredeususrio = it })
                            //placeholder = "Nombre de Usuario*"

                            var date by remember { mutableStateOf("Fecha de Nacimiento*") }
                            InputDate(selectedDate = date, onDateSelected = { date = it })

                            var email by remember { mutableStateOf("") }
                            InputEmail(value = email, onValueChange = { email = it })

                            var pwd by remember { mutableStateOf("") }
                            InputPassword(value = pwd, onValueChange = { pwd = it })

                            var confpwd by remember { mutableStateOf("") }
                            InputPassword(value = confpwd, onValueChange = { confpwd = it })
                        }

                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    item {
                        PrimaryButton(
                            text = "Registrarse",
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                        )

                        GoogleSignButton(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Description(
                                text = "¿Ya tienes una cuenta? ",
                                textAlign = TextAlign.Center
                            )
                            //Spacer(modifier = Modifier.width(8.dp))
                            TextButtonLink(
                                text = "Iniciar Sesión",
                                onClick = { }
                            )
                        }
                    }
                }
            }
        }

        // Header superpuesto como en "Recuperar Contraseña"
        Header(
            title = "Registrarse",
            showBackButton = true,
            onBackClick = { },
            profileImage = null
        )
    }
}
