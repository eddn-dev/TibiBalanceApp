/**
 * @file    ModalInfoDialog.kt
 * @ingroup ui_component_modal // Grupo para componentes modales o de diálogo
 * @brief   Define un [Composable] para un diálogo modal altamente versátil que puede mostrar estados de carga, información o errores.
 *
 * @details Este archivo contiene la data class [DialogButton] para estructurar la información de los
 * botones de acción y el [Composable] principal `ModalInfoDialog`.
 * Este diálogo es "3-en-1":
 * - **Modo Carga (`loading = true`):** Muestra un [CircularProgressIndicator] y un texto
 * opcional (por defecto "Procesando..."). Los botones y otros elementos de información se ocultan.
 * - **Modo Información/Error/Éxito (`loading = false`):** Muestra un icono opcional (con colores
 * personalizables), un título opcional, un mensaje opcional y hasta dos botones de acción
 * ([PrimaryButton] y/o [SecondaryButton]).
 * - **Oculto (`visible = false`):** El diálogo no se renderiza.
 *
 * Su flexibilidad lo hace adecuado para una amplia gama de escenarios de feedback al usuario,
 * como confirmaciones, errores de red, mensajes de éxito tras una operación, o simplemente
 * para indicar que una tarea está en curso.
 *
 * <h4>Ejemplos de Uso (como se indica en el comentario original del código):</h4>
 * ```kotlin
 * // Sólo spinner
 * ModalInfoDialog(visible = true, loading = true)
 *
 * // Mensaje de éxito con botón OK
 * ModalInfoDialog(
 * visible = true,
 * icon    = Icons.Default.Check,
 * message = "¡Guardado!",
 * primaryButton = DialogButton("Aceptar") { dialogVisible = false }
 * )
 *
 * // Mensaje de error con dos botones
 * ModalInfoDialog(
 * visible = true,
 * icon       = Icons.Default.Error,
 * iconColor  = MaterialTheme.colorScheme.error,
 * message    = "No se pudo completar la operación",
 * primaryButton   = DialogButton("Reintentar", ::retry),
 * secondaryButton = DialogButton("Cancelar") { dialogVisible = false }
 * )
 * ```
 *
 * @see Dialog Componente base para diálogos.
 * @see Surface Usado como contenedor visual del diálogo.
 * @see CircularProgressIndicator Indicador para el estado de carga.
 * @see Icon Para mostrar el icono informativo.
 * @see Text Para mostrar título, mensaje y texto de carga.
 * @see PrimaryButton
 * @see SecondaryButton
 * @see DialogButton Data class para definir los botones de acción.
 */
package com.app.tibibalance.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Para previews (no directamente en ModalInfoDialog)
import androidx.compose.material.icons.filled.Check // Para previews
import androidx.compose.material.icons.filled.Error // Para previews
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Para previews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton

/**
 * @brief Data class para definir la estructura y acción de un botón dentro de [ModalInfoDialog].
 * @details Simplifica la API de `ModalInfoDialog` al agrupar el texto del botón y su callback `onClick`.
 *
 * @param text El [String] que se mostrará en el botón.
 * @param onClick La función lambda que se ejecutará cuando el botón sea pulsado.
 */
data class DialogButton(
    val text: String,
    val onClick: () -> Unit
)

