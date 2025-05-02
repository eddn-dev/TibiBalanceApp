package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.shadow

@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFAED3E3),
    padding: PaddingValues = PaddingValues(16.dp),
    cornerRadius: Int = 12,
    elevation: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius.dp))
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(backgroundColor)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}


@Preview
@Composable
fun LoginScreen() {
    FormContainer { // Codigo de prueba
        TextField(value = "", onValueChange = {}, label = { Text("Email") })
        TextField(value = "", onValueChange = {}, label = { Text("Password") })
        Button(onClick = { /* Acción de login */ }) {
            Text("Iniciar sesión")
        }
    }
}