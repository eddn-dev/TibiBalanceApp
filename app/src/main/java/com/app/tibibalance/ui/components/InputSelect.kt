package com.app.tibibalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Selector simple basado en OutlinedTextField + DropdownMenu.
 *
 * @param options          Lista de opciones visibles.
 * @param selectedOption   Elemento actualmente seleccionado.
 * @param onOptionSelected Callback que devuelve la opción elegida.
 * @param label            Texto del label/flotante.
 * @param isError          Dibuja el borde en rojo.
 * @param supportingText   Mensaje de ayuda / error debajo del campo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSelect(
    options         : List<String>,
    selectedOption  : String,
    onOptionSelected: (String) -> Unit,
    label           : String = "",
    isError         : Boolean = false,
    supportingText  : String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    /* Contenedor especializado que se encarga del anclaje y la gestión del estado */
    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded }            // abre / cierra al tocar el campo
    ) {

        /* -------- Campo visible -------- */
        OutlinedTextField(
            value          = selectedOption,
            onValueChange  = {},                // read-only
            readOnly       = true,
            label          = { if (label.isNotEmpty()) Text(label) },
            trailingIcon   = {                  // icono que rota automáticamente
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
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
            modifier       = Modifier
                .fillMaxWidth()
                .menuAnchor()                   // 📌 desde Compose 1.6; ignóralo si tu versión es <1.6
        )

        /* -------- Menú desplegable -------- */
        ExposedDropdownMenu(                    // usa el scoped menú del box
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

