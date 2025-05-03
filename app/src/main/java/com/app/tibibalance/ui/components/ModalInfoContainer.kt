package com.app.tibibalance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

