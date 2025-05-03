// ui/theme/Theme.kt
package com.app.tibibalance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* ─── ColorScheme Light ─── */
private val LightColors = lightColorScheme(
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

/* ─── ColorScheme Dark ─── */
private val DarkColors = darkColorScheme(
    primary       = Blue80,
    onPrimary     = Color.Black,

    secondary     = Blue40,
    onSecondary   = Color.White,

    background    = Grey90,
    onBackground  = White,

    surface       = Grey90,
    onSurface     = White,

    outline       = Blue40
)

/* ─── Tema raíz ─── */
@Composable
fun TibiBalanceTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}
