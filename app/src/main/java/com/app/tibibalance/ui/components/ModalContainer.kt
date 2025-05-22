/**
 * @file    ModalContainer.kt
 * @ingroup ui_component // O ui_component_modal si se agrupan los modales
 * @brief   Define un [Composable] base reutilizable para mostrar contenido dentro de un diálogo modal.
 *
 * @details Este archivo contiene el [Composable] `ModalContainer`, que actúa como una estructura
 * estándar para todos los diálogos modales de la aplicación. Utiliza el componente
 * [Dialog] de Jetpack Compose y dentro de él un [Card] de Material 3 para darle
 * estilo (esquinas redondeadas, elevación, color de fondo).
 *
 * Incluye un [Box] interno que permite superponer un botón de cierre opcional ([com.app.tibibalance.ui.components.buttons.IconButton] con icono [Icons.Filled.Close])
 * en la esquina superior derecha sobre el contenido principal. El contenido del modal
 * se pasa como una lambda con receptor [ColumnScope], facilitando la disposición vertical
 * de los elementos internos. El `ModalContainer` define un ancho fijo y padding interno
 * para mantener la consistencia visual entre diferentes modales.
 *
 * @see androidx.compose.ui.window.Dialog Componente base para mostrar diálogos.
 * @see androidx.compose.material3.Card Componente de Material 3 usado como contenedor visual del diálogo.
 * @see androidx.compose.foundation.layout.ColumnScope El scope proporcionado al slot `content`.
 * @see androidx.compose.foundation.layout.Box Contenedor usado para posicionar el botón de cierre.
 * @see androidx.compose.material3.IconButton Botón utilizado para la acción de cierre.
 * @see androidx.compose.material.icons.filled.Close Icono utilizado para el botón de cierre.
 * @see androidx.compose.ui.window.DialogProperties Permite configurar el comportamiento del diálogo.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview // Importar para Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * @brief Un [Composable] que actúa como contenedor base para los diálogos modales de la aplicación.
 *
 * @details Renderiza un [Dialog] de Compose que contiene un [Card] estilizado.
 * Dentro del [Card], un [Box] organiza el contenido principal (proporcionado a través del
 * slot `content` con [ColumnScope]) y superpone opcionalmente un [com.app.tibibalance.ui.components.buttons.IconButton] de cierre
 * en la esquina superior derecha. Proporciona padding interno y un ancho fijo para
 * consistencia visual.
 *
 * @param onDismissRequest La función lambda que se invoca cuando el usuario intenta cerrar
 * el diálogo (e.g., pulsando fuera del diálogo o usando el botón de retroceso del sistema,
 * si `properties` lo permite).
 * @param modifier Un [Modifier] opcional que se aplica al [Card] interno del diálogo.
 * Por defecto, aplica un ancho fijo de `400.dp`.
 * @param shape La [Shape] a aplicar al [Card] interno. Por defecto, [RoundedCornerShape] con `16.dp`.
 * @param containerColor El [Color] de fondo para el [Card] interno. Por defecto, el color `surface` del tema.
 * @param contentColor El [Color] preferido para el contenido (texto, iconos) dentro del [Card],
 * calculado por defecto con `contentColorFor` basado en `containerColor`.
 * @param properties Una instancia de [DialogProperties] para configurar el comportamiento del
 * [Dialog] subyacente (e.g., si se puede descartar con el botón Atrás o pulsando fuera).
 * Por defecto, utiliza las propiedades predeterminadas de [DialogProperties].
 * @param closeButtonEnabled Un [Boolean] que indica si se debe mostrar el botón de cierre ('X')
 * en la esquina superior derecha. Por defecto `true`.
 * @param closeButtonBackgroundColor El [Color] de fondo para el [Surface] circular que envuelve
 * el icono de cierre. Por defecto `Color.White`.
 * @param closeButtonContentColor El [Color] (tinte) para el icono de cierre ([Icons.Filled.Close]).
 * Por defecto, el color `onSurface` del tema.
 * @param content Un slot [Composable] lambda con receptor [ColumnScope]. Aquí es donde se
 * coloca el contenido principal del diálogo (títulos, textos, botones, etc.).
 * La [Column] interna aplica padding y disposición vertical espaciada.
 */
