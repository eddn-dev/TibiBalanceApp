/**
 * @file    InputSelect.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] que simula un campo de selección (dropdown/spinner)
 * utilizando componentes de Material 3.
 *
 * @details Este componente reutilizable presenta la apariencia de un [OutlinedTextField]
 * pero actúa como un selector desplegable. Utiliza [ExposedDropdownMenuBox]
 * para gestionar el estado de expansión y mostrar un menú ([ExposedDropdownMenu])
 * con las opciones disponibles ([DropdownMenuItem]).
 *
 * El [OutlinedTextField] interno es de solo lectura (`readOnly = true`) y muestra
 * la opción actualmente seleccionada. Al pulsar sobre él, se expande o colapsa
 * el menú desplegable. Al seleccionar una opción del menú, se llama al callback
 * `onOptionSelected` y el menú se cierra.
 *
 * Soporta estados de error visuales y texto de soporte/error.
 *
 * @see ExposedDropdownMenuBox Contenedor principal para este patrón de UI.
 * @see OutlinedTextField Usado para mostrar la selección actual y como ancla del menú.
 * @see ExposedDropdownMenu Menú que contiene las opciones.
 * @see DropdownMenuItem Elemento individual seleccionable en el menú.
 * @see ExperimentalMaterial3Api Necesario para usar ExposedDropdownMenuBox.
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que implementa un campo de selección estilo dropdown/spinner.
 *
 * @details Utiliza el patrón [ExposedDropdownMenuBox] de Material 3 para crear un selector
 * que se parece a un [OutlinedTextField] pero despliega una lista de opciones al ser pulsado.
 * El campo de texto es de solo lectura y muestra la opción seleccionada.
 *
 * @param options La [List] de [String] que representa las opciones disponibles para seleccionar en el menú desplegable.
 * @param selectedOption El [String] de la opción actualmente seleccionada, que se mostrará en el campo de texto.
 * @param onOptionSelected La función lambda que se invoca cuando el usuario selecciona una nueva opción
 * del menú desplegable. Recibe el [String] de la opción seleccionada como parámetro.
 * @param label El texto [String] opcional que se muestra como etiqueta flotante del campo. Por defecto `""`.
 * @param isError Un [Boolean] que indica si el campo debe mostrarse en estado de error. Por defecto `false`.
 * @param supportingText Un [String] opcional que se muestra debajo del campo, típicamente para mensajes de error
 * cuando `isError` es `true`. Por defecto `null`.
 * @param modifier Un [Modifier] opcional para aplicar al [ExposedDropdownMenuBox] contenedor,
 * permitiendo personalizar layout, padding, etc.
 */
@OptIn(ExperimentalMaterial3Api::class) // Indica el uso de APIs experimentales de Material 3
@Composable
fun InputSelect(
    options         : List<String>, // Lista de opciones a mostrar
    selectedOption  : String, // Opción actualmente seleccionada
    onOptionSelected: (String) -> Unit, // Callback al seleccionar una opción
    label           : String   = "", // Etiqueta opcional
    isError         : Boolean  = false, // Estado de error
    supportingText  : String?  = null, // Texto de soporte/error opcional
    modifier        : Modifier = Modifier // Modificador para el contenedor Box
) {
    // Estado para controlar si el menú desplegable está visible (expandido)
    var expanded by remember { mutableStateOf(false) }

    // Contenedor que maneja la lógica de expansión del menú
    ExposedDropdownMenuBox(
        expanded         = expanded, // Pasa el estado actual
        onExpandedChange = { expanded = !expanded }, // Alterna el estado al interactuar con el Box
        modifier = modifier // Aplica el modificador externo al Box
    ) {
        // Campo de texto que muestra la selección y sirve de ancla para el menú
        OutlinedTextField(
            modifier = Modifier // Modificador específico para el TextField
                .fillMaxWidth() // Ocupa el ancho disponible
                .menuAnchor(), // ¡Importante! Vincula este TextField al menú desplegable
            readOnly       = true, // El usuario no puede escribir directamente
            value          = selectedOption, // Muestra la opción seleccionada
            onValueChange  = {}, // No hace nada, es readOnly
            label          = { if (label.isNotEmpty()) Text(label) }, // Muestra la etiqueta si existe
            // Icono al final que indica si el menú está desplegado (flecha arriba/abajo)
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // Colores estándar del tema, considerando el estado de error
            colors         = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor  = MaterialTheme.colorScheme.surface,
                focusedBorderColor      = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor    = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                errorBorderColor        = MaterialTheme.colorScheme.error,
                focusedLabelColor       = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor     = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                errorLabelColor         = MaterialTheme.colorScheme.error,
                errorSupportingTextColor= MaterialTheme.colorScheme.error
            ),
            isError        = isError, // Aplica estilo de error si es true
            // Muestra texto de soporte/error si se proporciona
            supportingText = { supportingText?.let { Text(it) } },
            shape          = RoundedCornerShape(12.dp), // Bordes redondeados
            singleLine     = true // Asegura una sola línea
        )

        // El menú desplegable que contiene las opciones
        ExposedDropdownMenu(
            expanded         = expanded, // Se muestra u oculta según el estado 'expanded'
            onDismissRequest = { expanded = false } // Se llama cuando se pide cerrar el menú (e.g., clic fuera)
            // modifier = Modifier.exposedDropdownSize() // Opcional: Para ajustar tamaño automáticamente
        ) {
            // Itera sobre la lista de opciones
            options.forEach { selectionOption ->
                // Crea un elemento de menú para cada opción
                DropdownMenuItem(
                    text = { Text(selectionOption) }, // Muestra el texto de la opción
                    onClick = { // Acción al hacer clic en esta opción
                        onOptionSelected(selectionOption) // Llama al callback con la opción elegida
                        expanded = false // Cierra el menú
                    }
                    // contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding // Padding por defecto
                )
            }
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [InputSelect] en estado normal.
 */
@Preview(showBackground = true, name = "InputSelect Normal")
@Composable
private fun InputSelectPreview() {
    val options = listOf("Opción A", "Opción B", "Opción C con texto largo")
    // Simula el estado de la opción seleccionada
    var selected by remember { mutableStateOf(options[1]) }
    MaterialTheme {
        InputSelect(
            options = options,
            selectedOption = selected,
            onOptionSelected = { selected = it },
            label = "Elige una opción",
            modifier = Modifier.padding(16.dp) // Añade padding para ver mejor
        )
    }
}

/**
 * @brief Previsualización del [InputSelect] en estado de error.
 */
@Preview(showBackground = true, name = "InputSelect Error")
@Composable
private fun InputSelectErrorPreview() {
    val options = listOf("Válido 1", "Válido 2")
    // Simula una selección inicial o un estado que requiere validación
    var selected by remember { mutableStateOf("Válido 1") }
    MaterialTheme {
        InputSelect(
            options = options,
            selectedOption = selected,
            onOptionSelected = { selected = it },
            label = "Estado",
            isError = true, // Marca como error
            supportingText = "Selección requerida", // Muestra mensaje de error
            modifier = Modifier.padding(16.dp)
        )
    }
}
