// ui/components/SignUpProgressDialog.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Diálogo modal que se muestra mientras el registro está en progreso.
 *
 * @param loading  Cuando es true se presenta el spinner; en false no se muestra nada.
 */
@Composable
fun SignUpProgressDialog(
    loading: Boolean
) {
    if (!loading) return        // ↩️  nada que dibujar

    Dialog(onDismissRequest = { /* No cancelable */ }) {
        Surface(
            shape          = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .widthIn(min = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    text  = "Creando tu cuenta…",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
