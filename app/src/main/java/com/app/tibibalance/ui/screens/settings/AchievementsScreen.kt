package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.containers.AchievementContainer

@Composable
fun AchievementsScreen(
    navController: NavHostController
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
            Spacer(Modifier.height(10.dp)) // Espacio superior.


            FormContainer (
                backgroundColor = Color(0xFFAED3E3),
                modifier = Modifier.fillMaxWidth()
            ) {

                AchievementContainer(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.racha),
                            contentDescription = "fire"
                        )
                    },
                    title = "Racha",
                    description = "Usaste la app 3 días seguidos",
                    percent = 70, // progreso  de logro
                    isUnlocked = true // <- Este aparecerá como bloqueado
                )

                AchievementContainer(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_smile),
                            contentDescription = "Smile"
                        )
                    },
                    title = "Todo en su lugar",
                    description = "Registra un estado de ánimo “feliz” por siete días consecutivos",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )


                AchievementContainer(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_medal),
                            contentDescription = "medal"
                        )
                    },
                    title = "Siete días en línea",
                    description = "Cumplir un hábito por siete días consecutivos",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )


                AchievementContainer(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.iconperfilphoto),
                            contentDescription = "FotoPerfil"
                        )
                    },
                    title = "Un placer conocernos",
                    description = "Agrega una foto de perfil",
                    percent = 70, // progreso  de logro
                    isUnlocked = false // <- Este aparecerá como bloqueado
                )

            }

        }
    }
}
/*@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PreviewAchievementsScreen() {
    AchievementsScreen()
}*/

