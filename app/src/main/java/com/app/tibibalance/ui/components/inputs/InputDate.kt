/**
 * @file    InputDate.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] que simula un campo de entrada de fecha pero actúa como un botón para abrir un selector.
 *
 * @details Este componente presenta la apariencia de un [OutlinedTextField] estándar,
 * mostrando una fecha formateada y una etiqueta. Sin embargo, el `TextField` interno
 * está deshabilitado (`enabled = false`) para prevenir el foco y la aparición
 * del teclado virtual.
 *
 * En su lugar, todo el componente está envuelto en un [Box] que captura los clics
 * (`clickable` modifier). Al pulsar en cualquier parte del área del componente,
 * se invoca la lambda `onClick` proporcionada. Esta lambda es la responsable de
 * lanzar el diálogo o pantalla del selector de fecha (`DatePickerDialog`, etc.).
 *
 * El componente también soporta un estado de error (`isError`) que cambia los colores
 * del borde, la etiqueta y el icono, y opcionalmente muestra un texto de ayuda/error
 * (`supportingText`). Incluye un icono de calendario como `trailingIcon`.
 *
 * @see OutlinedTextField Componente base de Material 3 utilizado para la apariencia.
 * @see Box Contenedor utilizado para hacer clicable toda el área.
 * @see Modifier.clickable Modificador que detecta los clics.
 * @see Icons.Filled.CalendarToday Icono mostrado.
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview // Importar para Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que funciona como un campo de entrada de fecha de solo lectura
 * y que, al ser pulsado, invoca una acción para mostrar un selector de fecha.
 *
 * @details Simula un `OutlinedTextField` pero está deshabilitado para la entrada directa.
 * En su lugar, un [Box] envolvente con un modificador `clickable` captura los taps
 * y ejecuta la lambda [onClick]. Muestra un icono de calendario y soporta
 * estados de error visuales.
 *
 * @param value La [String] que representa la fecha formateada actualmente seleccionada
 * (e.g., "dd/MM/yyyy"). Se muestra dentro del campo.
 * @param onClick La función lambda que se ejecuta cuando el usuario pulsa sobre el componente.
 * Esta función debe encargarse de mostrar el selector de fecha (e.g., un `DatePickerDialog`).
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout
 * (padding, tamaño, etc.) al [Box] contenedor.
 * @param label El texto [String] que se muestra como etiqueta flotante del campo.
 * Por defecto es "Fecha de nacimiento*".
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error
 * (e.g., borde y etiqueta en color de error). Por defecto `false`.
 * @param supportingText Un [String] opcional que se muestra debajo del campo únicamente
 * cuando [isError] es `true`. Útil para mostrar mensajes de validación. Por defecto `null`.
 */
@Composable
fun InputDate(
    value          : String,
    onClick        : () -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Fecha de nacimiento*", // Etiqueta por defecto
    isError        : Boolean  = false, // Sin error por defecto
    supportingText : String?  = null // Sin texto de soporte por defecto
) {
    // Define los colores personalizados para el TextField, considerando el estado de error
    val colors = OutlinedTextFieldDefaults.colors(
        // Colores de fondo (siempre blanco para mantener consistencia visual)
        focusedContainerColor   = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor  = Color.White, // Clave para el estado deshabilitado

        /* Colores de Bordes */
        focusedBorderColor      = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error, // Usado cuando isError = true

        /* Colores de Etiquetas */
        focusedLabelColor       = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error, // Usado cuando isError = true

        /* Colores de Contenido (en estado deshabilitado) */
        disabledTextColor         = MaterialTheme.colorScheme.onSurface, // Color del texto 'value'
        // Color del icono final (calendario), cambia si hay error
        disabledTrailingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Fuente de interacción para el Box clickable (aunque la indicación visual está desactivada)
    val interactionSource = remember { MutableInteractionSource() }

    // Box actúa como el área clicable que envuelve el TextField visual
    Box(
        modifier = modifier // Aplica modificador externo
            .fillMaxWidth() // Ocupa el ancho disponible
            .clickable( // Hace que el Box responda a clics
                interactionSource = interactionSource,
                indication        = null, // Desactiva el efecto ripple visual
                role              = Role.Button, // Semántica para accesibilidad: actúa como botón
                onClick           = onClick // Llama a la función proporcionada al hacer clic
            )
            .semantics { // Propiedades semánticas adicionales
                role = Role.Button // Confirma el rol
                // Describe la acción para lectores de pantalla
                contentDescription = "Abrir selector de fecha para $label. Fecha actual: $value"
            }
    ) {
        // TextField interno, solo para apariencia visual, no funcional para entrada
        OutlinedTextField(
            value          = value, // Muestra la fecha formateada
            onValueChange  = {},    // Lambda vacía, no se puede cambiar el valor directamente
            modifier       = Modifier.fillMaxWidth(), // Ocupa el ancho del Box
            enabled        = false, // Deshabilitado para prevenir interacción directa y teclado
            readOnly       = true,  // Refuerza que es de solo lectura
            singleLine     = true,  // Para que el texto no haga wrap
            label          = { Text(label) }, // Etiqueta del campo
            trailingIcon   = { // Icono al final del campo
                // Usa el color calculado anteriormente
                val iconTint = colors.disabledTrailingIconColor
                Icon(
                    imageVector = Icons.Filled.CalendarToday, // Icono de calendario
                    contentDescription = null, // Descripción nula, el Box ya describe la acción
                    tint = iconTint // Aplica el tinte correcto (error o normal)
                )
            },
            isError        = isError, // Pasa el estado de error al TextField para estilo visual
            // Muestra el texto de soporte solo si hay error y se proporcionó el texto
            supportingText = if (isError && supportingText != null) {
                { Text(text = supportingText, color = MaterialTheme.colorScheme.error) }
            } else null,
            shape  = RoundedCornerShape(12.dp), // Bordes redondeados
            colors = colors // Aplica la configuración de colores personalizada
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [InputDate] en estado normal.
 */
@Preview(showBackground = true, name = "InputDate Normal")
@Composable
private fun InputDatePreview() {
    MaterialTheme {
        InputDate(
            value = "08/05/2025",
            onClick = {},
            label = "Fecha de Inicio"
        )
    }
}

/**
 * @brief Previsualización del [InputDate] en estado de error.
 */
@Preview(showBackground = true, name = "InputDate Error")
@Composable
private fun InputDateErrorPreview() {
    MaterialTheme {
        InputDate(
            value = "Fecha inválida",
            onClick = {},
            label = "Fecha de Nacimiento*",
            isError = true,
            supportingText = "El formato debe ser dd/MM/yyyy."
        )
    }
}
