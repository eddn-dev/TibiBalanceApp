// ui/theme/Theme.kt
package com.app.tibibalance.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* ─── ColorScheme ─── */
private val AppColorScheme = lightColorScheme(
    primary       = Blue80,
    onPrimary     = Color.Black,

    secondary     = Blue40,
    onSecondary   = Color.White,

    background    = White,
    onBackground  = Color.Black,

    surface       = White,
    onSurface     = Color.Black,

    outline       = Grey10
)

/* ─── Tema raíz (solo light) ─── */
@Composable
fun TibiBalanceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}
