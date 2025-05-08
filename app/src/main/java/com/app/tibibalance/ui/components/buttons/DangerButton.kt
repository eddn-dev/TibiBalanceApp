/**
 * @file    DangerButton.kt
 * @ingroup ui_component_button // Grupo específico para componentes de botón
 * @brief   Define un [Composable] para botones que indican una acción destructiva o peligrosa.
 *
 * @details Este archivo contiene el [Composable] `DangerButton`, que proporciona un botón
 * estilizado con colores que sugieren precaución (típicamente rojo). Se utiliza para
 * acciones como eliminar datos, cerrar sesión, cancelar suscripciones, etc.
 *
 * El botón maneja estados de habilitado/deshabilitado y un estado de carga,
 * mostrando un indicador de progreso circular cuando `isLoading` es `true`.
 * Permite personalizar los colores de fondo y contenido.
 * Incluye previsualizaciones para diferentes estados.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // Importar para Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que representa un botón para acciones peligrosas o destructivas.
 *
 * @details Este botón utiliza el componente [Button] de Material 3, pero aplica
 * colores específicos (rojo por defecto) para comunicar visualmente el riesgo
 * asociado a la acción.
 *
 * Maneja internamente el estado `enabled` basado tanto en el parámetro `enabled`
 * como en `isLoading` (se deshabilita si está cargando). Muestra un
 * [CircularProgressIndicator] en lugar del texto cuando `isLoading` es `true`.
 *
 * @param text El [String] que se muestra como etiqueta del botón cuando no está cargando.
 * @param onClick La función lambda que se ejecuta cuando el botón es pulsado (y está habilitado).
 * @param modifier Un [Modifier] opcional para personalizar el layout (tamaño, padding, etc.).
 * Por defecto, ocupa todo el ancho disponible (`fillMaxWidth`).
 * @param enabled Un [Boolean] que indica si el botón está habilitado para interacción.
 * Se combina con `isLoading` para determinar el estado final. Por defecto `true`.
 * @param isLoading Un [Boolean] que indica si el botón está en estado de carga.
 * Si es `true`, se muestra un indicador de progreso y el botón se deshabilita. Por defecto `false`.
 * @param container El [Color] de fondo del botón en estado normal. Por defecto, un color rojo (`0xFFFF3333`).
 * @param contentColor El [Color] del texto (o del indicador de progreso) dentro del botón.
 * Por defecto, `Color.White`.
 */
@Composable
fun DangerButton(
    text        : String,
    onClick     : () -> Unit,
    modifier    : Modifier = Modifier.fillMaxWidth(), // Default a ancho completo
    enabled     : Boolean  = true, // Habilitado por defecto
    isLoading   : Boolean  = false, // No cargando por defecto
    container   : Color    = Color(0xFFFF3333), // Color rojo por defecto
    contentColor: Color    = Color.White // Color blanco por defecto
) {
    // El botón solo está realmente habilitado si el parámetro 'enabled' es true
    // Y si no está en estado de carga 'isLoading'.
    val realEnabled = enabled && !isLoading

    Button(
        onClick  = onClick, // Acción a ejecutar al hacer clic
        enabled  = realEnabled, // Estado de habilitación calculado
        modifier = modifier.height(48.dp), // Altura fija del botón (ajustada a 48dp para mejor tactilidad)
        shape    = RoundedCornerShape(12.dp), // Bordes redondeados
        colors   = ButtonDefaults.buttonColors( // Configuración de colores
            containerColor         = container, // Color de fondo normal
            contentColor           = contentColor, // Color de texto/icono normal
            // Colores para el estado deshabilitado (más transparentes)
            disabledContainerColor = container.copy(alpha = 0.5f), // Ajustado alpha para mejor visibilidad
            disabledContentColor   = contentColor.copy(alpha = 0.5f) // Ajustado alpha
        ),
        contentPadding = PaddingValues(horizontal = 24.dp) // Espaciado interno horizontal
    ) {
        // Contenido del botón: indicador de carga o texto
        if (isLoading) {
            // Si está cargando, muestra un indicador de progreso circular
            CircularProgressIndicator(
                strokeWidth = 3.dp, // Grosor del círculo
                modifier    = Modifier.size(24.dp), // Tamaño del indicador
                color       = contentColor // Usa el color de contenido definido
            )
        } else {
            // Si no está cargando, muestra el texto
            Text(
                text  = text, // El texto proporcionado
                style = MaterialTheme.typography.labelLarge.copy( // Usar labelLarge para botones es común
                    fontWeight = FontWeight.SemiBold // Un poco más de peso
                    // fontSize = 16.sp, // El tamaño viene de labelLarge
                )
            )
        }
    }
}

// --- Previews para diferentes estados del botón ---

/**
 * @brief Previsualización del [DangerButton] en estado normal (habilitado).
 */
@Preview(showBackground = true, name = "Danger Button Enabled")
@Composable
private fun DangerButtonEnabledPreview() {
    MaterialTheme { // Envuelve en MaterialTheme para estilos correctos
        DangerButton(text = "Eliminar Cuenta", onClick = {})
    }
}

/**
 * @brief Previsualización del [DangerButton] en estado deshabilitado.
 */
@Preview(showBackground = true, name = "Danger Button Disabled")
@Composable
private fun DangerButtonDisabledPreview() {
    MaterialTheme {
        DangerButton(text = "Eliminar Cuenta", onClick = {}, enabled = false)
    }
}

/**
 * @brief Previsualización del [DangerButton] en estado de carga.
 */
@Preview(showBackground = true, name = "Danger Button Loading")
@Composable
private fun DangerButtonLoadingPreview() {
    MaterialTheme {
        DangerButton(text = "Eliminar Cuenta", onClick = {}, isLoading = true)
    }
}

/**
 * @brief Previsualización del [DangerButton] deshabilitado y cargando (mismo estado visual que solo cargando).
 */
@Preview(showBackground = true, name = "Danger Button Loading Disabled")
@Composable
private fun DangerButtonLoadingDisabledPreview() {
    MaterialTheme {
        DangerButton(text = "Eliminar Cuenta", onClick = {}, enabled = false, isLoading = true)
    }
}
