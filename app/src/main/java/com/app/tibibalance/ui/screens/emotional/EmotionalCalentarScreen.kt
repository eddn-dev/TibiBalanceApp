// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionalCalentarScreen.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title

@Composable
fun EmotionalCalentarScreen(
    onKnowMore: () -> Unit = {},
    onDayClick: (day: Int) -> Unit = {},
    mostFrequentEmotionDays: Int = 5,
    mostFrequentEmotionRes: Int = R.drawable.iconhappyimage
) {
    // fondo degradado del azul claro al blanco
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // contenedor celeste principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFAED3E3).copy(alpha = .45f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pill con título
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF85C3DE),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Title(
                        text = "¿Cómo te sientes hoy?",
                        textAlign = TextAlign.Center
                    )
                }

                // Calendario emocional
                // **Tendrás que generar tu lista real de días en un ViewModel o similar.**
                val sampleDays = remember {
                    // ejemplo: los primeros 7 días con distintas emociones, el resto vacíos
                    listOf(
                        EmotionDay(1, R.drawable.iconhappyimage, onClick = { onDayClick(1) }),
                        EmotionDay(2, R.drawable.iconsadimage,    onClick = { onDayClick(2) }),
                        EmotionDay(3, R.drawable.iconcalmimage,   onClick = { onDayClick(3) }),
                        EmotionDay(4, R.drawable.iconangryimage,  onClick = { onDayClick(4) }),
                        EmotionDay(5, R.drawable.icondisgustimage,onClick = { onDayClick(5) }),
                        EmotionDay(6, R.drawable.iconfearimage,   onClick = { onDayClick(6) }),
                        EmotionDay(7, R.drawable.iconhappyimage,  onClick = { onDayClick(7) })
                    ) + List(28) { EmotionDay(null, null) }
                }

                CalendarGrid(
                    month = "Abril",
                    days  = sampleDays,
                    modifier = Modifier.fillMaxWidth()
                )

                // “Saber más”
                TextButtonLink(
                    text = "SABER MÁS ?",
                    onClick = onKnowMore,
                    modifier = Modifier.align(Alignment.Start)
                )

                // Estado emocional más repetido
                Subtitle(text = "Estado emocional más repetido")
                HabitContainer(
                    icon = {
                        ImageContainer(
                            resId = mostFrequentEmotionRes,
                            contentDescription = "Emoción repetida",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    text = "Has estado ${emotionName(mostFrequentEmotionRes)} durante " +
                            "$mostFrequentEmotionDays días de este mes",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {}
                )
            }
        }
    }
}

private fun emotionName(resId: Int): String = when(resId) {
    R.drawable.iconhappyimage   -> "Feliz"
    R.drawable.iconsadimage     -> "Triste"
    R.drawable.iconcalmimage    -> "Tranquilo"
    R.drawable.iconangryimage   -> "Enojado"
    R.drawable.icondisgustimage -> "Disgusto"
    R.drawable.iconfearimage    -> "Miedo"
    else                         -> ""
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewEmotionalCalentarScreen() {
    EmotionalCalentarScreen()
}
