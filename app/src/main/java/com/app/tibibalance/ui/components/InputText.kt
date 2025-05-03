// ui/components/InputText.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*

/**
 * Campo de texto genérico estilo “underline”.
 *
 * @param value            Texto actual.
 * @param onValueChange    Callback de cambios.
 * @param placeholder      Texto fantasma cuando está vacío.
 * @param isError          Marca el campo en rojo.
 * @param supportingText   Mensaje debajo (p. ej. error o ayuda); `null` para ninguno.
 * @param singleLine       Una sola línea (default) o multilinea.
 * @param visualTransformation   Permite formatear (p.ej. PasswordVisualTransformation).
 */
@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),                 // alto mínimo
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
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
