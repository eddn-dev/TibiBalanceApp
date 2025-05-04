// ui/components/PrimaryButton.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón primario reutilizable.
 *
 * @param text          Texto que se muestra.
 * @param onClick       Acción.
 * @param modifier      Modifier extra (por defecto fillMaxWidth).
 * @param enabled       Habilitado/deshabilitado.
 * @param isLoading     Muestra CircularProgressIndicator centrado e
 *                      inhabilita el click.
 * @param container     Color de fondo (por defecto primary).
 * @param contentColor  Color del texto / indicador.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    isLoading: Boolean = false,
    container: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val realEnabled = enabled && !isLoading

    Button(
        onClick = onClick,
        enabled = realEnabled,
        modifier = modifier.height(50.dp),           // alto consistente
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3EA8FE),
            contentColor   = contentColor,
            disabledContainerColor = container.copy(alpha = 0.4f),
            disabledContentColor   = contentColor.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
                color = contentColor
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable fun PrimaryButtonPreview() {
    PrimaryButton("Iniciar sesión", onClick = {})
}

@Preview(showBackground = true, widthDp = 200)
@Composable fun PrimaryButtonLoadingPreview() {
    PrimaryButton("Registrarse", onClick = {}, isLoading = true)
}
