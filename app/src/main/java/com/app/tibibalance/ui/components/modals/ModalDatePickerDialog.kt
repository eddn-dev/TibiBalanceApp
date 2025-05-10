/**
 * @file    ModalDatePickerDialog.kt
 * @ingroup ui_component_modal // Grupo específico para componentes modales/diálogos
 * @brief   Define un [Composable] para un diálogo modal que contiene un selector de fecha ([DatePicker]).
 *
 * @details Este componente reutilizable presenta un diálogo flotante ([Dialog]) que permite
 * al usuario seleccionar una fecha utilizando el componente [DatePicker] de Material 3.
 * Mantiene un estilo visual consistente con otros diálogos modales de la aplicación
 * (como `ModalInfoDialog`, mencionado en el comentario original), incluyendo un icono
 * de cabecera ([Icons.Default.Event]), un título, el propio selector de fecha, y
 * botones de acción ("Cancelar", "Aceptar").
 *
 * El estado del selector de fecha se gestiona internamente mediante [rememberDatePickerState].
 * La visibilidad del diálogo se controla externamente a través del parámetro [visible].
 * El resultado de la selección (la [LocalDate] elegida o `null` si se cancela) se
 * comunica a través del callback [onConfirmDate].
 *
 * @see Dialog Componente base para mostrar contenido flotante.
 * @see DatePicker Composable de Material 3 para seleccionar fechas.
 * @see rememberDatePickerState Hook para gestionar el estado del DatePicker.
 * @see ExperimentalMaterial3Api Necesario para DatePicker y su estado.
 * @see PrimaryButton Botón utilizado para la acción de confirmar.
 * @see TextButton Botón utilizado para la acción de cancelar.
 * @see LocalDate Clase de `java.time` utilizada para representar la fecha seleccionada.
 */
package com.app.tibibalance.ui.components.modals

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event // Icono de calendario
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog // Usar Dialog para la ventana modal
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import java.time.LocalDate // Usar java.time.LocalDate
// import java.time.ZoneOffset // Necesario si se usara conversión más precisa a/desde Instant

/**
 * @brief Muestra un diálogo modal que contiene un [DatePicker] de Material 3.
 *
 * @details Este [Composable] renderiza un [Dialog] estándar de Compose que envuelve
 * el selector de fecha [DatePicker]. Incluye un icono de cabecera, título, el
 * propio DatePicker, y botones de "Cancelar" y "Aceptar". La selección de fecha
 * se gestiona con [rememberDatePickerState] y se convierte a [LocalDate] al confirmar.
 * El diálogo se cierra y se llama a [onConfirmDate] con `null` si el usuario
 * descarta el diálogo (pulsando fuera o botón Atrás) o pulsa "Cancelar".
 * Se llama a [onConfirmDate] con la [LocalDate] seleccionada si pulsa "Aceptar".
 *
 * @optIn Necesita [ExperimentalMaterial3Api] porque [DatePicker] y [rememberDatePickerState]
 * son parte de las APIs experimentales de Material 3.
 *
 * @param visible Controla si el diálogo se muestra (`true`) o se oculta (`false`).
 * @param initialDate La [LocalDate] que se mostrará seleccionada inicialmente cuando
 * el diálogo aparezca por primera vez. Por defecto es la fecha actual (`LocalDate.now()`).
 * @param onConfirmDate La función lambda que se invoca cuando el diálogo se cierra.
 * Recibe la [LocalDate] seleccionada si el usuario pulsó "Aceptar", o `null`
 * si el usuario canceló o descartó el diálogo.
 * @param title El [String] que se muestra como título principal del diálogo.
 * Por defecto es "Selecciona fecha".
 */
