package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape // Importar CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Importar Icons
import androidx.compose.material.icons.filled.Close // Importar Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog // Importación clave
import androidx.compose.ui.window.DialogProperties

/**
 * Contenedor base para modales, con un ancho fijo de 320.dp.
 *
 * @param onDismissRequest Se llama cuando el usuario intenta cerrar el diálogo.
 * @param modifier Modificador a aplicar al Card interno.
 * @param shape La forma del Card interno.
 * @param containerColor Color de fondo del Card interno.
 * @param contentColor Color del contenido dentro del Card.
 * @param properties Propiedades del Dialog.
 * @param closeButtonEnabled Si el botón 'X' debe mostrarse.
 * @param closeButtonBackgroundColor Color de fondo para el botón de cierre.
 * @param closeButtonContentColor Color del icono del botón de cierre.
 * @param content El contenido composable que se mostrará dentro del diálogo.
 */
@Composable
fun ModalContainer(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier, // Modificador para el Card interno
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    properties: DialogProperties = DialogProperties(), // Propiedades del Dialog
    closeButtonEnabled: Boolean = true,
    closeButtonBackgroundColor: Color = Color.White,
    closeButtonContentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit // Contenido del modal
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Card(
            modifier = modifier.width(400.dp),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp) // Padding interno del Box
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // La columna sí llena el ancho del Card
                        .padding( // Padding interno de la columna
                            top = if (closeButtonEnabled) 40.dp else 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                ) {
                    content() // Contenido personalizado
                }

                // Botón de cierre 'X'.
                if (closeButtonEnabled) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp),
                        shape = CircleShape,
                        color = closeButtonBackgroundColor,
                        shadowElevation = 2.dp
                    ) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cerrar modal",
                                tint = closeButtonContentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
