/**
 * @file    InputText.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] genérico para campos de texto delineados ([OutlinedTextField]).
 *
 * @details Este componente reutilizable sirve como base para diferentes tipos de campos de entrada
 * de texto en la aplicación. Envuelve el [OutlinedTextField] de Material 3 y ofrece
 * una API unificada para configurar propiedades comunes como el valor, el callback
 * de cambio, placeholder (usado como etiqueta y placeholder interno), estado de error,
 * texto de soporte, modo de una o múltiples líneas, transformación visual (útil para
 * contraseñas), límite de caracteres y opciones de teclado.
 *
 * Otros componentes de entrada más específicos (como [InputEmail], [InputPassword], [InputNumber])
 * pueden reutilizar este componente base, proporcionando configuraciones predeterminadas
 * o validaciones adicionales.
 *
 * @see OutlinedTextField Componente base de Material 3 utilizado.
 * @see InputEmail Ejemplo de especialización para correos.
 * @see InputPassword Ejemplo de especialización para contraseñas.
 * @see InputNumber Ejemplo de especialización para números.
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn // Para altura mínima
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Importar remember y mutableStateOf para previews
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] genérico para campos de entrada de texto delineados.
 *
 * @details Proporciona un [OutlinedTextField] estilizado con bordes redondeados y colores
 * basados en el tema. Permite configurar múltiples aspectos como el modo de una/múltiples
 * líneas, transformación visual, límite de caracteres con contador, estado de error
 * con texto de soporte y opciones de teclado.
 *
 * @param value El valor [String] actual del campo de texto.
 * @param onValueChange La función lambda que se invoca cada vez que el texto cambia.
 * Recibe el nuevo valor [String] como parámetro. La implementación interna aplica
 * `maxChars` si está definido.
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout.
 * Por defecto, ocupa el ancho máximo y tiene una altura mínima.
 * @param placeholder El texto [String] que actúa como etiqueta flotante y como placeholder
 * cuando el campo está vacío. Por defecto `""`.
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error.
 * Por defecto `false`.
 * @param supportingText Un [String] opcional. Si `isError` es `true`, se muestra este texto
 * como mensaje de error. Si `isError` es `false` y `maxChars` está definido, se
 * muestra un contador de caracteres en su lugar. Por defecto `null`.
 * @param singleLine Un [Boolean] que determina si el campo de texto debe ser de una sola línea (`true`)
 * o puede expandirse a múltiples líneas (`false`). Por defecto `true`.
 * @param visualTransformation Una [VisualTransformation] opcional para modificar la apariencia
 * visual del texto introducido (e.g., [PasswordVisualTransformation]). Por defecto [VisualTransformation.None].
 * @param maxChars Un [Int] opcional que define el número máximo de caracteres permitidos.
 * Si no es `null`, la entrada se trunca y se muestra un contador. Por defecto `null`.
 * @param keyboardOptions Las [KeyboardOptions] para configurar el teclado virtual (tipo, acción IME, etc.).
 * Por defecto [KeyboardOptions.Default].
 */
@Composable
fun InputText(
    value               : String,
    onValueChange       : (String) -> Unit,
    modifier            : Modifier             = Modifier,
    placeholder         : String               = "", // Usado como Label y Placeholder
    isError             : Boolean              = false, // Estado de error
    supportingText      : String?              = null, // Texto de ayuda/error
    singleLine          : Boolean              = true, // Una línea por defecto
    visualTransformation: VisualTransformation = VisualTransformation.None, // Sin transformación por defecto
    maxChars            : Int?                 = null, // Sin límite por defecto
    keyboardOptions     : KeyboardOptions      = KeyboardOptions.Default // Opciones de teclado por defecto
) {
    // Define los colores del TextField usando el tema y considerando el estado de error
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor  = MaterialTheme.colorScheme.surface, // Color si estuviera deshabilitado
        // Colores de borde condicionales
        focusedBorderColor      = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error,
        // Colores de etiqueta/placeholder condicionales
        focusedLabelColor       = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error,
        // Color del texto de soporte/error
        errorSupportingTextColor = MaterialTheme.colorScheme.error
        // Otros colores (texto, cursor, etc.) usan los defaults del tema
    )

    OutlinedTextField(
        value            = value, // Valor actual
        onValueChange    = { input -> // Callback al cambiar
            // Aplica el límite de caracteres antes de llamar al callback externo
            val finalValue = if (maxChars != null) input.take(maxChars) else input
            onValueChange(finalValue)
        },
        modifier         = modifier // Aplica modificador externo
            .fillMaxWidth() // Ocupa el ancho por defecto
            .heightIn(min = 48.dp), // Altura mínima para mejor tactilidad
        label            = { Text(placeholder) }, // Usa el placeholder como etiqueta
        placeholder      = { Text(placeholder) }, // Y también como placeholder interno
        singleLine       = singleLine, // Configura si es una o varias líneas
        // Limita las líneas si no es singleLine para evitar expansión infinita
        maxLines         = if (singleLine) 1 else 4,
        visualTransformation = visualTransformation, // Aplica transformación (e.g., contraseña)
        keyboardOptions  = keyboardOptions, // Configura el teclado
        isError          = isError, // Aplica estilo de error
        supportingText   = { // Define el contenido del texto de soporte
            when {
                // Prioridad 1: Mostrar texto de error si isError es true y supportingText existe
                isError && supportingText != null -> Text(supportingText)
                // Prioridad 2: Mostrar contador si maxChars está definido (y no hay error con texto)
                maxChars != null                  -> Text("${value.length}/$maxChars")
                // No muestra nada si no se cumplen las condiciones anteriores
            }
        },
        colors           = colors, // Aplica los colores definidos
        shape            = RoundedCornerShape(12.dp) // Bordes redondeados
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [InputText] en estado normal.
 */
@Preview(showBackground = true, name = "InputText Normal")
@Composable
private fun InputTextNormalPreview() {
    var text by remember { mutableStateOf("Texto de ejemplo") }
    MaterialTheme {
        InputText(
            value = text,
            onValueChange = { text = it },
            placeholder = "Etiqueta"
        )
    }
}

/**
 * @brief Previsualización del [InputText] en estado de error.
 */
@Preview(showBackground = true, name = "InputText Error")
@Composable
private fun InputTextErrorPreview() {
    var text by remember { mutableStateOf("Valor incorrecto") }
    MaterialTheme {
        InputText(
            value = text,
            onValueChange = { text = it },
            placeholder = "Campo Obligatorio",
            isError = true,
            supportingText = "Este campo no puede estar vacío"
        )
    }
}

/**
 * @brief Previsualización del [InputText] multilínea con contador de caracteres.
 */
@Preview(showBackground = true, name = "InputText Multiline Max Chars")
@Composable
private fun InputTextMultilineMaxCharsPreview() {
    var text by remember { mutableStateOf("Este es un texto que ocupa\nvarias líneas y tiene un límite.") }
    MaterialTheme {
        InputText(
            value = text,
            onValueChange = { text = it },
            placeholder = "Comentarios",
            singleLine = false, // Permite múltiples líneas
            maxChars = 150 // Límite de 150 caracteres
        )
    }
}

/**
 * @brief Previsualización del [InputText] vacío con placeholder.
 */
@Preview(showBackground = true, name = "InputText Empty")
@Composable
private fun InputTextEmptyPreview() {
    var text by remember { mutableStateOf("") }
    MaterialTheme {
        InputText(
            value = text,
            onValueChange = { text = it },
            placeholder = "Dirección (Opcional)"
        )
    }
}
