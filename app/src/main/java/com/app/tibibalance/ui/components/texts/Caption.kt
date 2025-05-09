/**
 * @file    Caption.kt
 * @ingroup ui_component_text // Grupo específico para componentes de texto
 * @brief   Define un [Composable] para mostrar texto con el estilo "Caption".
 *
 * @details Este archivo contiene el [Composable] `Caption`, que es una abstracción
 * sobre el componente [Text] de Jetpack Compose. Aplica un estilo tipográfico
 * específico (tamaño pequeño 12sp, peso ligero) y un color semitransparente
 * por defecto, adecuado para textos secundarios, pies de foto, o información
 * adicional de baja prominencia visual.
 *
 * Permite la personalización del color, alineación, número máximo de líneas y
 * comportamiento de desbordamiento (overflow).
 *
 * @see Text Componente base de Jetpack Compose utilizado.
 * @see MaterialTheme Usado para obtener el color por defecto.
 * @see TextStyle
 * @see TextAlign
 * @see TextOverflow
 */
package com.app.tibibalance.ui.components.texts

import androidx.compose.foundation.layout.Column // Para previews
import androidx.compose.foundation.layout.Spacer // Para previews
import androidx.compose.foundation.layout.fillMaxWidth // Para previews
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
 * @brief Un [Composable] que muestra texto con estilo "Caption".
 *
 * @details Renderiza un componente [Text] aplicando un [TextStyle] predefinido
 * con `FontWeight.Light` y `fontSize = 12.sp`. Utiliza un color semitransparente
 * (`onBackground` con 70% de opacidad) por defecto, ideal para textos secundarios.
 * Permite configurar la alineación, el número máximo de líneas y el desbordamiento.
 *
 * @param text El [String] que se mostrará.
 * @param modifier Un [Modifier] opcional para aplicar al componente [Text].
 * @param color El [Color] del texto. Por defecto, es el color `onBackground` del tema
 * con una transparencia alfa de 0.7f.
 * @param textAlign La alineación horizontal del texto ([TextAlign]). Por defecto, `null`
 * (que normalmente resulta en `TextAlign.Start`).
 * @param maxLines El número máximo de líneas a mostrar. Por defecto [Int.MAX_VALUE] (sin límite).
 * @param overflow Cómo se debe manejar el desbordamiento visual si el texto excede
 * el espacio disponible o `maxLines`. Por defecto [TextOverflow.Clip] (corta el texto).
 * Otras opciones comunes son [TextOverflow.Ellipsis].
 */
@Composable
fun Caption(
    text: String,
    modifier: Modifier = Modifier,
    // Color por defecto: color 'onBackground' del tema pero semitransparente
    color: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
    textAlign: TextAlign? = null, // Alineación opcional
    maxLines: Int = Int.MAX_VALUE, // Sin límite de líneas por defecto
    overflow: TextOverflow = TextOverflow.Clip // Corta el texto por defecto si excede
) {
    Text(
        text = text, // Texto a mostrar
        modifier = modifier, // Modificador externo
        color = color, // Color del texto
        // Estilo de texto específico para Caption
        style = TextStyle(
            fontFamily = FontFamily.Default,  // Fuente estándar (Roboto en Android)
            fontWeight = FontWeight.Light, // Peso de fuente ligero
            fontSize = 12.sp, // Tamaño de fuente pequeño
            lineHeight = 16.sp // Altura de línea ligeramente mayor para legibilidad (opcional)
        ),
        textAlign = textAlign, // Alineación horizontal
        maxLines = maxLines, // Límite de líneas
        overflow = overflow // Manejo de desbordamiento
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [Caption] con texto largo y límite de líneas.
 */
@Preview(showBackground = true, name = "Caption Text Preview")
@Composable
private fun CaptionPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) { // Añade padding para ver mejor
            Caption(
                text = "Este es un texto de tipo caption, usualmente usado para información secundaria, pies de foto, o notas aclaratorias que no deben tener mucho énfasis visual.",
                maxLines = 2, // Limita a 2 líneas
                overflow = TextOverflow.Ellipsis // Muestra "..." si excede
            )
            Spacer(Modifier.height(8.dp))
            Caption(
                text = "Otro caption más corto."
            )
        }
    }
}

/**
 * @brief Previsualización del [Caption] con alineación centrada.
 */
@Preview(showBackground = true, name = "Caption Text Centered")
@Composable
private fun CaptionCenteredPreview() {
    MaterialTheme {
        Caption(
            text = "Texto caption centrado en el ancho disponible.",
            textAlign = TextAlign.Center, // Alineación centrada
            modifier = Modifier
                .fillMaxWidth() // Necesario para que el centrado tenga efecto
                .padding(16.dp)
        )
    }
}

/**
 * @brief Previsualización del [Caption] con un color diferente.
 */
@Preview(showBackground = true, name = "Caption Text Custom Color")
@Composable
private fun CaptionCustomColorPreview() {
    MaterialTheme {
        Caption(
            text = "Caption con color primario.",
            color = MaterialTheme.colorScheme.primary, // Usa el color primario del tema
            modifier = Modifier.padding(16.dp)
        )
    }
}
