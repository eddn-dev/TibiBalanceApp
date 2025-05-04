// ui/components/SecondaryButton.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Usamos Surface para obtener shadow (shadowElevation) + border
    Surface(
        modifier = modifier
            .width(120.dp)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = if (enabled) 8.dp else 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.2.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) Color.Black else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 60)
@Composable
fun PreviewSecondaryButton() {
    SecondaryButton(
        text = "Cancelar",
        onClick = { /* prueba */ }
    )
}
