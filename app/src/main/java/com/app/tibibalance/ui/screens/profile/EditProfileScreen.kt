/**
 * @file    EditProfileScreen.kt
 * @ingroup ui_screens_profile // Grupo para pantallas relacionadas con el perfil de usuario
 * @brief   Define el [Composable] para la pantalla de edición del perfil de usuario.
 *
 * @details Este archivo contiene la implementación de la interfaz de usuario para
 * permitir al usuario modificar la información de su perfil, como la foto,
 * el nombre de usuario, la fecha de nacimiento y la contraseña.
 *
 * La pantalla presenta:
 * - Un contenedor de imagen de perfil ([ProfileContainer]) con un botón para cambiarla.
 * - Campos de texto ([InputText], [OutlinedTextField]) para el nombre de usuario,
 * correo electrónico (solo lectura con nota informativa), fecha de nacimiento (que abre un [DatePickerDialog]),
 * y contraseña (mostrada como asteriscos).
 * - Botones de acción ([SecondaryButton]) para guardar los cambios o cancelar la edición.
 * - Un fondo degradado consistente con otras pantallas.
 *
 * Utiliza `remember` para gestionar el estado local de la fecha seleccionada y
 * el [DatePickerDialog] del sistema. Los callbacks `onChangePhoto`, `onSave`, y `onCancel`
 * se proporcionan desde el exterior para manejar la lógica de negocio asociada.
 *
 * @see ProfileContainer Componente para mostrar y potencialmente cambiar la imagen de perfil.
 * @see InputText Componente reutilizable para campos de texto.
 * @see OutlinedTextField Componente de Material 3 usado directamente para el campo de fecha.
 * @see Subtitle Componente para los títulos de cada campo.
 * @see SecondaryButton Botones para acciones de "Guardar" y "Cancelar".
 * @see DatePickerDialog Diálogo estándar de Android para seleccionar fechas.
 */
package com.app.tibibalance.ui.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Subtitle

/**
 * @brief Composable que define la interfaz de usuario para la pantalla de edición del perfil.
 *
 * @details Permite al usuario modificar su foto de perfil, nombre de usuario, fecha de nacimiento
 * y contraseña. El correo electrónico se muestra pero no es editable. Utiliza un [DatePickerDialog]
 * para la selección de la fecha de nacimiento.
 *
 * @param onChangePhoto Lambda que se invoca cuando el usuario pulsa el botón "Cambiar Foto".
 * Debería iniciar el flujo para seleccionar una nueva imagen. Por defecto, lambda vacía.
 * @param onSave Lambda que se invoca cuando el usuario pulsa el botón "Guardar".
 * Debería manejar la lógica de persistencia de los cambios del perfil. Por defecto, lambda vacía.
 * @param onCancel Lambda que se invoca cuando el usuario pulsa el botón "Cancelar".
 * Debería descartar los cambios y/o navegar hacia atrás. Por defecto, lambda vacía.
 */
@Composable
fun EditProfileScreen(
    onChangePhoto: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    // Estado local para la fecha de nacimiento, inicializada con un valor de ejemplo.
    var date by remember { mutableStateOf("01/05/2025") }
    val context = LocalContext.current // Contexto para el DatePickerDialog.

    // DatePickerDialog del sistema, recordado para evitar recreación.
    // El listener actualiza el estado 'date' cuando se selecciona una fecha.
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day -> // El mes es 0-indexed, por eso month + 1
                date = "%02d/%02d/%04d".format(day, month + 1, year)
            },
            2025, 4, 1 // Año, mes (0-indexed), día iniciales para el diálogo
        )
    }

    // Define el fondo degradado para la pantalla.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor principal que ocupa toda la pantalla.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Aplica el fondo degradado.
    ) {
        // Columna principal para el contenido, permite desplazamiento vertical.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita el scroll.
                .padding(horizontal = 16.dp, vertical = 15.dp), // Padding general.
            horizontalAlignment = Alignment.CenterHorizontally // Centra elementos horizontalmente.
        ) {
            Spacer(Modifier.height(1.dp)) // Pequeño espacio superior.

            // Contenedor para la imagen de perfil.
            ProfileContainer(
                imageResId = R.drawable.imagenprueba, // Imagen de perfil de ejemplo.
                size = 110.dp,
                contentDescription = "Foto de perfil"
            )

            Spacer(Modifier.height(5.dp))

            // Botón para cambiar la foto de perfil.
            SecondaryButton(
                text = "Cambiar Foto",
                onClick = onChangePhoto, // Invoca el callback al pulsar.
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
            )

            Spacer(Modifier.height(6.dp))

            // Campo para el nombre de usuario.
            Subtitle(text = "Nombre de usuario:")
            InputText(
                value = "nora soto", // Valor de ejemplo, debería venir de un ViewModel.
                onValueChange = { /* TODO: Actualizar estado del ViewModel */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(2.dp))

            // Campo para el correo electrónico (solo lectura).
            Subtitle(text = "Correo electrónico:")
            InputText(
                value = "norasoto5@gmail.com", // Valor de ejemplo.
                onValueChange = { /* no editable */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = (-4).dp), // Ajuste visual.
                // Texto de ayuda indicando que no es editable.
                supportingText = "ℹ\uFE0F El correo electrónico no es editable"
            )

            Spacer(Modifier.height(5.dp))

            // Campo para la fecha de nacimiento (usa OutlinedTextField para clic).
            Subtitle(text = "Fecha de nacimiento:")
            OutlinedTextField(
                value = date, // Muestra la fecha seleccionada.
                onValueChange = { /* read-only */ },
                readOnly = true, // No permite escritura directa.
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePicker.show() }, // Muestra el DatePickerDialog al hacer clic.
                label = { Text("Fecha de nacimiento") },
                trailingIcon = { // Icono de calendario.
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        tint = MaterialTheme.colorScheme.primary // Tinte del tema.
                    )
                },
                singleLine = true,
                // Colores personalizados para el estado deshabilitado/enfocado.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor       = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor     = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor      = MaterialTheme.colorScheme.onSurface,
                    errorTextColor         = MaterialTheme.colorScheme.error,
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor  = MaterialTheme.colorScheme.surface,
                    errorContainerColor     = MaterialTheme.colorScheme.surface,
                    focusedBorderColor      = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                    disabledBorderColor     = MaterialTheme.colorScheme.outline,
                    errorBorderColor        = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp) // Bordes redondeados.
            )

            Spacer(Modifier.height(5.dp))

            // Campo para la contraseña.
            Subtitle(text = "Contraseña:")
            InputText( // Debería ser InputPassword para ocultar el texto.
                value = "********", // Valor de ejemplo.
                onValueChange = { /* TODO: Actualizar estado y manejar visibilidad */ },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones de acción Guardar y Cancelar.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre botones.
            ) {
                SecondaryButton(
                    text = "Guardar",
                    onClick = onSave, // Invoca callback de guardado.
                    modifier = Modifier.weight(1f) // Ocupa espacio flexible.
                )
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onCancel, // Invoca callback de cancelación.
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                )
            }
        }
    }
}

/**
 * @brief Previsualización del [Composable] [EditProfileScreen].
 * @details Muestra la pantalla de edición de perfil con valores de ejemplo.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewFeatureEditProfileScreen() {
    // Envuelve en MaterialTheme para que los colores y estilos del tema se apliquen en la preview.
    MaterialTheme {
        EditProfileScreen()
    }
}