// ui/screens/auth/SignUpScreen.kt
package com.app.tibibalance.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

@Composable
fun SignUpScreen() {
    GradientBackgroundScreen {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Header(
                    title = "Registrarse",
                    showBackButton = true,
                    onBackClick = { },
                    profileImage = null
                )

                Spacer(modifier = Modifier.height(12.dp))

                ImageContainer(
                    imageResId = R.drawable.registro_logo,
                    contentDescription = "Imagen registro",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                FormContainer {
                    var username by remember { mutableStateOf("") }
                    InputText(value = username, onValueChange = { username = it })

                    var date by remember { mutableStateOf("") }
                    InputDate(selectedDate = date, onDateSelected = { date = it })

                    var email by remember { mutableStateOf("") }
                    InputEmail(value = email, onValueChange = { email = it })

                    var pwd by remember { mutableStateOf("") }
                    InputPassword(value = pwd, onValueChange = { pwd = it })
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                PrimaryButton(
                    text = "Registrarse",
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                GoogleSignButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
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
                        text = "Iniciar Sesión",
                        onClick = { }
                    )
                }
            }
        }
    }
}