/**
 * @brief Un diálogo modal versátil para mostrar estados de carga, información, éxito o error.
 *
 * @details Si `visible` es `false`, no se renderiza nada.
 * Si `loading` es `true`, muestra un [CircularProgressIndicator] y el `textLoading`.
 * En otro caso, muestra un icono opcional, título opcional, mensaje opcional y hasta dos
 * botones de acción personalizables.
 *
 * @param visible Controla si el diálogo se muestra (`true`) o se oculta (`false`).
 * @param loading Si es `true`, el diálogo muestra un indicador de carga y el `textLoading`,
 * ignorando los parámetros de `icon`, `title`, `message` y botones. Por defecto `false`.
 * @param icon Un [ImageVector] opcional que se muestra en la parte superior del contenido informativo.
 * Ignorado si `loading` es `true`. Por defecto `null` (sin icono).
 * @param iconColor El [Color] para el tinte del `icon`. Por defecto `MaterialTheme.colorScheme.onPrimaryContainer`.
 * @param iconBgColor El [Color] de fondo para el [Box] circular que contiene el `icon`.
 * Por defecto `MaterialTheme.colorScheme.primaryContainer`.
 * @param title Un [String] opcional que se muestra como título del diálogo. Ignorado si `loading` es `true`.
 * Por defecto `null`.
 * @param message Un [String] opcional que se muestra como cuerpo del mensaje del diálogo.
 * Ignorado si `loading` es `true`. Por defecto `null`.
 * @param textLoading El [String] que se muestra debajo del indicador de progreso cuando `loading` es `true`.
 * Por defecto "Procesando…".
 * @param primaryButton Un objeto [DialogButton] opcional para la acción principal (e.g., "Aceptar", "Guardar").
 * Ignorado si `loading` es `true`. Por defecto `null`.
 * @param secondaryButton Un objeto [DialogButton] opcional para la acción secundaria (e.g., "Cancelar", "Cerrar").
 * Ignorado si `loading` es `true`. Por defecto `null`. Se muestra a la izquierda del primario.
 * @param dismissOnBack Un [Boolean] que controla si el diálogo se puede descartar pulsando el botón
 * de retroceso del sistema. Por defecto es `!loading` (se puede descartar si no está cargando).
 * @param dismissOnClickOutside Un [Boolean] que controla si el diálogo se puede descartar pulsando
 * fuera de su área. Por defecto es `!loading` (se puede descartar si no está cargando).
 */
@Composable
fun ModalInfoDialog(
    visible        : Boolean,
    loading        : Boolean = false,
    icon           : ImageVector? = null,
    iconColor      : Color = MaterialTheme.colorScheme.onPrimaryContainer,
    iconBgColor    : Color = MaterialTheme.colorScheme.primaryContainer,
    title          : String? = null,
    message        : String? = null,
    textLoading    : String = "Procesando…", // Texto por defecto para loading
    primaryButton  : DialogButton? = null,
    secondaryButton: DialogButton? = null,
    dismissOnBack  : Boolean = !loading, // No se puede descartar si está cargando, por defecto
    dismissOnClickOutside: Boolean = !loading // No se puede descartar si está cargando, por defecto
) {
    // Si no es visible, no renderizar nada
    if (!visible) return

    // Componente Dialog raíz
    Dialog(
        // El onDismissRequest se maneja a través de DialogProperties
        onDismissRequest = { /* No hacer nada aquí; controlado por properties */ },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBack, // Controla descarte con botón Atrás
            dismissOnClickOutside = dismissOnClickOutside // Controla descarte con clic fuera
        )
    ) {
        // Contenedor visual principal del diálogo
        Surface(
            shape          = RoundedCornerShape(20.dp), // Bordes redondeados
            tonalElevation = 6.dp // Sombra
            // El modificador para tamaño se podría aplicar aquí si se deseara
        ) {
            // Elige qué contenido mostrar basado en el estado 'loading'
            when {
                loading -> LoadingContent(textLoading) // Muestra contenido de carga
                else    -> InfoContent( // Muestra contenido informativo/de acción
                    icon, iconColor, iconBgColor,
                    title, message,
                    primaryButton, secondaryButton
                )
            }
        }
    }
}

/* ---------- layouts internos privados ---------- */

/**
 * @brief Composable privado que define el layout para el estado de carga del [ModalInfoDialog].
 * @param text El texto a mostrar debajo del indicador de progreso.
 */
@Composable
private fun LoadingContent(text: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 24.dp) // Padding interno
            .widthIn(min = 200.dp), // Ancho mínimo para asegurar legibilidad
        horizontalAlignment = Alignment.CenterHorizontally // Centra elementos
    ) {
        CircularProgressIndicator() // Indicador de progreso
        Spacer(Modifier.height(16.dp)) // Espacio
        Text(text, style = MaterialTheme.typography.bodyMedium) // Texto de carga
    }
}

/**
 * @brief Composable privado que define el layout para el estado informativo/de acción del [ModalInfoDialog].
 * @param icon Icono opcional.
 * @param iconColor Color del icono.
 * @param iconBgColor Color de fondo del contenedor del icono.
 * @param title Título opcional.
 * @param message Mensaje opcional.
 * @param primaryButton Botón de acción principal opcional.
 * @param secondaryButton Botón de acción secundario opcional.
 */
