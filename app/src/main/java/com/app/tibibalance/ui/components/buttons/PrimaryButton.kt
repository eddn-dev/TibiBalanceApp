/**
 * @file    PrimaryButton.kt
 * @ingroup ui_component_button // Grupo específico para componentes de botón
 * @brief   Define un [Composable] para el botón de acción principal de la aplicación.
 *
 * @details Este archivo contiene el [Composable] `PrimaryButton`, que representa el estilo
 * estándar para las acciones primarias (confirmar, guardar, iniciar sesión, etc.).
 * Utiliza los colores primarios del tema de Material 3 por defecto (`primary` y `onPrimary`)
 * pero permite personalización.
 *
 * Incluye soporte para un estado de carga (`isLoading`), durante el cual muestra un
 * [CircularProgressIndicator] y se deshabilita automáticamente. También respeta el
 * parámetro `enabled`.
 *
 * ### Ejemplo de uso
 * ```kotlin
 * PrimaryButton(
 * text      = "Guardar Cambios",
 * isLoading = viewModel.isSaving.collectAsState().value,
 * onClick   = { viewModel.saveChanges() }
 * )
 * ```
 *
 * @note
 * - Utiliza [MaterialTheme.colorScheme] para los colores por defecto, asegurando
 * consistencia con el tema de la aplicación. Se pueden sobrescribir con los
 * parámetros `container` y `contentColor`.
 * - Tiene una altura fija de **40dp** y bordes redondeados de **12dp**.
 * - El estilo del texto usa `MaterialTheme.typography.titleMedium` pero **fija el color a blanco**,
 * ignorando el parámetro `contentColor` para el texto (sí lo usa para el spinner).
 *
 * @see DangerButton Botón similar pero estilizado para acciones destructivas.
 * @see Button Componente base de Material 3 utilizado internamente.
 * @see CircularProgressIndicator Indicador mostrado durante el estado de carga.
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
 * @brief Un [Composable] que representa el botón de acción principal estándar de la aplicación.
 *
 * @details Este botón reutilizable aplica el estilo primario definido (colores del tema,
 * forma redondeada, altura fija). Maneja estados de habilitación y carga.
 * Cuando `isLoading` es `true`, muestra un [CircularProgressIndicator] y se deshabilita,
 * independientemente del valor de `enabled`.
 *
 * @param text El [String] que se muestra como etiqueta del botón cuando no está en estado de carga.
 * @param onClick La función lambda que se invoca cuando el usuario pulsa el botón (solo si está habilitado).
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout (padding, tamaño, etc.)
 * desde el exterior. Por defecto, ocupa el ancho máximo (`fillMaxWidth`).
 * @param enabled Un [Boolean] que controla si el botón está habilitado para la interacción del usuario.
 * Este valor se ignora (el botón se deshabilita) si `isLoading` es `true`. Por defecto `true`.
 * @param isLoading Un [Boolean] que indica si el botón debe mostrar un estado de carga.
 * Si es `true`, se muestra un [CircularProgressIndicator] en lugar del texto y el botón se deshabilita.
 * Por defecto `false`.
 * @param container El [Color] de fondo del botón. Por defecto, usa el color primario del tema
 * (`MaterialTheme.colorScheme.primary`).
 * @param contentColor El [Color] para el contenido del botón (texto y/o indicador de progreso).
 * Por defecto, usa el color "sobre primario" del tema (`MaterialTheme.colorScheme.onPrimary`).
 * **Nota:** La implementación actual aplica este color al `CircularProgressIndicator` pero
 * hardcodea `Color.White` para el `Text`.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(), // Ocupa ancho máximo por defecto
    enabled: Boolean = true, // Habilitado por defecto
    isLoading: Boolean = false, // No cargando por defecto
    container: Color = MaterialTheme.colorScheme.primary, // Color primario del tema
    contentColor: Color = MaterialTheme.colorScheme.onPrimary // Color sobre primario
) {
    // Determina si el botón está realmente habilitado
    val realEnabled = enabled && !isLoading

    Button(
        onClick = onClick, // Acción al pulsar
        enabled = realEnabled, // Estado de habilitación efectivo
        modifier = modifier.height(48.dp), // Altura fija (ajustada a 48dp)
        shape = RoundedCornerShape(12.dp), // Bordes redondeados
        colors = ButtonDefaults.buttonColors( // Colores del botón
            containerColor         = container, // Fondo normal
            contentColor           = contentColor, // Contenido normal
            disabledContainerColor = container.copy(alpha = 0.5f), // Fondo deshabilitado (más transparente)
            disabledContentColor   = contentColor.copy(alpha = 0.5f) // Contenido deshabilitado
        ),
        contentPadding = PaddingValues(horizontal = 24.dp) // Padding interno
    ) {
        // Contenido condicional: Indicador de carga o Texto
        if (isLoading) {
            // Muestra el indicador si está cargando
            CircularProgressIndicator(
                strokeWidth = 3.dp, // Grosor del indicador
                modifier    = Modifier.size(24.dp), // Tamaño del indicador
                color       = contentColor // Color del indicador
            )
        } else {
            // Muestra el texto si no está cargando
            Text(
                text  = text,
                style = MaterialTheme.typography.labelLarge.copy( // Estilo de texto (usando labelLarge)
                    fontWeight = FontWeight.SemiBold,
                    // ¡Atención! El color del texto está fijo a blanco aquí,
                    // ignorando el parámetro 'contentColor'.
                    color      = Color.White
                )
            )
        }
    }
}

/* ─────────────────────────── Previews ─────────────────────────── */

/**
 * @brief Previsualización del [PrimaryButton] en estado normal y habilitado.
 */
@Preview(showBackground = true, name = "Primary Button Enabled", widthDp = 200)
@Composable
private fun PrimaryButtonPreview() {
    MaterialTheme { // Envuelve en MaterialTheme para que los colores por defecto funcionen
        PrimaryButton("Iniciar sesión", onClick = {})
    }
}

/**
 * @brief Previsualización del [PrimaryButton] en estado de carga.
 */
@Preview(showBackground = true, name = "Primary Button Loading", widthDp = 200)
@Composable
private fun PrimaryButtonLoadingPreview() {
    MaterialTheme {
        PrimaryButton("Registrando…", onClick = {}, isLoading = true)
    }
}

/**
 * @brief Previsualización del [PrimaryButton] en estado deshabilitado.
 */
@Preview(showBackground = true, name = "Primary Button Disabled", widthDp = 200)
@Composable
private fun PrimaryButtonDisabledPreview() {
    MaterialTheme {
        PrimaryButton("Iniciar sesión", onClick = {}, enabled = false)
    }
}

/**
 * @brief Previsualización del [PrimaryButton] deshabilitado y cargando (mismo estado visual que solo cargando).
 */
@Preview(showBackground = true, name = "Primary Button Loading Disabled", widthDp = 200)
@Composable
private fun PrimaryButtonLoadingDisabledPreview() {
    MaterialTheme {
        PrimaryButton("Iniciar sesión", onClick = {}, enabled = false, isLoading = true)
    }
}
