/* ui/components/InputDate.kt */
package com.app.tibibalance.ui.components

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
import androidx.compose.ui.unit.dp

@Composable
fun InputDate(
    value          : String,
    onClick        : () -> Unit,
    modifier       : Modifier = Modifier,
    label          : String   = "Fecha de nacimiento*",
    isError        : Boolean  = false,
    supportingText : String?  = null
) {

    val colors = OutlinedTextFieldDefaults.colors(
        /* --- contenedor --- */
        focusedContainerColor   = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor  = Color.White,

        /* --- bordes --- */
        focusedBorderColor      = if (isError)
            MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = if (isError)
            MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.outline,
        errorBorderColor        = MaterialTheme.colorScheme.error,

        /* --- label y texto --- */
        focusedLabelColor       = if (isError)
            MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor     = if (isError)
            MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor         = MaterialTheme.colorScheme.error,

        /* texto / iconos dentro del campo */
        disabledTextColor       = MaterialTheme.colorScheme.onSurface,         // legible
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )


    val interaction = remember { MutableInteractionSource() }

    /* ① Contenedor que recibe el click */
    Box(
        modifier
            .fillMaxWidth()
            .clickable(                       // toda la fila es “botón”
                interactionSource = interaction,
                indication        = null,     // sin ripple para que no se superponga
                role              = Role.Button,
                onClick           = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "Selector de fecha"
            }
    ) {
        /* ② `OutlinedTextField` puro display: enabled = false → sin foco ni teclado */
        OutlinedTextField(
            value           = value,
            onValueChange   = {},             // no editable
            modifier        = Modifier.fillMaxWidth(),
            enabled         = false,
            readOnly        = true,
            singleLine      = true,
            label           = { Text(label) },
            trailingIcon    = {
                val tint: Color = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Icon(
                    imageVector       = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint              = tint
                )
            },
            isError         = isError,
            supportingText  = supportingText?.let {
                { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            shape  = RoundedCornerShape(12.dp),
            colors = colors
        )
    }
}
