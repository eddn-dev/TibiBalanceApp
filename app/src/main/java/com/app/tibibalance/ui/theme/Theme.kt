// ui/theme/Theme.kt
package com.app.tibibalance.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme


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

private val DarkColorScheme = darkColorScheme(
    primary       = DarkPrimary,
    onPrimary     = DarkText,

    secondary     = DarkSecondary,
    onSecondary   = DarkBackground,

    background    = DarkBackground,
    onBackground  = DarkText,

    surface       = DarkSurface,
    onSurface     = DarkText,

    error         = DarkError,
    onError       = DarkText,

    outline       = DarkSecondary
)



/* ─── Tema raíz (solo light) ─── */
@Composable
fun TibiBalanceTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else AppColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}


