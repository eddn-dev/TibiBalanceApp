/* ui/components/InputPassword.kt */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun InputPassword(
    value          : String,
    onValueChange  : (String) -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Contraseña",
    isError        : Boolean  = false,
    supportingText : String?  = null,
    maxChars       : Int?     = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction    = ImeAction.Done
    )
) {
    var visible by remember { mutableStateOf(false) }

    /* —— misma paleta que InputEmail / InputText —— */
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor  = MaterialTheme.colorScheme.surface,
        focusedBorderColor      = if (isError)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error,
        focusedLabelColor       = if (isError)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error
    )

    OutlinedTextField(
        value            = value,
        onValueChange    = { onValueChange( if (maxChars != null) it.take(maxChars) else it ) },
        modifier         = modifier.fillMaxWidth(),
        label            = { Text(label) },
        singleLine       = true,
        isError          = isError,
        visualTransformation = if (visible)
            VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions  = keyboardOptions,
        trailingIcon     = {
            val icon = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val tint = if (isError)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant

            IconButton(onClick = { visible = !visible }) {
                Icon(icon, null, tint = tint)
            }
        },
        supportingText   = {
            when {
                isError        && supportingText != null -> Text(supportingText)
                maxChars != null                        -> Text("${value.length}/$maxChars")
            }
        },
        shape  = RoundedCornerShape(12.dp),
        colors = colors
    )
}