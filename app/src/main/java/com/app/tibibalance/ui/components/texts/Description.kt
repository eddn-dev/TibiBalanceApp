/**
 * @file    Description.kt
 * @ingroup ui_component_text // Grupo específico para componentes de texto
 * @brief   Define un [Composable] para mostrar texto con el estilo "Description" o cuerpo de texto estándar.
 *
 * @details Este archivo contiene el [Composable] `Description`, que es una abstracción
 * sobre el componente [Text] de Jetpack Compose. Aplica un estilo tipográfico
 * estándar para párrafos o descripciones (tamaño 16sp, peso normal) y utiliza
 * el color de texto principal del tema (`onBackground`) por defecto.
 *
 * Es adecuado para mostrar la mayor parte del contenido textual informativo en la aplicación.
 * Permite la personalización del color, alineación, número máximo de líneas y
 * comportamiento de desbordamiento (overflow).
 *
 * @see Text Componente base de Jetpack Compose utilizado.
 * @see MaterialTheme Usado para obtener el color por defecto.
 * @see Caption Composable para texto secundario o más pequeño.
 * @see Subtitle Composable para subtítulos o texto con más énfasis que Description.
 * @see Title Composable para títulos principales.
 * @see TextStyle
 * @see TextAlign
 * @see TextOverflow
 */
package com.app.tibibalance.ui.components.texts

import androidx.compose.foundation.layout.Column // Para previews
import androidx.compose.foundation.layout.Spacer // Para previews
import androidx.compose.foundation.layout.height // Para previews
import androidx.compose.foundation.layout.padding // Para previews
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.dp // Para previews
import androidx.compose.ui.unit.sp

/**
 * @brief Un [Composable] que muestra texto con el estilo estándar de cuerpo o descripción.
 *
 * @details Renderiza un componente [Text] aplicando un [TextStyle] predefinido
 * con `FontWeight.Normal` y `fontSize = 16.sp`, adecuado para la mayoría de los
 * párrafos de texto. Utiliza el color `onBackground` del tema por defecto.
 * Permite configurar la alineación, el número máximo de líneas y el desbordamiento.
 *
 * @param text El [String] que se mostrará.
 * @param modifier Un [Modifier] opcional para aplicar al componente [Text].
 * @param color El [Color] del texto. Por defecto, es el color `onBackground` del tema.
 * @param textAlign La alineación horizontal del texto ([TextAlign]). Por defecto, `null`
 * (que normalmente resulta en `TextAlign.Start`).
 * @param maxLines El número máximo de líneas a mostrar. Por defecto [Int.MAX_VALUE] (sin límite).
 * @param overflow Cómo se debe manejar el desbordamiento visual si el texto excede
 * el espacio disponible o `maxLines`. Por defecto [TextOverflow.Clip] (corta el texto).
 * Otras opciones comunes son [TextOverflow.Ellipsis].
 */
@Composable
fun Description(
    text: String,
    modifier: Modifier = Modifier,
    // Color por defecto: color 'onBackground' del tema (texto principal sobre fondo)
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = null, // Alineación opcional
    maxLines: Int = Int.MAX_VALUE, // Sin límite de líneas por defecto
    overflow: TextOverflow = TextOverflow.Clip // Corta el texto por defecto si excede
) {
    Text(
        text = text, // Texto a mostrar
        modifier = modifier, // Modificador externo
        color = color, // Color del texto
        // Estilo de texto específico para Description/Body
        style = TextStyle(
            fontFamily = FontFamily.Default,  // Fuente estándar (Roboto en Android)
            fontWeight = FontWeight.Normal, // Peso de fuente normal (estándar)
            fontSize = 16.sp, // Tamaño de fuente estándar para cuerpo
            lineHeight = 24.sp // Altura de línea estándar para 16sp (Material Design)
        ),
        textAlign = textAlign, // Alineación horizontal
        maxLines = maxLines, // Límite de líneas
        overflow = overflow // Manejo de desbordamiento
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [Description] con texto normal.
 */
@Preview(showBackground = true, name = "Description Text Preview")
@Composable
private fun DescriptionPreview() {
    MaterialTheme {
        Description(
            text = "Este es un texto descriptivo estándar, utilizado para párrafos de cuerpo o explicaciones generales en la interfaz de usuario.",
            modifier = Modifier.padding(16.dp) // Añade padding para ver mejor
        )
    }
}

/**
 * @brief Previsualización del [Description] con límite de líneas y elipsis.
 */
@Preview(showBackground = true, name = "Description Text Max Lines")
@Composable
private fun DescriptionMaxLinesPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            Description(
                text = "Este texto es un poco más largo y se cortará después de dos líneas para demostrar el efecto de maxLines y el overflow con puntos suspensivos. El resto del texto no será visible.",
                maxLines = 2, // Limita a 2 líneas
                overflow = TextOverflow.Ellipsis // Muestra "..."
            )
            Spacer(Modifier.height(8.dp))
            Description(
                text = "Este es otro párrafo normal."
            )
        }
    }
}

/**
 * @brief Previsualización del [Description] con un color diferente.
 */
@Preview(showBackground = true, name = "Description Text Custom Color")
@Composable
private fun DescriptionCustomColorPreview() {
    MaterialTheme {
        Description(
            text = "Texto descriptivo con color secundario.",
            color = MaterialTheme.colorScheme.secondary, // Usa el color secundario del tema
            modifier = Modifier.padding(16.dp)
        )
    }
}
