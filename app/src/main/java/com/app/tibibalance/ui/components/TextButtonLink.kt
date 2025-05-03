// ui/components/TextButtonLink.kt
package com.app.tibibalance.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextButtonLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underline: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (underline) TextDecoration.Underline
                else TextDecoration.None
            )
        )
    }
}