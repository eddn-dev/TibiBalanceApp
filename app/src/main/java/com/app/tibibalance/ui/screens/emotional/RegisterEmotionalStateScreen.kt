// ui/screens/emotional/RegisterEmotionalStateScreen.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ModalContainer
import com.app.tibibalance.ui.components.buttons.EmotionButton
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.texts.Subtitle
import com.commandiron.spin_wheel_compose.SpinWheel
import com.commandiron.spin_wheel_compose.SpinWheelDefaults
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState
import kotlinx.coroutines.launch

@Composable
fun RegisterEmotionalStateScreen(
    onDismissRequest: () -> Unit,
    onEmotionSelected: (Emotion) -> Unit = {},
    onConfirm: () -> Unit
) {
    // Lista fija de emociones en el orden del “ruedo”
    val emotions = listOf(
        Emotion.FELICIDAD,
        Emotion.TRANQUILIDAD,
        Emotion.TRISTEZA,
        Emotion.ENOJO,
        Emotion.DISGUSTO,
        Emotion.MIEDO
    )

    var selected by remember { mutableStateOf<Emotion?>(null) }
    val scope = rememberCoroutineScope()
    // Estado del spin wheel
    val spinState = rememberSpinWheelState(
        pieCount        = emotions.size,
        durationMillis  = 400,
        delayMillis     = 0,
        rotationPerSecond = 5f,
        easing          = LinearOutSlowInEasing,
        startDegree     = 0f,
        resultDegree    = 360f,
        autoSpinDelay   = 0
    )

    ModalContainer(
        onDismissRequest = onDismissRequest,
        shape            = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Subtitle(
                text      = "Selecciona la emoción que más resonó contigo hoy.",
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            // SpinWheel sin borde ni colores, solo para reordenar los items
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                SpinWheel(
                    state      = spinState,
                    onClick    = {
                        scope.launch {
                            spinState.animate()
                        }
                    },
                    dimensions = SpinWheelDefaults.spinWheelDimensions(
                        spinWheelSize = 280.dp,
                        frameWidth    = 0.dp,     // sin marco
                        selectorWidth = 0.dp      // sin selector
                    ),
                    colors     = SpinWheelDefaults.spinWheelColors(
                        frameColor   = Color.Transparent,
                        dividerColor = Color.Transparent,
                        selectorColor= Color.Transparent,
                        pieColors    = List(emotions.size) { Color.Transparent }
                    )
                ) { pieIndex ->
                    val emotion = emotions[pieIndex]
                    EmotionButton(
                        emotionLabel   = emotion.label,
                        emotionRes     = emotion.drawableRes,
                        isSelected     = selected == emotion,
                        size           = if (emotion == Emotion.DISGUSTO) 80.dp else 48.dp,
                        onClick        = {
                            selected = emotion
                            onEmotionSelected(emotion)
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // Botón de confirmar
            PrimaryButton(
                text     = "LISTO",
                onClick  = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

enum class Emotion(val drawableRes: Int, val label: String) {
    FELICIDAD   (R.drawable.iconhappyimage,   "Felicidad"),
    TRANQUILIDAD(R.drawable.iconcalmimage,    "Tranquilidad"),
    TRISTEZA    (R.drawable.iconsadimage,     "Tristeza"),
    ENOJO       (R.drawable.iconangryimage,   "Enojo"),
    DISGUSTO    (R.drawable.icondisgustimage, "Disgusto"),
    MIEDO       (R.drawable.iconfearimage,    "Miedo");
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewRegisterEmotionalStateScreen() {
    RegisterEmotionalStateScreen(
        onDismissRequest = {},
        onEmotionSelected = {},
        onConfirm = {}
    )
}
