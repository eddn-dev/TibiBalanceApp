/**
 * @file    Title.kt
 * @ingroup ui_component_text // Grupo específico para componentes de texto
 * @brief   Define un [Composable] para mostrar texto con el estilo "Title".
 *
 * @details Este archivo contiene el [Composable] `Title`, que es una abstracción
 * sobre el componente [Text] de Jetpack Compose. Aplica un estilo tipográfico
 * prominente (tamaño 24sp, peso Bold) adecuado para títulos principales de
 * pantallas o secciones importantes. Utiliza el color de texto principal del tema
 * (`onBackground`) por defecto.
 *
 * Proporciona parámetros para personalizar el texto, modificador, color, alineación,
 * número máximo de líneas y comportamiento de desbordamiento.
 *
 * @see Text Componente base de Jetpack Compose utilizado.
 * @see MaterialTheme Usado para obtener el color por defecto.
 * @see Subtitle Composable para subtítulos o texto con menos énfasis.
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Para previews
import androidx.compose.ui.unit.sp

/**
 * @brief Un [Composable] que muestra texto con estilo "Title".
 *
 * @details Renderiza un componente [Text] aplicando un [TextStyle] predefinido
 * con `FontWeight.Bold` y `fontSize = 24.sp`, adecuado para títulos principales
 * y encabezados de pantalla. Utiliza el color `onBackground` del tema por defecto.
 * Permite configurar la alineación, el número máximo de líneas y el desbordamiento.
 *
 * @param text El [String] que se mostrará como título.
 * @param modifier Un [Modifier] opcional para aplicar al componente [Text].
 * @param color El [Color] del texto. Por defecto, es el color `onBackground` del tema.
 * @param textAlign La alineación horizontal del texto ([TextAlign]). Por defecto, `null`
 * (que normalmente resulta en `TextAlign.Start`).
 * @param maxLines El número máximo de líneas a mostrar. Por defecto [Int.MAX_VALUE] (sin límite).
 * @param overflow Cómo se debe manejar el desbordamiento visual si el texto excede
 * el espacio disponible o `maxLines`. Por defecto [TextOverflow.Clip] (corta el texto).
 */
@Composable
fun Title(
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
        // Estilo de texto específico para Title
        style = TextStyle(
            fontFamily = FontFamily.Default,    // Fuente estándar
            fontWeight = FontWeight.Bold, // Peso negrita para énfasis
            fontSize = 24.sp, // Tamaño de fuente grande para títulos
            lineHeight = 32.sp // Altura de línea sugerida para 24sp
        ),
        textAlign = textAlign, // Alineación horizontal
        maxLines = maxLines, // Límite de líneas
        overflow = overflow // Manejo de desbordamiento
    )
}

// --- Previews ---

/**
 * @brief Previsualización del [Title] centrado.
 */
@Preview(
    showBackground = true,
    name = "Title Preview Centered", // Nombre descriptivo
    widthDp = 320
)
@Composable
private fun TitlePreview() {
    MaterialTheme { // Envuelve en MaterialTheme para estilos correctos
        Title(
            text = "Título de Prueba",
            modifier = Modifier
                .fillMaxWidth() // Ocupa el ancho para que el centrado funcione
                .padding(vertical = 8.dp), // Añade padding vertical
            textAlign = TextAlign.Center // Centra el texto
        )
    }
}

/**
 * @brief Previsualización del [Title] con texto más largo.
 */
@Preview(showBackground = true, name = "Title Preview Long")
@Composable
private fun TitleLongPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) { // Usa Column para padding
            Title(
                text = "Este es un Título Bastante Largo para Probar el Ajuste de Línea"
            )
        }
    }
}

/**
 * @brief Previsualización del [Title] con un color diferente.
 */
@Preview(showBackground = true, name = "Title Custom Color")
@Composable
private fun TitleCustomColorPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            Title(
                text = "Título Destacado",
                color = MaterialTheme.colorScheme.primary // Usa color primario del tema
            )
        }
    }
}
