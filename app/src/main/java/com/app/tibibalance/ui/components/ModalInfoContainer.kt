package com.app.tibibalance.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/* ---------- Tipos de alerta ---------- */
enum class ModalType(
    val icon: ImageVector,
    val tint: Color
) {
    Success(Icons.Default.Check,  Color(0xFF4CAF50)),
    Error  (Icons.Default.Close,  Color(0xFFF44336)),
    Warning(Icons.Default.ErrorOutline, Color(0xFFFFC107)),
    Info   (Icons.Default.Info,   Color(0xFF2196F3))
}

/* ---------- API pública ---------- */
@Composable
fun ModalInfoContainer(
    type: ModalType = ModalType.Info,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    confirmText: String = "Aceptar"
) {
    Dialog(onDismissRequest = onDismiss) {

        /* ---- Animación de aparición ---- */
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(animationSpec = tween(350)) + fadeIn(),
            exit  = scaleOut(animationSpec = tween(200)) + fadeOut()
        ) {

            Box(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp, RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(cornerRadius))
            ) {

                /* ---- Contenido principal ---- */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    PrimaryButton(
                        text = confirmText,
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                /* ---- Icono flotante ---- */
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .background(type.tint, CircleShape)
                        .align(Alignment.TopCenter)
                        .offset(y = (-28).dp)          // la mitad para que sobresalga
                        .padding(12.dp)
                )
            }
        }
    }
}
