/* ui/components/InputEmail.kt */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InputEmail(
    value          : String,
    onValueChange  : (String) -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Correo electrónico",
    isError        : Boolean  = false,
    supportingText : String?  = null,
    maxChars       : Int?     = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction    = ImeAction.Done
    )
) {
    /* Colores compartidos con los otros inputs */
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor  = MaterialTheme.colorScheme.surface,
        focusedBorderColor      = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = MaterialTheme.colorScheme.outline
    )

    OutlinedTextField(
        value          = value,
        onValueChange  = { onValueChange(if (maxChars != null) it.take(maxChars) else it) },
        modifier       = modifier.fillMaxWidth(),
        label          = { Text(label) },
        singleLine     = true,
        isError        = isError,
        keyboardOptions = keyboardOptions,
        supportingText = {
            when {
                isError        && supportingText != null -> Text(supportingText)
                maxChars != null                        -> Text("${value.length}/$maxChars")
            }
        },
        shape  = RoundedCornerShape(12.dp),
        colors = colors
    )
}

/* ——— Preview opcional ——— */
@Preview(showBackground = true)
@Composable
private fun InputEmailPreview() {
    var text = remember { "" }
    InputEmail(value = text, onValueChange = { text = it })
}
