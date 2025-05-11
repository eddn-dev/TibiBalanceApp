// src/main/java/com/app/tibibalance/ui/screens/emotional/RegisterEmotionalStateModal.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import java.time.LocalDate

/**
 * @file    RegisterEmotionalStateModal.kt
 * @ingroup ui_screens_emotional
 * @brief   Diálogo para escoger y confirmar la emoción de un día dado.
 *
 * @param date        Fecha a la que se está asignando la emoción.
 * @param onDismiss   Callback que se invoca al cerrar el modal sin confirmar.
 * @param onConfirm   Callback que recibe la [Emotion] seleccionada cuando el usuario pulsa "LISTO".
 */
@Composable
fun RegisterEmotionalStateModal(
    date: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (Emotion) -> Unit
) {
    // Emociones disponibles en orden de rueda
    val emotions = listOf(
        Emotion.FELICIDAD,
        Emotion.TRANQUILIDAD,
        Emotion.TRISTEZA,
        Emotion.ENOJO,
        Emotion.DISGUSTO,
        Emotion.MIEDO
    )

    var selectedEmotion by remember { mutableStateOf<Emotion?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val spinState = rememberSpinWheelState(
        pieCount         = emotions.size,
        durationMillis   = 400,
        delayMillis      = 0,
        rotationPerSecond= 5f,
        easing           = LinearOutSlowInEasing,
        startDegree      = 0f,
        resultDegree     = 360f,
        autoSpinDelay    = 0
    )

    ModalContainer(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier           = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment= Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.spacedBy(16.dp)
        ) {
            // Título con la fecha
            Subtitle(
                text      = "¿Cómo te sentiste el ${date.dayOfMonth}/${date.monthValue}/${date.year}?",
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            // Rueda de emociones
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                SpinWheel(
                    state      = spinState,
                    onClick    = {
                        coroutineScope.launch { spinState.animate() }
                    },
                    dimensions = SpinWheelDefaults.spinWheelDimensions(
                        spinWheelSize = 280.dp,
                        frameWidth    = 0.dp,
                        selectorWidth = 0.dp
                    ),
                    colors     = SpinWheelDefaults.spinWheelColors(
                        frameColor    = Color.Transparent,
                        dividerColor  = Color.Transparent,
                        selectorColor = Color.Transparent,
                        pieColors     = List(emotions.size) { Color.Transparent }
                    )
                ) { pieIndex ->
                    val emotion = emotions[pieIndex]
                    EmotionButton(
                        emotionLabel = emotion.label,
                        emotionRes   = emotion.drawableRes,
                        isSelected   = selectedEmotion == emotion,
                        size         = if (emotion == Emotion.DISGUSTO) 80.dp else 48.dp,
                        onClick      = {
                            selectedEmotion = emotion
                        },
                        modifier     = Modifier.padding(4.dp)
                    )
                }
            }

            // Botón de confirmación
            PrimaryButton(
                text    = "LISTO",
                enabled = selectedEmotion != null,
                onClick = {
                    selectedEmotion?.let(onConfirm)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

/**
 * @brief   Tipos de emoción disponibles.
 * @param drawableRes Drawable asociado.
 * @param label       Texto descriptivo.
 */
enum class Emotion(val drawableRes: Int, val label: String) {
    FELICIDAD    (R.drawable.iconhappyimage,    "Felicidad"),
    TRANQUILIDAD (R.drawable.iconcalmimage,     "Tranquilidad"),
    TRISTEZA     (R.drawable.iconsadimage,      "Tristeza"),
    ENOJO        (R.drawable.iconangryimage,    "Enojo"),
    DISGUSTO     (R.drawable.icondisgustimage,  "Disgusto"),
    MIEDO        (R.drawable.iconfearimage,     "Miedo");
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewRegisterEmotionalStateModal() {
    // Previsión con una fecha de ejemplo y acciones vacías
    RegisterEmotionalStateModal(
        date      = LocalDate.now(),
        onDismiss = {},
        onConfirm = {}
    )
}
