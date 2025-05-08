/**
 * @file    Alert.kt
 * @ingroup ui_component_feedback // Grupo para componentes de feedback/notificación
 * @brief   Define un [Composable] para mostrar mensajes de alerta con diferentes niveles de severidad.
 *
 * @details Este archivo contiene el enum [AlertSeverity] para definir los tipos de alerta
 * (Success, Warning, Error) y el [Composable] [Alert] que renderiza un banner
 * o caja de alerta con un estilo visual (color de fondo, color de texto, icono opcional)
 * correspondiente a la severidad indicada.
 *
 * Es útil para mostrar mensajes de estado, confirmaciones, advertencias o errores
 * al usuario de forma clara y consistente.
 *
 * @see AlertSeverity Enumeración de los niveles de severidad.
 * @see Alert El componente Composable principal.
 * @see Row Layout utilizado para alinear icono y texto.
 * @see Icon Composable opcional para mostrar un icono.
 * @see Text Composable para mostrar el mensaje de alerta.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // Importar para estilos de texto si se usan
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @brief Enumeración que define los niveles de severidad para el componente [Alert].
 * @details Determina el estilo visual (colores) de la alerta.
 */
enum class AlertSeverity {
    /** @brief Indica un mensaje de éxito o confirmación (generalmente verde). */
    Success,
    /** @brief Indica una advertencia o información importante (generalmente amarillo/naranja). */
    Warning,
    /** @brief Indica un error o fallo (generalmente rojo). */
    Error
}

/**
 * @brief Un [Composable] que muestra un mensaje de alerta estilizado según su severidad.
 *
 * @details Renderiza un [Row] con un color de fondo y contenido específicos
 * basados en el parámetro [severity]. Puede incluir opcionalmente un icono
 * al inicio del mensaje, pasado como un slot [Composable].
 *
 * @param text El [String] del mensaje que se mostrará en la alerta.
 * @param severity El nivel de [AlertSeverity] (Success, Warning, Error), que determina los colores.
 * @param modifier Un [Modifier] opcional para aplicar al [Row] contenedor principal.
 * @param icon Un slot [Composable] opcional que permite pasar un icono (u otro Composable)
 * para mostrar al inicio de la alerta. Si es `null`, no se muestra ningún icono.
 * Se recomienda pasar un [Icon] con tamaño y tinte consistentes con la severidad.
 */
@Composable
fun Alert(
    text: String,
    severity: AlertSeverity,
    modifier: Modifier = Modifier,
    // Permite pasar un icono (o cualquier Composable) como slot opcional
    icon: (@Composable () -> Unit)? = null
) {
    // Determina los colores basados en la severidad
    val backgroundColor = when (severity) {
        AlertSeverity.Success -> Color(0xFFE2FAD9) // Verde claro pastel
        AlertSeverity.Warning -> Color(0xFFFFF7D9) // Amarillo claro pastel
        AlertSeverity.Error   -> Color(0xFFFDE2E2) // Rojo claro pastel
    }
    val contentColor = when (severity) {
        AlertSeverity.Success -> Color(0xFF1B5E20) // Verde oscuro (ajustado para contraste)
        AlertSeverity.Warning -> Color(0xFFE65100) // Naranja oscuro (ajustado para contraste)
        AlertSeverity.Error   -> Color(0xFFB71C1C) // Rojo oscuro (ajustado para contraste)
    }

    // Fila principal de la alerta
    Row(
        modifier = modifier // Aplica modificador externo
            .fillMaxWidth() // Ocupa todo el ancho disponible
            // Fondo con color y bordes redondeados
            .background(backgroundColor, RoundedCornerShape(8.dp))
            // Padding interno
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, // Centra icono y texto verticalmente
        horizontalArrangement = Arrangement.Start // Alinea contenido al inicio
    ) {
        // Renderiza el icono si el slot no es nulo
        icon?.invoke()
        // Añade espacio solo si se renderizó un icono
        if (icon != null) {
            Spacer(modifier = Modifier.width(12.dp)) // Espacio entre icono y texto
        }
        // Texto del mensaje de alerta
        Text(
            text = text, // Mensaje
            color = contentColor, // Color de texto según severidad
            style = TextStyle( // Estilo de texto
                fontSize = 15.sp, // Tamaño ligeramente ajustado
                fontWeight = FontWeight.Medium // Peso medio
            )
            // Alternativa usando el tema:
            // style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización que muestra los diferentes tipos de [Alert].
 */
@Preview(showBackground = true, name = "Alerts Preview", widthDp = 340)
@Composable
private fun AlertPreview() {
    MaterialTheme { // Envuelve en MaterialTheme para estilos de texto
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre alertas
        ) {
            // Alerta de Éxito con icono
            Alert(
                text = "¡Éxito! Tu perfil ha sido actualizado.",
                severity = AlertSeverity.Success,
                icon = { // Slot para el icono
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Éxito", // Descripción semántica opcional
                        // Usa el color de contenido calculado para consistencia
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            // Alerta de Advertencia con icono
            Alert(
                text = "Atención: La conexión a internet es inestable.",
                severity = AlertSeverity.Warning,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Advertencia",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            // Alerta de Error con icono
            Alert(
                text = "Error: No se pudo cargar la información.",
                severity = AlertSeverity.Error,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            // Alerta sin icono (pasando null al slot)
            Alert(
                text = "Este es un mensaje informativo sin icono.",
                severity = AlertSeverity.Warning, // Puede ser cualquier severidad
                icon = null // Indica que no hay icono
            )
        }
    }
}
