// file: ui/components/InputPassword.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputPassword(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Contraseña"
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val visualTransformation: VisualTransformation =
        if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp), // altura suficiente para centrar texto
        ) {
            // Campo de texto: ocupa todo el ancho menos espacio para el ojo
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = visualTransformation,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = TextStyle(
                    color = Color(0xFF000000),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp), // deja espacio para el icono
                decorationBox = { inner ->
                    Box(
                        Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center // centra texto
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(
                                    color = Color(0xFF000000).copy(alpha = 0.5f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        inner()
                    }
                }
            )

            // Icono de ojo a la derecha, centrado verticalmente
            Icon(
                imageVector = if (passwordVisible)
                    Icons.Filled.VisibilityOff
                else
                    Icons.Filled.Visibility,
                contentDescription = null,
                tint = Color(0xFF000000),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .padding(end = 8.dp)
                    .clickable { passwordVisible = !passwordVisible }
            )
        }

        // Línea inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 50)
@Composable
fun InputPasswordPreview() {
    var pwd by remember { mutableStateOf("") }
    InputPassword(
        value = pwd,
        onValueChange = { pwd = it }
    )
}
