// ui/components/DangerButton.kt
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón “peligroso” para acciones destructivas (logout, delete…).
 * Estándar visual idéntico a PrimaryButton.
 */
@Composable
fun DangerButton(
    text       : String,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier.fillMaxWidth(),
    enabled    : Boolean  = true,
    isLoading  : Boolean  = false,
    container  : Color    = Color(0xFFFF3333),
    contentColor: Color   = Color.White
) {
    val realEnabled = enabled && !isLoading

    Button(
        onClick = onClick,
        enabled = realEnabled,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor          = container,
            contentColor            = contentColor,
            disabledContainerColor  = container.copy(alpha = 0.40f),
            disabledContentColor    = contentColor.copy(alpha = 0.40f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier    = Modifier.size(20.dp),
                color       = contentColor
            )
        } else {
            Text(
                text  = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
