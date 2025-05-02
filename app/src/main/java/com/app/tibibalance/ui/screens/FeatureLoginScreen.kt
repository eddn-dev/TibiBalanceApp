package com.app.tibibalance.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.Caption
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.GoogleSignButton
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.InputEmail
import com.app.tibibalance.ui.components.InputPassword
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.TextButtonLink
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

@Composable
fun FeatureLoginScreen() {
    GradientBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp) // espacio general
        ) {
            // Header fijo arriba
            Header(
                title = "Registrarse",
                showBackButton = true,
                onBackClick = { }
            )
            // Imagen justo debajo del header
            ImageContainer(
                imageResId = R.drawable.registro_logo,
                contentDescription = "Recuperar contraseña",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 10.dp)
            )
//container preguntar
            FormContainer {
                var email by remember { mutableStateOf("") }
                var username by remember { mutableStateOf("") }
                var birthdate by remember { mutableStateOf("") }
                var date by remember { mutableStateOf("01/05/2025") }
                InputEmail(//username
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Nombre de Usuario*"
                )
                InputEmail(
                    value = birthdate,
                    onValueChange = { birthdate = it },
                    placeholder = "Fecha de Nacimiento*"
                    //InputDate(selectedDate = date, onDateSelected = { date = it })
                )
                InputEmail(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo electrónico*"
                )
                InputPassword(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Contraseña*"
                )
                InputPassword(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Confirmar Contraseña*"
                )
            }
            PrimaryButton(
                text = "Registrarse",
                onClick = { /* Acción del primer botón */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            GoogleSignButton(
                onClick = { /* Acción */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            /*Caption(
                text = "¿Ya tienes una cuenta?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            TextButtonLink(
                text = "Iniciar sesión",
                onClick = { },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )*/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Caption(text = "¿Ya tienes una cuenta?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButtonLink(
                    text = "Iniciar sesión",
                    onClick = { }
                )
            }
        }
    }
}