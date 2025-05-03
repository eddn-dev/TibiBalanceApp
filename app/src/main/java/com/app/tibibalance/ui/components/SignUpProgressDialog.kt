// ui/components/ModalInfoDialog.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog genérico con 2 estados:
 *  – loading = true  ➜ spinner
 *  – loading = false y [message] != null ➜ icono + texto + botón “Aceptar”
 */
@Composable
fun ModalInfoDialog(
    loading : Boolean,
    message : String?,           // null ⇒ no mostrar diálogo
    onAccept: () -> Unit         // se llama al pulsar “Aceptar”
) {
    // mostrar solo si está cargando o ya hay mensaje
    if (!loading && message == null) return

    Dialog(onDismissRequest = { /* bloqueamos back mientras loading */ }) {
        Surface(
            shape          = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            if (loading) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                        .widthIn(min = 200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Procesando…", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .widthIn(min = 220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /* círculo con check */
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        message ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    PrimaryButton(
                        text = "Aceptar",
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
