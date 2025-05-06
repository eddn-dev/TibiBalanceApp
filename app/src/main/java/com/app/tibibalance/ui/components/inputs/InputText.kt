package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/* Generic: one or more lines ---------------------------------- */
@Composable
fun InputText(
    value               : String,
    onValueChange       : (String) -> Unit,
    modifier            : Modifier             = Modifier,
    placeholder         : String               = "",
    isError             : Boolean              = false,
    supportingText      : String?              = null,
    singleLine          : Boolean              = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxChars            : Int?                 = null,
    keyboardOptions     : KeyboardOptions      = KeyboardOptions.Default
) {
    // Colores personalizados para borde y fondo
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor      = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = MaterialTheme.colorScheme.outline
    )

    OutlinedTextField(
        value            = value,
        onValueChange    = { input ->
            // Limita longitud si se pidió maxChars
            onValueChange(if (maxChars != null) input.take(maxChars) else input)
        },
        modifier         = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        label            = { Text(placeholder) },
        singleLine       = singleLine,
        maxLines         = if (singleLine) 1 else 4,
        visualTransformation = visualTransformation,
        keyboardOptions  = keyboardOptions,
        isError          = isError,
        supportingText   = {
            when {
                isError && supportingText != null -> Text(supportingText)
                maxChars != null                  -> Text("${value.length}/$maxChars")
            }
        },
        colors           = colors,
        shape            = RoundedCornerShape(12.dp)
    )
}
