// ui/components/InputDate.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InputDate(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Fecha de nacimiento*",
    isError: Boolean = false,
    supportingText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputText(
            value = value,
            onValueChange = {},                   // no editable
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            placeholder = placeholder,
            isError = isError,
            supportingText = supportingText,
            singleLine = true
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = if (isError) MaterialTheme.colorScheme.error
            else Color(0xFF000000),
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onClick)
        )
    }
}
