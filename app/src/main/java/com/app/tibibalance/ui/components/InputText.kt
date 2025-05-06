package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/* genérico: una o varias líneas  ---------------------------------- */
@Composable
fun InputText(
    value               : String,
    onValueChange       : (String) -> Unit,
    modifier            : Modifier              = Modifier,
    placeholder         : String                = "",
    isError             : Boolean               = false,
    supportingText      : String?               = null,
    singleLine          : Boolean               = true,
    visualTransformation: VisualTransformation  = VisualTransformation.None,
    maxChars            : Int?                  = null,
    keyboardOptions     : KeyboardOptions       = KeyboardOptions.Default
) {
    /* ---------- 1.  usa OutlinedTextFieldDefaults ---------- */
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor      = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = MaterialTheme.colorScheme.outline
    )

    OutlinedTextField(
        value            = value,
        onValueChange    = {
            onValueChange(if (maxChars != null) it.take(maxChars) else it)
        },
        label            = { Text(placeholder) },
        modifier         = modifier.fillMaxWidth(),
        singleLine       = singleLine,
        maxLines         = if (singleLine) 1 else 4,
        visualTransformation = visualTransformation,
        keyboardOptions  = keyboardOptions,
        isError          = isError,
        supportingText   = {
            when {
                isError        && supportingText != null -> Text(supportingText)
                maxChars != null                        -> Text("${value.length}/$maxChars")
            }
        },
        colors           = colors,
        shape            = RoundedCornerShape(12.dp)
    )
}
