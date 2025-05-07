package com.app.tibibalance.ui.components.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Diálogo para elegir una fecha con el mismo *look & feel* que ModalInfoDialog.
 *
 * @param visible       ¿Mostrar el diálogo?
 * @param initialDate   Fecha pre-seleccionada (por defecto hoy).
 * @param onConfirmDate Callback con la fecha elegida (LocalDate) o null si se cancela.
 * @param title         Título opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDatePickerDialog(
    visible       : Boolean,
    initialDate   : LocalDate = LocalDate.now(),
    onConfirmDate : (LocalDate?) -> Unit,
    title         : String = "Selecciona fecha"
) {
    if (!visible) return

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 86_400_000
    )

    Dialog(onDismissRequest = { onConfirmDate(null) }) {
        Surface(
            shape          = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(min = 260.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /* Icono de cabecera como en ModalInfoDialog -------------------- */
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Event, contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.height(16.dp))

                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                /* -------- DatePicker Material3 -------- */
                DatePicker(state = dateState)
                Spacer(Modifier.height(16.dp))

                /* -------- Botones -------- */
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { onConfirmDate(null) }) {
                        Text("Cancelar")
                    }
                    PrimaryButton(
                        text = "Aceptar",
                        onClick = {
                            val millis = dateState.selectedDateMillis ?: return@PrimaryButton
                            val picked = LocalDate.ofEpochDay(millis / 86_400_000)
                            onConfirmDate(picked)
                        }
                    )
                }
            }
        }
    }
}
