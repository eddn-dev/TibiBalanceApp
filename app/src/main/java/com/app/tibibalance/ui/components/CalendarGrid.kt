// src/main/java/com/app/tibibalance/ui/components/CalendarGrid.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.R

data class EmotionDay(
    val day: Int?,
    val emotionRes: Int?,
    val isSelected: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun CalendarGrid(
    month: String,
    days: List<EmotionDay>,
    modifier: Modifier = Modifier
) {
    val weekDays = listOf("Do","Lu","Ma","Mi","Ju","Vi","Sa")

    Column(modifier = modifier.fillMaxWidth()) {
        // Mes
        Text(
            text      = month,
            style     = MaterialTheme.typography.titleMedium,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )
        // Encabezado de días
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { wd ->
                Text(
                    wd,
                    style     = MaterialTheme.typography.bodySmall,
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        // Celdas por semana
        days.chunked(7).forEach { week ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { item ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clickable { item.onClick() },
                        shape          = RoundedCornerShape(8.dp),
                        color          = if (item.isSelected) Color(0xFF85C3DE) else Color(0x40AED3E3),
                        border         = if (item.isSelected) BorderStroke(1.dp, Color(0xFF85C3DE)) else null,
                        tonalElevation = 0.dp
                    ) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // icono de la emoción, si existe
                            item.emotionRes?.let { res ->
                                ImageContainer(
                                    resId = res,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            // número de día semitransparente
                            item.day?.let { d ->
                                Text(
                                    text      = d.toString(),
                                    fontSize  = 16.sp,
                                    color     = Color.White.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    modifier  = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 400)
@Composable
fun PreviewCalendarGrid() {
    val sampleDays = listOf(
        EmotionDay(1, R.drawable.iconhappyimage, isSelected = true),
        EmotionDay(2, R.drawable.iconsadimage),
        EmotionDay(3, R.drawable.iconcalmimage),
        EmotionDay(4, R.drawable.iconangryimage),
        EmotionDay(5, R.drawable.icondisgustimage),
        EmotionDay(6, R.drawable.iconfearimage),
        EmotionDay(7, R.drawable.iconhappyimage)
    ) + List(28) { EmotionDay(null, null) }

    CalendarGrid(
        month = "Abril",
        days  = sampleDays
    )
}
