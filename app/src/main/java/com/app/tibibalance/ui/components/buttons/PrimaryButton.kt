/**
 * @file    PrimaryButton.kt
 * @ingroup ui_components
 * @brief   Botón primario reutilizable con soporte para estado “cargando”.
 *
 * Este componente encapsula la directriz visual de botones principales
 * (color de marca, altura fija y bordes redondeados) y ofrece un modo
 * “loading” que bloquea la interacción mientras muestra un
 * `CircularProgressIndicator`.
 *
 * ### Ejemplo de uso
 * ```kotlin
 * PrimaryButton(
 *     text      = "Guardar",
 *     isLoading = uiState.saving,
 *     onClick   = viewModel::save
 * )
 * ```
 *
 * @note
 * - Emplea el sistema de colores de **Material 3**; se puede sobrescribir el
 *   color de contenedor con el parámetro `container`.
 * - El alto se fija en **40 dp** para mantener consistencia en toda la app.
 *
 * @see DangerButton  Para acciones destructivas (borrar, cerrar sesión).
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.*
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
 * @param text         Etiqueta mostrada en el botón.
 * @param onClick      Callback al pulsar (solo si `isLoading == false`).
 * @param modifier     Modificador externo (por defecto `fillMaxWidth()`).
 * @param enabled      Habilita/deshabilita el componente.  \
 *                     Se ignora cuando `isLoading == true`.
 * @param isLoading    Muestra un spinner y bloquea la acción.
 * @param container    Color de fondo.  \
 *                     Valor por defecto: `MaterialTheme.colorScheme.primary`.
 * @param contentColor Color del texto/ícono/spinner.  \
 *                     Valor por defecto: `MaterialTheme.colorScheme.onPrimary`.
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
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
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
                    fontWeight = FontWeight.Medium,
                    color      = Color.White
                )
            )
        }
    }
}

/* ─────────────────────────── Previews ─────────────────────────── */

@Preview(showBackground = true, widthDp = 200)
@Composable private fun PrimaryButtonPreview() =
    PrimaryButton("Iniciar sesión", onClick = {})

@Preview(showBackground = true, widthDp = 200)
@Composable private fun PrimaryButtonLoadingPreview() =
    PrimaryButton("Registrando…", onClick = {}, isLoading = true)
