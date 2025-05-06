package com.app.tibibalance.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun InputNumber(
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    modifier      : Modifier = Modifier
) {
    InputText(
        value          = value,
        onValueChange  = { if (it.all(Char::isDigit)) onValueChange(it) },
        placeholder    = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier       = modifier
    )
}
