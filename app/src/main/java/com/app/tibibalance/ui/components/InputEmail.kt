// ui/components/InputEmail.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputEmail(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Correo electrónico",
    isError: Boolean = false,
    supportingText: String? = null        // muestra debajo si no es nulo
) {
    Column(modifier.fillMaxWidth()) {

        /* Campo */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = TextStyle(
                    color = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.fillMaxWidth()
            ) { inner ->
                Box(Modifier.fillMaxSize(), Alignment.CenterStart) {
                    AnimatedPlaceholder(value.isEmpty(), placeholder, isError)
                    inner()
                }
            }
        }

        FieldUnderline(isError)

        /* Mensaje de apoyo / error */
        supportingText?.let {
            Text(
                text = it,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailPreview() {
    var text by remember { mutableStateOf("") }
    InputEmail(value = text, onValueChange = { text = it })
}
