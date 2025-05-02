// file: ui/components/Title.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp

@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = TextStyle(
            fontFamily = FontFamily.Default,    // Roboto por defecto
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 25
)
@Composable
fun TitlePreview() {
    Title(
        text = "Título de Prueba",
        modifier = Modifier.fillMaxWidth(),      // ocupa todo el ancho
        textAlign = TextAlign.Center             // centra el texto
    )
}
