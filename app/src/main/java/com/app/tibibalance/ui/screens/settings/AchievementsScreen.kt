package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.containers.AchievementContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
@Composable
fun AchievementsScreen(
    onNavigateUp: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor principal que ocupa toda la pantalla.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Aplica el fondo degradado.
    ) {
        // Columna principal para el contenido, permite desplazamiento vertical.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita el scroll.
                .padding(horizontal = 16.dp, vertical = 24.dp), // Padding general.
            horizontalAlignment = Alignment.CenterHorizontally // Centra elementos horizontalmente.
        ) {
            Spacer(modifier = Modifier.height(77.dp))// Espacio superior.


            FormContainer (
                backgroundColor = Color(0xFFAED3E3),
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .background(Color(0xFF5997C7), shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 100.dp, vertical = 8.dp)
                        .align(Alignment.CenterHorizontally) // Centrado dentro del FormContainer
                ) {
                    Text(
                        text = "Tus logros",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))


                AchievementContainer(
                    iconRes = R.drawable.racha,
                    title = "Racha",
                    description = "Usaste la app 3 días seguidos",
                    percent = 70, // progreso  de logro
                    isUnlocked = true // <- Este aparecerá como bloqueado
                )

                AchievementContainer(
                    iconRes = R.drawable.ic_smile,
                    title = "Todo en su lugar",
                    description = "Registra un estado de ánimo “feliz” por siete días consecutivos",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )


                AchievementContainer(
                    iconRes = R.drawable.ic_medal,
                    title = "Siete días en línea",
                    description = "Cumplir un hábito por siete días consecutivos",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )


                AchievementContainer(
                    iconRes = R.drawable.iconperfilphoto,
                    title = "Un placer conocernos",
                    description = "Agrega una foto de perfil",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )

            }

        }
        Header(
            title = "Logros y Rachas",
            showBackButton = true,
            onBackClick = onNavigateUp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
/*@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PreviewAchievementsScreen() {
    AchievementsScreen()
}*/

