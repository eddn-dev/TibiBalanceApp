// file: ui/components/InputEmail.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputEmail(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Nombre de usuario/correo"
) {
    Column(
        modifier
            .fillMaxWidth()
    ) {
        // Campo de texto centrado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),               // altura suficiente para centrar 16sp
            contentAlignment = Alignment.Center // centra horizontal y verticalmente
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = TextStyle(
                    color = Color(0xFF000000),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                decorationBox = { inner ->
                    Box(
                        Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
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
        }
        // Línea inferior visible
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 50
)
@Composable
fun InputEmailPreview() {
    var email by remember { mutableStateOf("") }
    InputEmail(
        value = email,
        onValueChange = { email = it }
    )
}
