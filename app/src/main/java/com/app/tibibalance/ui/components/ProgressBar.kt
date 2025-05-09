/**
 * @file    ProgressBar.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o indicadores
 * @brief   Define un [Composable] para una barra de progreso lineal simple con texto de porcentaje.
 *
 * @details Este archivo contiene el [Composable] `ProgressBar`, que renderiza una barra de progreso
 * visualmente. Consiste en un [Box] contenedor (el "track" o fondo) y otro [Box]
 * superpuesto que representa el progreso actual (la "barra"). Ambos tienen esquinas
 * redondeadas. Debajo de la barra, muestra un [Text] con el valor porcentual numérico.
 *
 * Permite personalizar los colores de la barra y del track, así como el valor
 * del porcentaje a mostrar. Es útil para visualizar el avance hacia una meta
 * (e.g., pasos diarios, minutos de ejercicio).
 *
 * @see androidx.compose.foundation.layout.Box Usado para dibujar el track y la barra de progreso.
 * @see androidx.compose.foundation.layout.Column Usado para apilar la barra y el texto.
 * @see androidx.compose.foundation.shape.RoundedCornerShape Usado para las esquinas redondeadas.
 * @see androidx.compose.material3.Text Usado para mostrar el porcentaje.
 */
// file: ui/components/ProgressBar.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // Para Preview
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * @brief Un [Composable] que muestra una barra de progreso lineal con un texto de porcentaje debajo.
 *
 * @details Dibuja una barra de fondo (`trackColor`) y una barra de progreso superpuesta
 * (`barColor`) cuyo ancho es proporcional al `percent` proporcionado. Ambos elementos
 * tienen esquinas redondeadas. Debajo, un componente [Text] muestra el valor numérico
 * del porcentaje. El valor `percent` se asegura de estar entre 0 y 100.
 *
 * @param percent El porcentaje de progreso a mostrar ([Int]), se espera un valor entre 0 y 100.
 * Valores fuera de este rango serán acotados a 0 o 100.
 * @param modifier Un [Modifier] opcional que se aplica al [Column] contenedor principal.
 * Por defecto aplica un padding superior de 5.dp y ocupa el ancho máximo.
 * @param barColor El [Color] de la barra que representa el progreso actual.
 * Por defecto es un verde claro (`0xFFBCE2C2`).
 * @param trackColor El [Color] de la barra de fondo (el "track") sobre la cual se dibuja
 * la barra de progreso. Por defecto es un gris claro (`0xFFE0E0E0`).
 */
@Composable
fun ProgressBar(
    percent: Int,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFFBCE2C2), // Color verde claro por defecto para la barra
    trackColor: Color = Color(0xFFE0E0E0) // Color gris claro por defecto para el fondo
) {
    // Columna para agrupar la barra y el texto
    Column(
        modifier = modifier
            .padding(top = 5.dp) // Padding superior
            .fillMaxWidth(), // Ocupa el ancho disponible
        horizontalAlignment = Alignment.CenterHorizontally, // Centra la barra y el texto
    ) {
        // Box que actúa como el fondo o 'track' de la barra de progreso
        Box(
            modifier = Modifier
                .width(160.dp) // Ancho fijo para la barra
                .height(18.dp) // Altura fija
                .clip(RoundedCornerShape(9.dp)) // Bordes redondeados (radio = altura / 2)
                .background(trackColor) // Color de fondo del track

        ) {
            // Box que representa el progreso actual, superpuesto al track
            Box(
                modifier = Modifier
                    .fillMaxHeight() // Ocupa toda la altura del track
                    // Ancho fraccional basado en el porcentaje (asegurado entre 0 y 1)
                    .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                    .clip(RoundedCornerShape(9.dp)) // Mismos bordes redondeados
                    .background(barColor) // Color de la barra de progreso
            )
        }
        // Espacio entre la barra y el texto del porcentaje
        Spacer(modifier = Modifier.height(4.dp))
        // Texto que muestra el porcentaje numérico
        Text(
            text = "$percent%", // Formatea el número con el símbolo '%'
            fontSize = 12.sp, // Tamaño de fuente pequeño
            fontWeight = FontWeight.Medium, // Peso de fuente medio
            color = Color(0xFF000000), // Color negro para el texto (podría usar MaterialTheme.colorScheme.onSurface)
            textAlign = TextAlign.Center, // Texto centrado
            modifier = Modifier.fillMaxWidth() // Ocupa el ancho para el centrado
        )
    }
}

/**
 * @brief Previsualización del [ProgressBar] mostrando un 75% de progreso.
 */
@Preview(showBackground = true, widthDp = 200, heightDp = 60) // Ajustada altura para ver texto
@Composable
fun ProgressBarPreview() {
    MaterialTheme { // Envuelve en MaterialTheme para estilos de texto
        ProgressBar(percent = 75) // Muestra un 75% de progreso
    }
}

/**
 * @brief Previsualización del [ProgressBar] mostrando un 0% de progreso.
 */
@Preview(showBackground = true, widthDp = 200, heightDp = 60)
@Composable
fun ProgressBarEmptyPreview() {
    MaterialTheme {
        ProgressBar(percent = 0) // Muestra 0%
    }
}

/**
 * @brief Previsualización del [ProgressBar] mostrando un 100% de progreso.
 */
@Preview(showBackground = true, widthDp = 200, heightDp = 60)
@Composable
fun ProgressBarFullPreview() {
    MaterialTheme {
        ProgressBar(percent = 100) // Muestra 100%
    }
}