/**
 * @file    InputPassword.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] para un campo de texto [OutlinedTextField] específicamente configurado para la entrada de contraseñas.
 *
 * @details Este componente reutilizable proporciona un [OutlinedTextField] de Material 3
 * con características adecuadas para la entrada segura de contraseñas:
 * - Configura el teclado virtual para tipo contraseña ([KeyboardType.Password]).
 * - Utiliza [PasswordVisualTransformation] para ocultar los caracteres introducidos por defecto.
 * - Incluye un icono al final del campo ([Icons.Filled.Visibility] / [Icons.Filled.VisibilityOff])
 * que permite al usuario alternar la visibilidad de la contraseña.
 * - Gestiona internamente el estado de visibilidad de la contraseña.
 * - Soporta estados de error, texto de soporte y límite de caracteres opcional.
 *
 * @see OutlinedTextField Componente base de Material 3 utilizado.
 * @see PasswordVisualTransformation Transformación visual para ocultar texto.
 * @see KeyboardOptions
 * @see KeyboardType
 * @see Icons.Filled.Visibility
 * @see Icons.Filled.VisibilityOff
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que representa un campo de texto seguro para la entrada de contraseñas.
 *
 * @details Utiliza [OutlinedTextField] con [PasswordVisualTransformation] para ocultar
 * la entrada por defecto. Incluye un [IconButton] con un icono de ojo (visible/oculto)
 * como `trailingIcon` para permitir al usuario alternar la visibilidad del texto introducido.
 * Configura el teclado para contraseñas y soporta estados de error, texto de soporte
 * y límite de caracteres.
 *
 * @param value El valor [String] actual del campo (la contraseña introducida).
 * @param onValueChange La función lambda que se invoca cada vez que el texto del campo cambia.
 * Recibe el nuevo valor [String] como parámetro. La implementación interna asegura
 * que no se exceda `maxChars` si está definido.
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout
 * (padding, tamaño, etc.) desde el exterior. Por defecto, ocupa el ancho máximo (`fillMaxWidth`).
 * @param label El texto [String] que se muestra como etiqueta flotante del campo.
 * Por defecto es "Contraseña".
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error
 * (e.g., borde y etiqueta en color de error). Por defecto `false`.
 * @param supportingText Un [String] opcional que se muestra debajo del campo. Si `isError` es `true`,
 * este texto se muestra (típicamente un mensaje de error). Si `isError` es `false` y
 * `maxChars` está definido, se muestra un contador de caracteres en su lugar.
 * Por defecto `null`.
 * @param maxChars Un [Int] opcional que define el número máximo de caracteres permitidos.
 * Si no es `null`, la entrada se truncará y se mostrará un contador. Por defecto `null`.
 * @param keyboardOptions Las [KeyboardOptions] para el teclado virtual. Por defecto,
 * configura [KeyboardType.Password] y [ImeAction.Done]. Se puede sobrescribir.
 */
@Composable
fun InputPassword(
    value          : String,
    onValueChange  : (String) -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Contraseña", // Etiqueta por defecto
    isError        : Boolean  = false, // Estado de error
    supportingText : String?  = null, // Texto de soporte/error opcional
    maxChars       : Int?     = null, // Límite de caracteres opcional
    keyboardOptions: KeyboardOptions = KeyboardOptions( // Opciones de teclado por defecto
        keyboardType = KeyboardType.Password, // Teclado específico para contraseñas
        imeAction    = ImeAction.Done // Acción "Done" por defecto
    )
) {
    // Estado interno para controlar si la contraseña es visible o no
    var passwordVisible by remember { mutableStateOf(false) }

    // Define los colores del TextField usando el tema y considerando el estado de error
    // (Misma lógica de colores que InputEmail/InputText para consistencia)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor   = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor  = MaterialTheme.colorScheme.surface,
        focusedBorderColor      = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error,
        focusedLabelColor       = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        // Colores para el icono de visibilidad
        focusedTrailingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorTrailingIconColor = MaterialTheme.colorScheme.error
    )

    OutlinedTextField(
        value            = value, // Valor actual de la contraseña
        onValueChange    = { newValue -> // Callback al cambiar el texto
            // Aplica límite de caracteres si está definido
            val finalValue = if (maxChars != null) newValue.take(maxChars) else newValue
            onValueChange(finalValue) // Notifica el cambio
        },
        modifier         = modifier.fillMaxWidth(), // Ocupa el ancho
        label            = { Text(label) }, // Etiqueta
        singleLine       = true, // Una sola línea
        isError          = isError, // Estado de error
        // Aplica la transformación visual para ocultar/mostrar contraseña
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions  = keyboardOptions, // Opciones de teclado (Password, Done)
        trailingIcon     = { // Icono al final del campo
            // Determina qué icono mostrar (ojo abierto o cerrado)
            val image = if (passwordVisible)
                Icons.Filled.VisibilityOff
            else Icons.Filled.Visibility
            // Descripción para accesibilidad que cambia según el estado
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            // Color del icono (cambia si hay error)
            val tint = colors.unfocusedTrailingIconColor // Usa el color definido en colors

            // Botón para alternar la visibilidad
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description, tint = tint)
            }
        },
        supportingText   = { // Texto debajo del campo
            when {
                // Muestra texto de error si aplica
                isError && supportingText != null -> Text(supportingText)
                // Muestra contador si aplica
                maxChars != null -> Text("${value.length}/$maxChars")
                // No muestra nada si no hay error ni contador
            }
        },
        shape  = RoundedCornerShape(12.dp), // Bordes redondeados
        colors = colors // Aplica los colores personalizados
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [InputPassword] con texto visible.
 */
@Preview(showBackground = true, name = "InputPassword Visible")
@Composable
private fun InputPasswordVisiblePreview() {
    var text by remember { mutableStateOf("Contraseña123") }
    // Para mostrarla visible, necesitaríamos controlar el estado 'visible' desde aquí,
    // pero como es interno, esta preview mostrará el estado inicial (oculto).
    // Una forma de verlo visible sería modificar temporalmente el estado inicial:
    // var passwordVisible by remember { mutableStateOf(true) } // <-- Cambio temporal para preview
    MaterialTheme {
        InputPassword(value = text, onValueChange = { text = it })
    }
    // Recordar quitar el cambio temporal después de la preview.
}

/**
 * @brief Previsualización del [InputPassword] con texto oculto (estado inicial).
 */
@Preview(showBackground = true, name = "InputPassword Hidden")
@Composable
private fun InputPasswordHiddenPreview() {
    var text by remember { mutableStateOf("ContraseñaSecreta") }
    MaterialTheme {
        InputPassword(value = text, onValueChange = { text = it })
    }
}

/**
 * @brief Previsualización del [InputPassword] en estado de error.
 */
@Preview(showBackground = true, name = "InputPassword Error")
@Composable
private fun InputPasswordErrorPreview() {
    var text by remember { mutableStateOf("pass") }
    MaterialTheme {
        InputPassword(
            value = text,
            onValueChange = { text = it },
            isError = true,
            supportingText = "La contraseña debe tener al menos 8 caracteres"
        )
    }
}

/**
 * @brief Previsualización del [InputPassword] con límite de caracteres.
 */
@Preview(showBackground = true, name = "InputPassword Max Chars")
@Composable
private fun InputPasswordMaxCharsPreview() {
    var text by remember { mutableStateOf("123456789012345") }
    MaterialTheme {
        InputPassword(
            value = text,
            onValueChange = { text = it },
            maxChars = 15 // Límite de 15 caracteres
        )
    }
}