@Composable
fun ModalContainer(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier, // Modificador para el Card interno
    shape: Shape = RoundedCornerShape(16.dp), // Forma por defecto
    containerColor: Color = MaterialTheme.colorScheme.surface, // Color de fondo del tema
    contentColor: Color = contentColorFor(containerColor), // Color de contenido acorde al fondo
    properties: DialogProperties = DialogProperties(), // Propiedades del diálogo (e.g., dismiss)
    closeButtonEnabled: Boolean = true, // Mostrar botón 'X' por defecto
    closeButtonBackgroundColor: Color = Color.White, // Fondo blanco para el botón 'X'
    closeButtonContentColor: Color = MaterialTheme.colorScheme.onSurface, // Color del icono 'X'
    content: @Composable ColumnScope.() -> Unit // Slot para el contenido principal
) {
    // Componente Dialog que gestiona la ventana modal
    Dialog(
        onDismissRequest = onDismissRequest, // Callback cuando se intenta cerrar
        properties = properties // Propiedades de comportamiento del diálogo
    ) {
        // Card que define la apariencia visual del contenedor del diálogo
        Card(
            modifier = modifier.width(400.dp), // Aplica modificador externo y ancho fijo
            shape = shape, // Forma (bordes redondeados)
            colors = CardDefaults.cardColors( // Colores del Card
                containerColor = containerColor, // Color de fondo
                contentColor = contentColor // Color del contenido interno
            )
            // Se podría añadir elevación aquí si se desea una sombra diferente a la del Dialog
        ) {
            // Box permite superponer el botón de cierre sobre el contenido
            Box(
                // Padding general dentro del Card, pero fuera de la Column de contenido
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                // Columna principal para el contenido del modal
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa el ancho del Card
                        // Padding interno para el contenido, dejando espacio arriba si hay botón 'X'
                        .padding(
                            top = if (closeButtonEnabled) 40.dp else 16.dp, // Más padding superior si hay botón
                            start = 10.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                ) {
                    // Renderiza el contenido proporcionado en el slot
                    content()
                }

                // Renderiza el botón de cierre si está habilitado
                if (closeButtonEnabled) {
                    // Surface para darle fondo circular y elevación al IconButton
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd) // Alinea en la esquina superior derecha del Box
                            .size(32.dp), // Tamaño del área del botón
                        shape = CircleShape, // Forma circular
                        color = closeButtonBackgroundColor, // Color de fondo
                        shadowElevation = 2.dp // Pequeña sombra para destacar
                    ) {
                        IconButton(
                            onClick = onDismissRequest, // Llama al dismiss al pulsar
                            modifier = Modifier.size(32.dp) // Asegura tamaño del IconButton
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close, // Icono 'X'
                                contentDescription = "Cerrar modal", // Descripción para accesibilidad
                                tint = closeButtonContentColor, // Tinte del icono
                                modifier = Modifier.size(18.dp) // Tamaño del icono interno
                            )
                        }
                    }
                }
            } // Fin Box
        } // Fin Card
    } // Fin Dialog
}

// --- Previews ---

/**
 * @brief Previsualización del [ModalContainer] con contenido simple y botón de cierre.
 */
@Preview(showBackground = true, name = "ModalContainer Default")
@Composable
private fun ModalContainerPreview() {
    MaterialTheme {
        // Simula la llamada al diálogo (en una preview real, necesitaría un estado `visible`)
        ModalContainer(
            onDismissRequest = { /* Acción al cerrar */ },
            // modifier = Modifier.height(250.dp) // Opcional: limitar altura en preview
        ) {
            // Contenido de ejemplo dentro del scope de ColumnScope
            Text("Título del Modal", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Este es el contenido del diálogo modal. Puede contener cualquier Composable.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { /* Acción */ }) {
                Text("Aceptar")
            }
        }
    }
}

/**
 * @brief Previsualización del [ModalContainer] sin botón de cierre y con color personalizado.
 */
@Preview(showBackground = true, name = "ModalContainer No Close Button")
@Composable
private fun ModalContainerNoClosePreview() {
    MaterialTheme {
        ModalContainer(
            onDismissRequest = { },
            containerColor = MaterialTheme.colorScheme.secondaryContainer, // Color diferente
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            closeButtonEnabled = false // Oculta el botón 'X'
        ) {
            Text("Diálogo Informativo", style = MaterialTheme.typography.titleMedium)
            Text("Este diálogo no tiene botón de cierre explícito y debe cerrarse mediante otra acción o descartándolo (si properties lo permite).")
        }
    }
}