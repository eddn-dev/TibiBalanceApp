/* ui/components/inputs/InputNumber.kt */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * Numeric-only text field that re-uses [InputText] while enforcing digits-only
 * input and exposing `isError` / `supportingText` parameters.
 *
 * @param value           current contents (only digits allowed)
 * @param onValueChange   callback with the sanitized numeric string
 * @param placeholder     hint shown inside the field
 * @param modifier        outer [Modifier]
 * @param isError         draws the error border & tint when true
 * @param supportingText  optional helper / error message
 */
@Composable
fun InputNumber(
    value          : String,
    onValueChange  : (String) -> Unit,
    placeholder    : String,
    modifier       : Modifier = Modifier,
    isError        : Boolean  = false,
    supportingText : String?  = null
) {
    InputText(
        value           = value,
        onValueChange   = { if (it.all(Char::isDigit)) onValueChange(it) },
        placeholder     = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier        = modifier,
        isError         = isError,
        supportingText  = supportingText,
        singleLine      = true        // keeps same behaviour as before
    )
}
