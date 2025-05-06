// ui/components/InputCommons.kt
package com.app.tibibalance.ui.components.inputs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Línea inferior del campo (1 dp) que cambia a `errorColor` si hay error */
@Composable
internal fun FieldUnderline(isError: Boolean) {
    val color = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onBackground
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

/** Texto placeholder con animación de opacidad */
@Composable
internal fun AnimatedPlaceholder(
    visible: Boolean,
    text: String,
    isError: Boolean
) {
    val alpha by animateFloatAsState(if (visible) 0.5f else 0f)
    if (alpha > 0f) {
        Text(
            text = text,
            color = (if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onBackground).copy(alpha = alpha),
            fontSize = 16.sp
        )
    }
}
