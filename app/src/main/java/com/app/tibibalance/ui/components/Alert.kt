// file: ui/components/Alert.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AlertSeverity {
    Success, Warning, Error
}

@Composable
fun Alert(
    text: String,
    severity: AlertSeverity,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?
) {
    val backgroundColor = when (severity) {
        AlertSeverity.Success -> Color(0xFFE2FAD9)
        AlertSeverity.Warning -> Color(0xFFFFF7D9)
        AlertSeverity.Error   -> Color(0xFFFDE2E2)
    }
    val contentColor = when (severity) {
        AlertSeverity.Success -> Color(0xFF2E7D32)
        AlertSeverity.Warning -> Color(0xFFF9A825)
        AlertSeverity.Error   -> Color(0xFFD32F2F)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        icon?.invoke()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = contentColor,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun AlertPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        Alert(
            text = "¡Éxito! Hábito creado.",
            severity = AlertSeverity.Success,
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Alert(
            text = "¡Cuidado! Faltan datos.",
            severity = AlertSeverity.Warning,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF9A825),
                    modifier = Modifier.size(24.dp)
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Alert(
            text = "Error: no se pudo guardar.",
            severity = AlertSeverity.Error,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}
