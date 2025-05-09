/**
 * @file    SettingItem.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o elementos de lista
 * @brief   Define un [Composable] reutilizable para mostrar un elemento de configuración o una fila de lista.
 *
 * @details Este archivo contiene el [Composable] `SettingItem`. Este componente está diseñado
 * para renderizar una fila estándar que se puede utilizar en listas de configuración,
 * listas de hábitos, u otros escenarios donde se necesita mostrar un icono principal,
 * un texto descriptivo, y opcionalmente un componente de acción o indicador al final.
 *
 * Utiliza un [Surface] de Material 3 para proporcionar un fondo, forma y elevación
 * consistentes. La disposición interna es un [Row] que contiene un [Box] para el icono
 * principal (asegurando un tamaño y recorte circular), un [Description] para el texto,
 * y un slot opcional para el componente `trailing`. Si se proporciona un `onClick` callback,
 * toda la superficie del `SettingItem` se vuelve clicable.
 *
 * @see androidx.compose.material3.Surface Componente contenedor principal con estilo.
 * @see androidx.compose.foundation.layout.Row Para la disposición horizontal de los elementos.
 * @see androidx.compose.foundation.layout.Box Usado para contener el `leadingIcon`.
 * @see com.app.tibibalance.ui.components.texts.Description Composable para mostrar el texto principal.
 * @see androidx.compose.foundation.clickable Modificador para manejar interacciones de clic.
 */
/* ui/components/SettingItem.kt */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons // Para Preview
import androidx.compose.material.icons.filled.AccountCircle // Para Preview
import androidx.compose.material.icons.filled.ChevronRight // Para Preview
import androidx.compose.material3.Icon // Para Preview
import androidx.compose.material3.MaterialTheme // Para Preview
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch // Para Preview
import androidx.compose.runtime.* // Para Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview // Para Preview
import com.app.tibibalance.ui.components.texts.Description

/**
 * @brief Un [Composable] que renderiza una fila estándar para listas de configuración o elementos similares.
 *
 * @details Presenta un icono principal (leading), un texto descriptivo que ocupa el espacio
 * central, y un componente opcional al final (trailing). Toda la fila puede ser clicable.
 * Se utiliza un [Surface] para darle una apariencia de tarjeta con elevación y esquinas redondeadas.
 *
 * @param leadingIcon Un slot [Composable] lambda que define el icono o imagen que se mostrará
 * al inicio de la fila. Se envuelve en un [Box] con recorte circular y tamaño fijo de `24.dp`.
 * @param text El [String] principal que se mostrará en el centro de la fila, utilizando
 * el componente [Description].
 * @param trailing Un slot [Composable] lambda opcional que define el contenido que se mostrará
 * al final de la fila (e.g., un [Switch], un [Icon] de flecha, un [Checkbox]). Por defecto es `null`.
 * @param onClick Una función lambda opcional que se ejecuta cuando el usuario pulsa sobre
 * el `SettingItem`. Si es `null`, el item no será clicable. Por defecto es `null`.
 * @param containerColor El [Color] de fondo para el [Surface] contenedor. Por defecto `Color.White`.
 * Considera usar `MaterialTheme.colorScheme.surface` para adaptarse al tema.
 * @param cornerRadius El radio ([Dp]) para las esquinas redondeadas del [Surface]. Por defecto `16.dp`.
 * @param modifier Un [Modifier] opcional que se aplica al [Surface] contenedor. Por defecto,
 * ocupa el ancho máximo (`fillMaxWidth`).
 */
@Composable
fun SettingItem(
    leadingIcon   : @Composable () -> Unit, // Icono/imagen al inicio
    text          : String,                  // Texto principal
    trailing      : (@Composable () -> Unit)? = null, // Contenido opcional al final
    onClick       : (() -> Unit)? = null,    // Acción de clic opcional para toda la fila
    containerColor: Color = Color.White,     // Color de fondo por defecto
    cornerRadius  : Dp = 16.dp,              // Radio de esquina por defecto
    modifier      : Modifier = Modifier      // Modificador estándar
) {
    Surface(
        color  = containerColor, // Color de fondo del Surface
        shape  = RoundedCornerShape(cornerRadius), // Forma con esquinas redondeadas
        shadowElevation = 1.dp, // Pequeña elevación para efecto de tarjeta
        modifier = modifier
            .fillMaxWidth() // Ocupa el ancho disponible
            // Hace que toda la superficie sea clicable si onClick no es null
            .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
        // Fila para disponer los elementos horizontalmente
        Row(
            modifier = Modifier
                // Padding interno de la fila
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth(), // Asegura que la fila ocupe el ancho del Surface
            verticalAlignment = Alignment.CenterVertically // Centra los elementos verticalmente
        ) {
            // Contenedor para el icono principal (leading)
            Box(
                modifier = Modifier
                    .size(24.dp) // Tamaño fijo para el icono
                    .clip(CircleShape), // Recorte circular (aunque el icono puede no serlo)
                contentAlignment = Alignment.Center // Centra el icono dentro del Box
            ) {
                leadingIcon() // Renderiza el icono proporcionado
            }

            // Espacio entre el icono principal y el texto
            Spacer(Modifier.width(16.dp))

            // Texto principal
            Description(
                text = text, // El texto a mostrar
                modifier = Modifier.weight(1f) // Ocupa el espacio restante, empujando el trailing
            )

            // Renderiza el contenido final (trailing) si se proporciona
            trailing?.invoke()
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [SettingItem] con un icono y texto.
 */
@Preview(showBackground = true, name = "SettingItem - Basic")
@Composable
private fun SettingItemBasicPreview() {
    MaterialTheme {
        SettingItem(
            leadingIcon = { Icon(Icons.Filled.AccountCircle, "Perfil") },
            text = "Nombre de Usuario",
            onClick = {} // Hacemos que sea clicable para la preview
        )
    }
}

/**
 * @brief Previsualización del [SettingItem] con icono, texto y un icono trailing.
 */
@Preview(showBackground = true, name = "SettingItem - With Trailing Icon")
@Composable
private fun SettingItemWithTrailingPreview() {
    MaterialTheme {
        SettingItem(
            leadingIcon = { Icon(Icons.Filled.AccountCircle, "Notificaciones") },
            text = "Notificaciones",
            trailing = { Icon(Icons.Filled.ChevronRight, "Ir a") },
            onClick = {}
        )
    }
}

/**
 * @brief Previsualización del [SettingItem] con un Switch como elemento trailing.
 */
@Preview(showBackground = true, name = "SettingItem - With Switch")
@Composable
private fun SettingItemWithSwitchPreview() {
    var isChecked by remember { mutableStateOf(true) }
    MaterialTheme {
        SettingItem(
            leadingIcon = { Icon(Icons.Filled.AccountCircle, "Modo oscuro") },
            text = "Modo Oscuro",
            trailing = {
                Switch(checked = isChecked, onCheckedChange = { isChecked = it })
            }
            // No onClick para la fila, el Switch maneja su propia interacción
        )
    }
}

/**
 * @brief Previsualización del [SettingItem] sin acción onClick.
 */
@Preview(showBackground = true, name = "SettingItem - Not Clickable")
@Composable
private fun SettingItemNotClickablePreview() {
    MaterialTheme {
        SettingItem(
            leadingIcon = { Icon(Icons.Filled.AccountCircle, "Información") },
            text = "Versión de la App: 1.0.2"
            // onClick es null por defecto, por lo que no será clicable
        )
    }
}