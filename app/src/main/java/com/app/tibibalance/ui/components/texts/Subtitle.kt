/**
 * @file    Subtitle.kt
 * @ingroup ui_component_text // Grupo específico para componentes de texto
 * @brief   Define un [Composable] para mostrar texto con el estilo "Subtitle".
 *
 * @details Este archivo contiene el [Composable] `Subtitle`, que es una abstracción
 * sobre el componente [Text] de Jetpack Compose. Aplica un estilo tipográfico
 * específico (tamaño 20sp, peso SemiBold) adecuado para subtítulos o encabezados
 * de sección secundarios. Utiliza el color de texto principal del tema (`onBackground`)
 * por defecto.
 *
 * Proporciona parámetros para personalizar el texto, modificador, color, alineación,
 * número máximo de líneas y comportamiento de desbordamiento.
 *
 * @see Text Componente base de Jetpack Compose utilizado.
 * @see MaterialTheme Usado para obtener el color por defecto.
 * @see Title Composable para títulos principales con mayor énfasis.
 * @see Description Composable para cuerpo de texto estándar.
 * @see Caption Composable para texto secundario o más pequeño.
 * @see TextStyle
 * @see TextAlign
 * @see TextOverflow
 */
package com.app.tibibalance.ui.components.texts

import androidx.compose.foundation.layout.Column // Para previews
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding // Para previews
import androidx.compose.foundation.layout.wrapContentHeight // No aplicable directamente a Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // No aplicable directamente a Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Para previews
import androidx.compose.ui.unit.sp

/**
 * @brief Un [Composable] que muestra texto con estilo "Subtitle".
 *
 * @details Renderiza un componente [Text] aplicando un [TextStyle] predefinido
 * con `FontWeight.SemiBold` y `fontSize = 20.sp`, adecuado para subtítulos
 * o encabezados secundarios. Utiliza el color `onBackground` del tema por defecto.
 * Permite configurar la alineación, el número máximo de líneas y el desbordamiento.
 *
 * @param text El [String] que se mostrará como subtítulo.
 * @param modifier Un [Modifier] opcional para aplicar al componente [Text].
 * @param color El [Color] del texto. Por defecto, es el color `onBackground` del tema.
 * @param textAlign La alineación horizontal del texto ([TextAlign]). Por defecto, `null`
 * (que normalmente resulta en `TextAlign.Start`).
 * @param maxLines El número máximo de líneas a mostrar. Por defecto [Int.MAX_VALUE] (sin límite).
 * @param overflow Cómo se debe manejar el desbordamiento visual si el texto excede
 * el espacio disponible o `maxLines`. Por defecto [TextOverflow.Clip] (corta el texto).
 */
@Composable
fun Subtitle(
    text: String,
    modifier: Modifier = Modifier,
    // Color por defecto: color 'onBackground' del tema
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = null, // Alineación opcional
    maxLines: Int = Int.MAX_VALUE, // Sin límite de líneas por defecto
    overflow: TextOverflow = TextOverflow.Clip // Corta el texto por defecto si excede
) {
    Text(
        text = text, // Texto a mostrar
        modifier = modifier, // Modificador externo
        color = color, // Color del texto
        // Estilo de texto específico para Subtitle
        style = TextStyle(
            fontFamily = FontFamily.Default,  // Fuente estándar
            fontWeight = FontWeight.SemiBold, // Peso semi-negrita
            fontSize = 20.sp, // Tamaño de fuente para subtítulo
            lineHeight = 28.sp // Altura de línea sugerida para 20sp
        ),
        textAlign = textAlign, // Alineación horizontal
        maxLines = maxLines, // Límite de líneas
        overflow = overflow // Manejo de desbordamiento
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [Subtitle] centrado.
 */
@Preview(
    showBackground = true,
    name = "Subtitle Centered", // Nombre descriptivo
    widthDp = 320
    // heightDp quitado para que se ajuste al contenido
)
@Composable
private fun SubtitlePreview() {
    MaterialTheme { // Envuelve en MaterialTheme para estilos correctos
        Subtitle(
            text = "Esta semana",
            modifier = Modifier
                .fillMaxWidth() // Ocupa el ancho para que el centrado funcione
                .padding(vertical = 8.dp), // Añade padding vertical
            textAlign = TextAlign.Center // Centra el texto
        )
    }
}

/**
 * @brief Previsualización del [Subtitle] con texto más largo.
 */
@Preview(showBackground = true, name = "Subtitle Long Text")
@Composable
private fun SubtitleLongTextPreview() {
    MaterialTheme {
        Subtitle(
            text = "Configuración de Notificaciones Avanzada",
            modifier = Modifier.padding(16.dp) // Padding para verlo mejor
        )
    }
}

/**
 * @brief Previsualización del [Subtitle] con un color diferente.
 */
@Preview(showBackground = true, name = "Subtitle Custom Color")
@Composable
private fun SubtitleCustomColorPreview() {
    MaterialTheme {
        Subtitle(
            text = "Sección Importante",
            color = MaterialTheme.colorScheme.tertiary, // Usa color terciario del tema
            modifier = Modifier.padding(16.dp)
        )
    }
}

