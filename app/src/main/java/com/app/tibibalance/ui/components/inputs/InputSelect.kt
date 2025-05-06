/* ui/components/InputSelect.kt */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Selector simple basado en `OutlinedTextField` + `ExposedDropdownMenu`.
 *
 * @param options          Lista de opciones visibles.
 * @param selectedOption   Elemento actualmente seleccionado.
 * @param onOptionSelected Callback que devuelve la opción elegida.
 * @param label            Texto del label/flotante.
 * @param isError          Dibuja el borde en rojo.
 * @param supportingText   Mensaje de ayuda / error debajo del campo.
 * @param modifier         Modificador exterior (ancho, padding, etc.).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSelect(
    options         : List<String>,
    selectedOption  : String,
    onOptionSelected: (String) -> Unit,
    label           : String   = "",
    isError         : Boolean  = false,
    supportingText  : String?  = null,
    modifier        : Modifier = Modifier       // ← nuevo
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        /* ---------- Campo visible ---------- */
        OutlinedTextField(
            value          = selectedOption,
            onValueChange  = {},           // read-only
            readOnly       = true,
            label          = { if (label.isNotEmpty()) Text(label) },
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            isError        = isError,
            supportingText = { supportingText?.let { Text(it) } },
            colors         = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor  = MaterialTheme.colorScheme.surface,
                focusedBorderColor      = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outline
            ),
            shape          = RoundedCornerShape(12.dp),
            singleLine     = true,
            modifier       = modifier       // usa el que recibe el llamador
                .fillMaxWidth()
                .menuAnchor()
        )

        /* ---------- Menú desplegable ---------- */
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
