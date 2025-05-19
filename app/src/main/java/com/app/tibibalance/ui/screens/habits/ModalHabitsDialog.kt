package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModalHabitsDialog(
    visible: Boolean,
    icon: ImageVector = Icons.Default.Info,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.4f))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth()
        ) {
            // Ícono redondo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFE0E0F8), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF7F39FB)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje
            Text(
                text = message,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3EA8FE)
                )
            ) {
                Text(text = "Entendido", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}
