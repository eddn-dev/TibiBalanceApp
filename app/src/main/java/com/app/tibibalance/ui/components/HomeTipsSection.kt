/**
 * @file    HomeTipsSection.kt
 * @ingroup ui_components
 * @brief   Componente visual que muestra la sección informativa inicial del Home cuando no hay reloj conectado.
 *
 * @details
 * Esta sección se presenta en la pantalla principal (`HomeScreen`) cuando no hay dispositivo wearable conectado.
 * Está compuesta por:
 * - Una tarjeta de invitación a conectar el reloj con fondo azul personalizado.
 * - Una tarjeta de consejos de salud estilo “tip del día”.
 * - Subcomponentes en forma de tarjetas redondeadas con texto e íconos.
 *
 * No utiliza `AchievementContainer` para evitar mostrar barras de progreso.
 *
 * @param 'onConnectClick Acción a ejecutar cuando el usuario toca la tarjeta para conectar su reloj.
 */

package com.app.tibibalance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.texts.*

@Composable
fun HomeTipsSection(
    onConnectClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(50.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 🟦 Tarjeta de conexión al reloj
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFB2E1F5), shape = RoundedCornerShape(20.dp))
                .clickable { onConnectClick() }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fruit_watch),
                    contentDescription = "Personaje con reloj",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "¡Mejora tu monitoreo! \nConecta tu reloj.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "(Toca para conectar)",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF007AFF))
                    )
                }
            }
        }

        // 🟦 Contenedor general del tip del día
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFE6F4FC), shape = RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Title("Tip del día")

            // Tarjeta: "¡Comienza a moverte hoy!"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCEBF2), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Subtitle("¡Comienza a Moverte Hoy!")
            }

            // Tarjeta: No necesitas un reloj
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCEBF2), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nerdy_face),
                        contentDescription = "Ícono tip",
                        modifier = Modifier.size(55.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "No necesitas un reloj \npara mejorar tu salud.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }


            // Tarjeta: Consejo de hidratación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCEBF2), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Bebe un vaso de agua antes de cada comida y aprovecha para caminar un poco.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "¡Mantenerte hidratado y activo te dará más energía!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_water_bottle),
                        contentDescription = "Botella de agua",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}
