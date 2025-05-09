/**
 * @file    InputEmail.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] para un campo de texto [OutlinedTextField] específicamente configurado para la entrada de correos electrónicos.
 *
 * @details Este componente reutilizable proporciona un [OutlinedTextField] de Material 3
 * preconfigurado con las opciones de teclado adecuadas para la entrada de direcciones
 * de correo electrónico (`KeyboardType.Email`, `ImeAction.Done`).
 *
 * Permite la personalización de la etiqueta, el manejo de estados de error (cambiando
 * los colores y mostrando un texto de soporte), y opcionalmente, un límite máximo
 * de caracteres con un contador visual.
 *
 * @see OutlinedTextField Componente base de Material 3 utilizado.
 * @see KeyboardOptions Configuración del teclado virtual.
 * @see KeyboardType
 * @see ImeAction
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importar remember y mutableStateOf para previews
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que muestra un [OutlinedTextField] configurado para la entrada de correos electrónicos.
 *
 * @details Utiliza [OutlinedTextField] de Material 3 con bordes redondeados y colores
 * basados en el tema. Configura automáticamente el teclado para email y la acción IME a "Done".
 * Soporta estados de error visuales, texto de soporte condicional y un contador opcional
 * de caracteres si se especifica `maxChars`.
 *
 * @param value El valor [String] actual del campo de texto (el correo electrónico introducido).
 * @param onValueChange La función lambda que se invoca cada vez que el texto del campo cambia.
 * Recibe el nuevo valor [String] como parámetro. La implementación interna asegura
 * que no se exceda `maxChars` si está definido.
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout
 * (padding, tamaño, etc.) desde el exterior. Por defecto, ocupa el ancho máximo (`fillMaxWidth`).
 * @param label El texto [String] que se muestra como etiqueta flotante del campo.
 * Por defecto es "Correo electrónico".
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error
 * (e.g., borde y etiqueta en color de error). Por defecto `false`.
 * @param supportingText Un [String] opcional que se muestra debajo del campo. Si `isError` es `true`,
 * este texto se muestra (típicamente un mensaje de error). Si `isError` es `false` y
 * `maxChars` está definido, se muestra un contador de caracteres en su lugar.
 * Por defecto `null`.
 * @param maxChars Un [Int] opcional que define el número máximo de caracteres permitidos
 * en el campo. Si no es `null`, la entrada se truncará y se mostrará un contador.
 * Por defecto `null` (sin límite).
 * @param keyboardOptions Las [KeyboardOptions] para el teclado virtual. Por defecto,
 * configura [KeyboardType.Email] y [ImeAction.Done]. Se puede sobrescribir si
 * se necesita una configuración diferente (e.g., `ImeAction.Next`).
 */
@Composable
fun InputEmail(
    value          : String,
    onValueChange  : (String) -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Correo electrónico", // Etiqueta por defecto
    isError        : Boolean  = false, // Estado de error
    supportingText : String?  = null, // Texto de ayuda/error
    maxChars       : Int?     = null, // Límite opcional de caracteres
    keyboardOptions: KeyboardOptions = KeyboardOptions( // Opciones de teclado por defecto
        keyboardType = KeyboardType.Email, // Tipo de teclado para email
        imeAction    = ImeAction.Done // Acción "Done" en el teclado
    )
) {
    // Define los colores del TextField usando el tema y considerando el estado de error
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor  = MaterialTheme.colorScheme.surface,
        // Colores de borde condicionales
        focusedBorderColor      = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error,
        // Colores de etiqueta condicionales
        focusedLabelColor       = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error,
        // Color del texto de soporte cuando hay error
        errorSupportingTextColor = MaterialTheme.colorScheme.error
    )

    OutlinedTextField(
        value          = value, // Valor actual
        onValueChange  = { newValue -> // Se llama cuando el usuario escribe
            // Trunca el nuevo valor si excede maxChars (si está definido)
            val finalValue = if (maxChars != null) newValue.take(maxChars) else newValue
            onValueChange(finalValue) // Llama al callback externo con el valor final
        },
        modifier       = modifier.fillMaxWidth(), // Ocupa todo el ancho
        label          = { Text(label) }, // Muestra la etiqueta
        singleLine     = true, // Campo de una sola línea
        isError        = isError, // Aplica estilo de error si es true
        keyboardOptions = keyboardOptions, // Configura el teclado
        supportingText = { // Contenido del texto de soporte/contador
            when {
                // Si hay error y texto de soporte, muestra el texto de soporte
                isError && supportingText != null -> Text(supportingText)
                // Si no hay error pero sí límite de caracteres, muestra el contador
                maxChars != null -> Text("${value.length}/$maxChars")
                // En otro caso, no muestra nada (Composable vacío)
                // else -> {} // No es necesario el else explícito aquí
            }
        },
        shape  = RoundedCornerShape(12.dp), // Bordes redondeados
        colors = colors // Aplica los colores definidos
    )
}