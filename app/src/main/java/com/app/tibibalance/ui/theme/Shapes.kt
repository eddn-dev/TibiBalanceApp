// ui/theme/Shapes.kt
package com.app.tibibalance.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Conjunto de esquinas redondeadas que toda la app compartirá.
 * Ajusta los radios a tu gusto o deja los valores por defecto.
 */
val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small      = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium     = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large      = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
)
