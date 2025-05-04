package com.app.tibibalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Campo de fecha “read-only” con subrayado y supportingText.
 *
 * @param value           Cadena ya formateada ("" si no hay fecha).
 * @param onClick         Abre el selector de fecha.
 * @param placeholder     Texto fantasma.
 * @param isError         Pinta el subrayado/ícono en rojo.
 * @param supportingText  Mensaje de ayuda o error (p.ej. “Debes tener ≥ 13 años”).
 */
@Composable
fun InputDate(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Fecha de nacimiento*",
    isError: Boolean = false,
    supportingText: String? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            /* un único hit-box accesible */
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics {
                role = Role.Button
                contentDescription = "Selector de fecha"
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* editable = false → solo para mostrar el valor */
            InputText(
                value = value,
                onValueChange = {},          // no editable
                modifier = Modifier.weight(1f),
                placeholder = placeholder,
                isError = isError,
                singleLine = true
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        supportingText?.let {
            Text(
                text = it,
                color = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
            )
        }
    }
}
