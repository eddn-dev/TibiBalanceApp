package com.app.tibibalance.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.data.AuthManager
import com.app.tibibalance.ui.theme.PrimaryLight



@Preview(showBackground = true)
@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val isFormValid = email.isNotBlank() && password.isNotBlank()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFC3E2FA), Color.White)
                )
            )
    ) {

        AppBar()

        Spacer(modifier = Modifier.height(12.dp))


        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(320.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            color = PrimaryLight,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 8.dp)
                    )
                }

                Row {
                    Text("¿Olvidaste tu contraseña?")
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Clic aquí",
                        modifier = Modifier.clickable { },
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
        }

        PrimaryButton(
            text = "Iniciar sesión",
            enabled = isFormValid && !loading,
            onClick = {
                loading = true
                AuthManager.loginWithEmail(
                    email = email.trim(),
                    password = password,
                    onSuccess = {
                        loading = false
                        errorMessage = ""
                        // navegar a Home
                    },
                    onFailure = {
                        loading = false
                        password = ""                              // limpia contraseña
                        errorMessage = "Correo o contraseña incorrectos"
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
        )
        if (loading) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier.size(30.dp)
            )
        }


        Spacer(modifier = Modifier.height(1.dp))
        DividerWithText()
        Spacer(modifier = Modifier.height(1.dp))


        CustomButton(
            modifier = Modifier.clickable { },
            painter = painterResource(id = R.drawable.google),
            title = "Continuar con Google"
        )

        Spacer(modifier = Modifier.height(24.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("¿No tienes cuenta?")
            Spacer(Modifier.width(5.dp))
            Text(
                "Regístrate",
                modifier = Modifier.clickable {},
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
    }
}



@Composable
fun DividerWithText() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
        Text(
            text = "  o  ",
            color = Color.Gray
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
fun CustomButton(modifier: Modifier, painter: Painter, title: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 70.dp)
            .background(
                color = Color.Transparent,
                shape = ButtonDefaults.shape
            )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = ButtonDefaults.shape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )


        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(
        title = { Text(text = "Iniciar Sesión") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.White)
    )
}