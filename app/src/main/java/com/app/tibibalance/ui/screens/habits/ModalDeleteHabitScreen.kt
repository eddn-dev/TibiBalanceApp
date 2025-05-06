// ui/screens/habits/ModalDeleteHabitScreen.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.DangerButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton

@Composable
fun ModalDeleteHabitScreen(
    habitName: String,
    habitIconRes: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {

    ModalContainer(
        onDismissRequest = onDismissRequest,
        containerColor   = Color(0xFFAED3E3),
        shape            = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImageContainer(
                resId              = R.drawable.trashimage,
                contentDescription = "Eliminar hábito",
                modifier           = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text      = "¿Estás seguro de que quieres eliminar este hábito?",
                modifier  = Modifier.fillMaxWidth(),
                style     = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text      = "Este hábito será eliminado.",
                modifier  = Modifier.fillMaxWidth(),
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text      = "Tienes un período de recuperación de X días para restaurarlo antes de que se elimine de forma permanente.",
                modifier  = Modifier.fillMaxWidth(),
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            Row(
                modifier             = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .clickable { /* opcional: ver detalle */ },
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImageContainer(
                    resId              = habitIconRes,
                    contentDescription = habitName,
                    modifier           = Modifier.size(32.dp)
                )
                Text(
                    text  = habitName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DangerButton(
                    text = "Eliminar",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewModalDeleteHabitScreen() {
    ModalDeleteHabitScreen(
        habitName        = "Beber dos litros \nde agua al día",
        habitIconRes     = R.drawable.iconwaterimage,
        onConfirm        = { /*…*/ },
        onCancel         = { /*…*/ },
        onDismissRequest = { /*…*/ }
    )
}
