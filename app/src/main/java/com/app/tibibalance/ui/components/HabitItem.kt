// ui/components/HabitItem.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R

/**
 * Fila de hábito con icono de edición, contenedor blanco, texto y switch.
 *
 * @param iconRes           Drawable resource del icono del hábito.
 * @param label             Texto descriptivo del hábito.
 * @param checked           Estado del switch.
 * @param onCheckedChange   Callback al cambiar el switch.
 * @param onEdit            Callback al pulsar el icono de editar.
 */
@Composable
fun HabitItem(
    iconRes: Int,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de edición a la izquierda
        ImageContainer(
            resId              = R.drawable.iconeditimage,
            contentDescription = "Editar hábito",
            modifier           = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Contenedor del hábito
        Row(
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del hábito
            ImageContainer(
                resId              = iconRes,
                contentDescription = label,
                modifier           = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Texto del hábito
            Text(
                text     = label,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Switch de activación
            SwitchToggle(
                checked         = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HabitItemPreview() {
    HabitItem(
        iconRes = R.drawable.iconwaterimage,
        label = "Beber 2 L de agua",
        checked = true,
        onCheckedChange = {},
        onEdit = {}
    )
}
