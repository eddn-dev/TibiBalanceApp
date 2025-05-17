package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.*
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ModalContainer
import com.app.tibibalance.ui.components.buttons.PrimaryButton

/**
 * @file    ModalChallengeScreen.kt
 * @ingroup ui_screens_habits
 * @brief   Muestra un diálogo de inicio de reto con animación y recordatorio.
 */
@Composable
fun ModalChallengeScreen(
    subtitle: String = "¡Has iniciado un nuevo reto!",
    message: String = "Los hábitos en modo reto se identifican con un 🔥 en la pantalla ‘Mis hábitos’.",
    lottieRes: Int = R.raw.challenge_animation,
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit
) {
    ModalContainer(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Lottie animation
            val compositionState = rememberLottieComposition(
                LottieCompositionSpec.RawRes(lottieRes)
            )
            val composition = compositionState.value
            val progress = animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            ).value

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

            // Styled message
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Recordatorio:")
                    }
                    append(" ")
                    append(message)
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Primary button
            PrimaryButton(
                text = "Aceptar",
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

/**
 * Preview for ModalChallengeScreen
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewModalChallengeScreen() {
    ModalChallengeScreen(
        onDismissRequest = {},
        onAccept = {}
    )
}
