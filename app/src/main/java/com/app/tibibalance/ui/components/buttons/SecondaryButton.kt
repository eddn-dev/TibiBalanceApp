/**
 * @file    SecondaryButton.kt
 * @ingroup ui_components
 * @brief   Botón secundario de estilo *outlined*.
 *
 * Se utiliza para acciones “cancelar / volver” o aquellas con menor
 * prioridad visual respecto al **PrimaryButton**.  Presenta borde gris
 * claro, fondo blanco y texto negro, evitando robar protagonismo al
 * flujo principal.
 *
 * ### Uso típico
 * ```kotlin
 * SecondaryButton(
 *     text    = "Cancelar",
 *     onClick = navController::popBackStack
 * )
 * ```
 *
 * @note Altura fija de **40 dp** y ancho mínimo de **120 dp** para
 *       mantener consistencia con el resto del sistema de diseño.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @param text     Etiqueta mostrada.
 * @param onClick  Acción al pulsar.
 * @param modifier Modificador externo; por defecto sin `fillMaxWidth`
 *                 para respetar el ancho mínimo.
 * @param enabled  Deshabilita el botón cuando es `false`.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .width(120.dp)
            .height(40.dp),
        shape  = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor         = Color.White,
            contentColor           = Color.Black,
            disabledContainerColor = Color.White.copy(alpha = 0.4f),
            disabledContentColor   = Color.Black.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize   = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

/* ─────────────── Preview ─────────────── */

@Preview(showBackground = true)
@Composable private fun PreviewSecondaryButton() =
    SecondaryButton(text = "Cancelar", onClick = {})
