// file: ui/components/ProgressBar.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ProgressBar(
    percent: Int,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFFBCE2C2),
    trackColor: Color = Color(0xFFE0E0E0)
) {
    Column(
        modifier = modifier
            .padding(top = 5.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Track
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(trackColor)

        ) {
            // Progress fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(barColor)
            )
        }
        // Percentage text
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$percent%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF000000),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 40)
@Composable
fun ProgressBarPreview() {
    ProgressBar(percent = 100)
}
