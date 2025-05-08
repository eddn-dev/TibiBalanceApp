/**
 * @file    InputNumber.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] para un campo de texto que solo acepta dígitos numéricos.
 *
 * @details Este componente actúa como una especialización del componente genérico [InputText]
 * (se asume que `InputText` existe y maneja la apariencia base como `OutlinedTextField`).
 * La principal funcionalidad añadida aquí es la validación en `onValueChange` que
 * asegura que solo se propaguen los cambios si la nueva cadena contiene únicamente
 * caracteres de dígito (`Char::isDigit`).
 *
 * Además, configura automáticamente el [KeyboardOptions] para mostrar un teclado
 * numérico ([KeyboardType.Number]), facilitando la entrada de números por parte del usuario.
 * Es ideal para campos como edad, cantidad, código postal, etc.
 *
 * @see InputText Componente genérico reutilizado internamente (asumido).
 * @see OutlinedTextField Posible componente base de Material 3 utilizado por InputText.
 * @see KeyboardOptions
 * @see KeyboardType
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.* // Importar remember y mutableStateOf para previews
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.material3.MaterialTheme // Importar para Preview
// Importar InputText si está en otro paquete
// import com.app.tibibalance.ui.components.inputs.InputText

/**
 * @brief Un [Composable] que representa un campo de texto optimizado para la entrada de números enteros no negativos.
 *
 * @details Envuelve un componente `InputText` (asumido), configurándolo para aceptar solo dígitos
 * y mostrar el teclado numérico. Filtra la entrada del usuario en el callback `onValueChange`
 * para prevenir la introducción de caracteres no numéricos.
 *
 * @param value La [String] numérica actual que se muestra en el campo.
 * @param onValueChange La función lambda que se invoca solo cuando el texto introducido
 * por el usuario consiste únicamente en dígitos. Recibe la nueva cadena numérica válida.
 * @param placeholder El texto [String] que se muestra como pista cuando el campo está vacío.
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout
 * (padding, tamaño, etc.) desde el exterior.
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error
 * (heredado por `InputText`). Por defecto `false`.
 * @param supportingText Un [String] opcional que se muestra debajo del campo, típicamente
 * para mensajes de error cuando `isError` es `true` (heredado por `InputText`). Por defecto `null`.
 */
@Composable
fun InputNumber(
    value          : String,
    onValueChange  : (String) -> Unit,
    placeholder    : String,
    modifier       : Modifier = Modifier,
    isError        : Boolean  = false, // Pasa el estado de error
    supportingText : String?  = null // Pasa el texto de soporte/error
) {
    // Delega la renderización al componente InputText genérico
    InputText(
        value           = value, // Valor actual
        onValueChange   = { newValue -> // Callback interceptado
            // Valida si todos los caracteres en el nuevo valor son dígitos
            if (newValue.all { char -> char.isDigit() }) {
                // Si son todos dígitos, llama al callback externo
                onValueChange(newValue)
            }
            // Si no son todos dígitos, NO llama a onValueChange,
            // por lo que el estado 'value' no se actualizará y la entrada inválida se ignora.
        },
        placeholder     = placeholder, // Texto de placeholder
        // Configura el teclado para mostrar solo números
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier        = modifier, // Aplica el modificador externo
        isError         = isError, // Pasa el estado de error
        supportingText  = supportingText, // Pasa el texto de soporte
        singleLine      = true // Asegura que sea una sola línea (común para números)
        // Otros parámetros que InputText pueda tener (label, etc.) se omiten aquí
        // o se les podría dar un valor fijo si fuera necesario.
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [InputNumber] en estado normal con valor.
 */
@Preview(showBackground = true, name = "InputNumber Normal")
@Composable
private fun InputNumberPreview() {
    var text by remember { mutableStateOf("12345") }
    MaterialTheme {
        InputNumber(
            value = text,
            onValueChange = { text = it },
            placeholder = "Cantidad"
        )
    }
}

/**
 * @brief Previsualización del [InputNumber] en estado de error.
 */
@Preview(showBackground = true, name = "InputNumber Error")
@Composable
private fun InputNumberErrorPreview() {
    // Aunque el componente filtra, podemos simular un valor inválido inicial
    // o simplemente mostrar el estado de error con un valor válido.
    var text by remember { mutableStateOf("99") }
    MaterialTheme {
        InputNumber(
            value = text,
            onValueChange = { text = it },
            placeholder = "Edad",
            isError = true,
            supportingText = "Debe ser mayor de 18" // Ejemplo de mensaje de error
        )
    }
}

/**
 * @brief Previsualización del [InputNumber] vacío con placeholder.
 */
@Preview(showBackground = true, name = "InputNumber Empty")
@Composable
private fun InputNumberEmptyPreview() {
    var text by remember { mutableStateOf("") }
    MaterialTheme {
        InputNumber(
            value = text,
            onValueChange = { text = it },
            placeholder = "Código Postal"
        )
    }
}
