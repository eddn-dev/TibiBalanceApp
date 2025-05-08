/**
 * @file    DangerButton.kt
 * @ingroup ui_components
 * @brief   Botón de acción destructiva (logout, eliminar, etc.).
 *
 * Visualmente replica el estilo de **PrimaryButton**, pero utiliza una
 * paleta roja para indicar riesgo.  Permite estado de carga y
 * deshabilitación.
 *
 * @param text         Etiqueta visible del botón.
 * @param onClick      Lambda ejecutada al pulsar.
 * @param modifier     Modificador para tamaño y posición.
 * @param enabled      `false` desactiva la interacción.
 * @param isLoading    `true` muestra un `CircularProgressIndicator`.
 * @param container    Color de fondo en estado normal.
 * @param contentColor Color del texto/ícono.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DangerButton(
    text        : String,
    onClick     : () -> Unit,
    modifier    : Modifier = Modifier.fillMaxWidth(),
    enabled     : Boolean  = true,
    isLoading   : Boolean  = false,
    container   : Color    = Color(0xFFFF3333),
    contentColor: Color    = Color.White
) {
    val realEnabled = enabled && !isLoading

    Button(
        onClick  = onClick,
        enabled  = realEnabled,
        modifier = modifier.height(40.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = container,
            contentColor           = contentColor,
            disabledContainerColor = container.copy(alpha = 0.40f),
            disabledContentColor   = contentColor.copy(alpha = 0.40f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier    = Modifier.size(20.dp),
                color       = contentColor
            )
        } else {
            Text(
                text  = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