@OptIn(ExperimentalMaterial3Api::class) // Indica uso de APIs experimentales de M3
@Composable
fun ModalDatePickerDialog(
    visible       : Boolean,
    initialDate   : LocalDate = LocalDate.now(), // Fecha inicial por defecto: hoy
    onConfirmDate : (LocalDate?) -> Unit, // Callback que devuelve LocalDate?
    title         : String = "Selecciona fecha", // Título por defecto
    selectableDates : SelectableDates? = null
) {
    // Si el diálogo no debe ser visible, no renderizar nada.
    if (!visible) return

    // Crea y recuerda el estado del DatePicker. Se inicializa con la fecha proporcionada.
    // Convierte LocalDate a milisegundos epoch UTC asumiendo inicio del día.
    // 86_400_000L es el número de milisegundos en un día (24 * 60 * 60 * 1000).
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 86_400_000L,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayEpochMillis = LocalDate.now().toEpochDay() * 86_400_000L
                return utcTimeMillis >= todayEpochMillis
            }
        }
    )


    // Renderiza el componente Dialog de Compose.
    Dialog(onDismissRequest = { onConfirmDate(null) }) { // Llama a onConfirmDate(null) si se descarta
        // Superficie principal del diálogo con forma y elevación.
        Surface(
            shape          = RoundedCornerShape(20.dp), // Bordes redondeados
            tonalElevation = 6.dp // Sombra/elevación
            // modifier = Modifier.wrapContentSize() // Ajusta tamaño al contenido
        ) {
            // Columna para organizar el contenido verticalmente.
            Column(
                modifier = Modifier
                    .padding(24.dp) // Padding interno general
                    // Define un ancho mínimo y máximo para el diálogo
                    .widthIn(min = 280.dp, max = 560.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos
            ) {
                /* ---- Cabecera con Icono ---- */
                Box(
                    modifier = Modifier
                        .size(72.dp) // Tamaño del círculo contenedor
                        .clip(CircleShape) // Recorte circular
                        .background(MaterialTheme.colorScheme.primaryContainer), // Fondo del tema
                    contentAlignment = Alignment.Center // Centra el icono
                ) {
                    Icon(
                        imageVector = Icons.Default.Event, // Icono de calendario
                        contentDescription = null, // Decorativo
                        modifier = Modifier.size(36.dp), // Tamaño del icono
                        tint = MaterialTheme.colorScheme.onPrimaryContainer // Color del icono del tema
                    )
                }
                Spacer(Modifier.height(16.dp)) // Espacio

                /* ---- Título ---- */
                Text(title, style = MaterialTheme.typography.headlineSmall) // Título principal
                Spacer(Modifier.height(16.dp)) // Espacio

                /* ---- Selector de Fecha (DatePicker) ---- */
                DatePicker(
                    state = datePickerState, // Pasa el estado gestionado
                    modifier = Modifier.weight(1f, fill = false), // Permite crecer verticalmente si es necesario
                    title = null, // Oculta el título interno del DatePicker
                    headline = null // Oculta el headline interno del DatePicker
                    // showModeToggle = false // Opcional: Ocultar el toggle entre calendario/input
                )
                Spacer(Modifier.height(24.dp)) // Espacio antes de los botones

                /* ---- Botones de Acción ---- */
                Row(
                    modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho
                    horizontalArrangement = Arrangement.End // Alinea botones al final (derecha)
                ) {
                    // Botón Cancelar
                    TextButton(onClick = { onConfirmDate(null) }) { // Llama al callback con null
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp)) // Espacio entre botones
                    // Botón Aceptar (Primario)
                    PrimaryButton(
                        text = "Aceptar",
                        onClick = {
                            // Obtiene los milisegundos seleccionados (puede ser null si no se seleccionó)
                            val selectedMillis = datePickerState.selectedDateMillis
                            // Convierte a LocalDate solo si no es null
                            val selectedDate = selectedMillis?.let { millis ->
                                // Convierte milisegundos epoch UTC a días epoch
                                LocalDate.ofEpochDay(millis / 86_400_000L)
                            }
                            // Llama al callback con la fecha seleccionada (o null)
                            onConfirmDate(selectedDate)
                        },
                        // Habilita el botón solo si hay una fecha seleccionada
                        enabled = datePickerState.selectedDateMillis != null
                    )
                }
            }
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [ModalDatePickerDialog].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "ModalDatePickerDialog")
@Composable
private fun ModalDatePickerDialogPreview() {
    // Simula el estado de visibilidad y la fecha seleccionada para la preview
    var showDialog by remember { mutableStateOf(true) } // Inicia visible para la preview
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.of(2025, 5, 8)) }

    MaterialTheme {
        // Muestra un texto con la fecha seleccionada (solo para la preview)
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fecha seleccionada: ${selectedDate?.toString() ?: "Ninguna"}")
            Spacer(Modifier.height(8.dp))
            // Botón para re-abrir el diálogo en la preview
            Button(onClick = { showDialog = true }, enabled = !showDialog) {
                Text("Abrir DatePicker")
            }
        }

        // El diálogo modal
        ModalDatePickerDialog(
            visible = showDialog,
            initialDate = LocalDate.of(2025, 5, 1), // Fecha inicial diferente a la seleccionada
            onConfirmDate = { date ->
                showDialog = false // Cierra el diálogo al confirmar o cancelar
                selectedDate = date // Actualiza el estado simulado
                // Log para ver el resultado en la consola de la preview
                println("Preview - Fecha confirmada: $date")
            },
            title = "Selecciona una Fecha de Inicio"
        )
    }
}
