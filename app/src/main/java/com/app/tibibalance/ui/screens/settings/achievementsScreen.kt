package com.app.tibibalance.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.containers.AchievementContainer
import com.app.tibibalance.ui.components.ImageContainer

@Composable
fun AchievementsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F4F8)) // Fondo general suave
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AchievementContainer(
            icon = {
                ImageContainer(
                    resId = R.drawable.ic_fire,
                    contentDescription = "Icono Racha",
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Racha",
            description = "Usaste la app 3 días seguidos",
            percent = 100
        )

        AchievementContainer(
            icon = {
                ImageContainer(
                    resId = R.drawable.ic_smile,
                    contentDescription = "Icono Bienvenida",
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Bienvenido",
            description = "Primer día usando la app",
            percent = 100
        )

        AchievementContainer(
            icon = {
                ImageContainer(
                    resId = R.drawable.ic_medal, // 🥇
                    contentDescription = "Icono Logros",
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Logros",
            description = "Visitaste todos los módulos de la app",
            percent = 80
        )

        AchievementContainer(
            icon = {
                ImageContainer(
                    resId = R.drawable.ic_avatar,
                    contentDescription = "Icono Personalizado",
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Personalizado",
            description = "Cambiaste tu avatar",
            percent = 100
        )
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PreviewAchievementsScreen() {
    AchievementsScreen()
}

