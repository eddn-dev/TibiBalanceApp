// ui/components/SignUpProgressDialog.kt
package com.app.tibibalance.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SignUpProgressDialog(
    loading: Boolean,
    emailSent: Boolean,
    onDismiss: () -> Unit
) {
    if (!loading && !emailSent) return

    Dialog(onDismissRequest = { if (emailSent) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            AnimatedContent(
                targetState = emailSent,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + scaleIn() togetherWith
                            fadeOut(animationSpec = tween(200)) + scaleOut()
                },
                modifier = Modifier
                    .padding(32.dp)
                    .sizeIn(minWidth = 200.dp)
            ) { success ->
                if (success) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Se envió un mensaje de confirmación a tu correo electrónico.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        PrimaryButton(
                            text = "Aceptar",
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Creando tu cuenta…", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
