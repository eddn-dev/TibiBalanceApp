// file: ui/components/Alert.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class AlertSeverity {
    Error, Warning, Info, Success
}

@Composable
fun Alert(
    text: String,
    severity: AlertSeverity,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    val backgroundColor = when (severity) {
        AlertSeverity.Error   -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        AlertSeverity.Warning -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
        AlertSeverity.Info    -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        AlertSeverity.Success -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    }
    val contentColor = when (severity) {
        AlertSeverity.Error   -> MaterialTheme.colorScheme.error
        AlertSeverity.Warning -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.Info    -> MaterialTheme.colorScheme.primary
        AlertSeverity.Success -> MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.invoke()
        if (icon != null) Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlertPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Alert(text = "Error: campo obligatorio", severity = AlertSeverity.Error)
            Spacer(modifier = Modifier.height(8.dp))
            Alert(text = "Advertencia: formato inválido", severity = AlertSeverity.Warning)
            Spacer(modifier = Modifier.height(8.dp))
            Alert(text = "Info: cambios guardados", severity = AlertSeverity.Info)
            Spacer(modifier = Modifier.height(8.dp))
            Alert(text = "Éxito: registro completado", severity = AlertSeverity.Success)
        }
    }
}