@Composable
private fun InfoContent(
    icon          : ImageVector?,
    iconColor     : Color,
    iconBgColor   : Color,
    title         : String?,
    message       : String?,
    primaryButton : DialogButton?,
    secondaryButton: DialogButton?
) {
    Column(
        modifier = Modifier
            .padding(32.dp) // Padding interno generoso
            .widthIn(min = 220.dp), // Ancho mínimo
        horizontalAlignment = Alignment.CenterHorizontally // Centra elementos
    ) {
        // Renderiza el icono si se proporciona
        icon?.let {
            Box(
                modifier = Modifier
                    .size(72.dp) // Tamaño del contenedor del icono
                    .clip(CircleShape) // Recorte circular
                    .background(iconBgColor), // Fondo del contenedor del icono
                contentAlignment = Alignment.Center // Centra el icono
            ) {
                Icon(
                    it, // El ImageVector del icono
                    contentDescription = null, // Icono decorativo, título/mensaje deben describir
                    tint   = iconColor, // Tinte del icono
                    modifier = Modifier.size(36.dp) // Tamaño del icono
                )
            }
            Spacer(Modifier.height(16.dp)) // Espacio post-icono
        }

        // Renderiza el título si se proporciona
        title?.let {
            Text(
                text  = it,
                style = MaterialTheme.typography.titleMedium, // Estilo para títulos
                textAlign = TextAlign.Center // Título centrado
            )
            Spacer(Modifier.height(8.dp)) // Espacio post-título
        }

        // Renderiza el mensaje si se proporciona
        message?.let {
            Text(
                text  = it,
                style = MaterialTheme.typography.bodyMedium, // Estilo para cuerpo de texto
                textAlign = TextAlign.Center // Mensaje centrado
            )
            // Espacio antes de los botones, solo si hay mensaje
            Spacer(Modifier.height(if (primaryButton != null || secondaryButton != null) 24.dp else 0.dp))
        }

        // Renderiza los botones si se proporcionan
        if (primaryButton != null || secondaryButton != null) {
            Row(
                Modifier.fillMaxWidth(), // La fila de botones ocupa el ancho
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End) // Espacio entre botones y alineados al final
            ) {
                // Botón secundario (se muestra a la izquierda del primario por convención)
                secondaryButton?.let {
                    SecondaryButton(
                        text = it.text,
                        onClick = it.onClick,
                        modifier = Modifier.weight(1f) // Comparte el espacio si ambos existen
                    )
                }
                // Botón primario
                primaryButton?.let {
                    PrimaryButton(
                        text = it.text,
                        onClick = it.onClick,
                        modifier = Modifier.weight(1f) // Comparte el espacio si ambos existen
                    )
                }
            }
        }
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "ModalInfoDialog - Loading")
@Composable
private fun ModalInfoDialogLoadingPreview() {
    MaterialTheme {
        ModalInfoDialog(visible = true, loading = true, textLoading = "Cargando datos...")
    }
}

@Preview(showBackground = true, name = "ModalInfoDialog - Success")
@Composable
private fun ModalInfoDialogSuccessPreview() {
    MaterialTheme {
        ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Check,
            title = "¡Operación Exitosa!",
            message = "Tus cambios se han guardado correctamente.",
            primaryButton = DialogButton("Aceptar") {},
            secondaryButton = DialogButton("Ver Detalles") {}
        )
    }
}

@Preview(showBackground = true, name = "ModalInfoDialog - Error")
@Composable
private fun ModalInfoDialogErrorPreview() {
    MaterialTheme {
        ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Error,
            iconColor = MaterialTheme.colorScheme.onErrorContainer,
            iconBgColor = MaterialTheme.colorScheme.errorContainer,
            title = "Error de Conexión",
            message = "No se pudo conectar al servidor. Por favor, revisa tu conexión a internet.",
            primaryButton = DialogButton("Reintentar") {}
        )
    }
}

@Preview(showBackground = true, name = "ModalInfoDialog - Solo Mensaje y Botón")
@Composable
private fun ModalInfoDialogSimplePreview() {
    MaterialTheme {
        ModalInfoDialog(
            visible = true,
            message = "¿Estás seguro de que deseas salir sin guardar?",
            primaryButton = DialogButton("Salir") {},
            secondaryButton = DialogButton("Cancelar") {}
        )
    }
}