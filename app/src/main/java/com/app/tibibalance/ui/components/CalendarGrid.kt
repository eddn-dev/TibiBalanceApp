/**
 * @file    CalendarGrid.kt
 * @ingroup ui_component_layout // Grupo para componentes de layout o complejos
 * @brief   Define un [Composable] para mostrar una vista de calendario mensual.
 *
 * @details Este archivo contiene la data class [EmotionDay] que representa el estado
 * de un día individual en el calendario (número, icono de emoción, selección) y
 * el [Composable] [CalendarGrid] que renderiza la cuadrícula completa del mes,
 * incluyendo el nombre del mes, los encabezados de los días de la semana (Do-Sa)
 * y las celdas para cada día.
 *
 * Cada celda del día es interactiva ([clickable]) y puede mostrar opcionalmente
 * un icono (representando una emoción registrada) y el número del día. El estado
 * de selección visual (`isSelected`) también se maneja.
 *
 * @see EmotionDay Data class para el estado de un día.
 * @see CalendarGrid El componente principal del calendario.
 * @see Column Layout vertical principal.
 * @see Row Layout horizontal para encabezados y semanas.
 * @see Surface Componente usado para cada celda del día.
 * @see Box Contenedor para superponer icono y número en la celda.
 * @see ImageContainer Composable (asumido) para mostrar el icono de emoción.
 * @see Text Composable para mostrar mes, días de semana y número del día.
 * @see Modifier.chunked Extensión útil para agrupar días en semanas.
 */
package com.app.tibibalance.ui.components

import androidx.annotation.DrawableRes // Importar para anotar emotionRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Importar remember y mutableStateOf para previews
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.R // Asegúrate que R está importado correctamente

/**
 * @brief Data class que representa el estado y contenido de un día en el [CalendarGrid].
 * @details Contiene la información necesaria para renderizar una celda individual del calendario.
 *
 * @property day El número del día del mes ([Int]). Es `null` si la celda corresponde a un día
 * fuera del mes actual (relleno al principio o final de la cuadrícula).
 * @property emotionRes El ID del recurso drawable ([DrawableRes]) para el icono de la emoción
 * asociada a este día. Es `null` si no hay emoción registrada o si `day` es `null`.
 * @property isSelected Indica si esta celda de día está actualmente seleccionada (`true`)
 * o no (`false`). Afecta al estilo visual de la celda. Por defecto `false`.
 * @property onClick La función lambda que se ejecuta cuando el usuario pulsa sobre esta celda.
 * Se activa solo si `day` no es `null`. Por defecto, una lambda vacía.
 */
data class EmotionDay(
    val day: Int?,
    @DrawableRes val emotionRes: Int?, // Anotación para indicar que es un ID de Drawable
    val isSelected: Boolean = false, // No seleccionado por defecto
    val onClick: () -> Unit = {} // Acción vacía por defecto
)

/**
 * @brief Un [Composable] que renderiza una cuadrícula de calendario mensual mostrando días y emociones asociadas.
 *
 * @details Muestra el nombre del mes, los encabezados de los días de la semana (Do-Sa),
 * y una cuadrícula de celdas. Cada celda representa un día y utiliza la información
 * de la lista [days] ([EmotionDay]) para mostrar el número del día, un icono de emoción
 * (si existe), y aplicar un estilo visual si está seleccionada. Las celdas de días válidos
 * son clicables.
 *
 * @param month El [String] con el nombre del mes y año a mostrar como título (e.g., "Mayo 2025").
 * @param days Una [List] de [EmotionDay] que representa todos los días a mostrar en la cuadrícula.
 * **Importante:** La lista debe contener elementos para todas las celdas de la cuadrícula,
 * incluyendo los días del mes anterior/siguiente que sean necesarios para completar la
 * primera y última semana. Los días fuera del mes actual deben tener `day = null`.
 * La lista debe tener un tamaño múltiplo de 7.
 * @param modifier Un [Modifier] opcional para aplicar al [Column] contenedor principal.
 */
@Composable
fun CalendarGrid(
    month: String,
    days: List<EmotionDay>, // Lista que representa todas las celdas
    modifier: Modifier = Modifier
) {
    // Nombres de los días de la semana para el encabezado (Domingo primero)
    val weekDays = listOf("Do","Lu","Ma","Mi","Ju","Vi","Sa")

    // Columna principal que organiza el título, encabezado y cuadrícula
    Column(modifier = modifier.fillMaxWidth()) {
        // Título: Nombre del mes
        Text(
            text      = month,
            style     = MaterialTheme.typography.titleMedium, // Estilo del título
            modifier  = Modifier
                .fillMaxWidth() // Ocupa todo el ancho
                .padding(bottom = 8.dp), // Espacio inferior
            textAlign = TextAlign.Start // Alineación al inicio (izquierda)
        )
        // Encabezado: Nombres de los días de la semana
        Row(
            modifier              = Modifier.fillMaxWidth(), // Ocupa todo el ancho
            horizontalArrangement = Arrangement.SpaceAround // Distribuye con espacio alrededor
        ) {
            weekDays.forEach { weekDayLabel ->
                Text(
                    text = weekDayLabel, // "Do", "Lu", etc.
                    style     = MaterialTheme.typography.bodySmall, // Estilo pequeño
                    modifier  = Modifier.weight(1f), // Cada etiqueta ocupa el mismo espacio
                    textAlign = TextAlign.Center // Texto centrado
                )
            }
        }
        Spacer(Modifier.height(4.dp)) // Pequeño espacio

        // Cuadrícula de días: Agrupa los días en semanas (listas de 7)
        days.chunked(7).forEach { weekItems ->
            // Fila para representar una semana
            Row(
                modifier              = Modifier.fillMaxWidth(),
                // Distribuye las celdas de los días uniformemente
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Itera sobre los 7 elementos (días o placeholders) de la semana
                weekItems.forEach { dayItem ->
                    // Celda individual para un día
                    Surface(
                        modifier = Modifier
                            .weight(1f) // Cada celda ocupa el mismo ancho
                            .aspectRatio(1f) // Hace que la celda sea cuadrada
                            .padding(2.dp) // Pequeño espacio alrededor de la celda
                            // Hace la celda clicable solo si es un día válido (day != null)
                            .clickable(enabled = dayItem.day != null) { dayItem.onClick() },
                        shape          = RoundedCornerShape(8.dp), // Bordes redondeados
                        // Color de fondo condicional: más oscuro si está seleccionado
                        color          = if (dayItem.isSelected) Color(0xFF85C3DE) else Color(0x40AED3E3),
                        // Borde condicional: solo visible si está seleccionado
                        border         = if (dayItem.isSelected) BorderStroke(1.dp, Color(0xFF85C3DE)) else null,
                        tonalElevation = 0.dp // Sin elevación adicional
                    ) {
                        // Contenedor para superponer icono y número del día
                        Box(
                            modifier = Modifier.fillMaxSize(), // Ocupa toda la celda Surface
                            contentAlignment = Alignment.Center // Centra el contenido
                        ) {
                            // Muestra el icono de emoción si existe
                            dayItem.emotionRes?.let { resourceId ->
                                // Asume que ImageContainer es un Composable existente
                                ImageContainer(
                                    resId = resourceId,
                                    contentDescription = null, // Icono decorativo
                                    modifier = Modifier.size(32.dp) // Tamaño del icono
                                )
                            }
                            // Muestra el número del día si existe
                            dayItem.day?.let { dayNumber ->
                                Text(
                                    text      = dayNumber.toString(), // Número del día
                                    fontSize  = 16.sp, // Tamaño del texto
                                    // Color blanco semitransparente para que sea visible sobre el icono
                                    color     = Color.White.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center, // Centrado
                                    modifier  = Modifier.align(Alignment.Center) // Asegura alineación central
                                )
                            }
                            // Si dayItem.day es null, el Box queda vacío (celda placeholder)
                        }
                    }
                }
                // Si la semana procesada por chunked tiene menos de 7 días (última semana incompleta),
                // añade Spacers para mantener la estructura de la cuadrícula.
                if (weekItems.size < 7) {
                    repeat(7 - weekItems.size) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp))
                    }
                }
            }
        }
    }
}

// --- Preview ---

/**
 * @brief Previsualización Composable para [CalendarGrid].
 * @details Muestra un ejemplo del calendario para un mes, incluyendo días vacíos
 * al inicio, días con diferentes iconos de emoción, un día seleccionado y días
 * sin emoción registrada.
 */
@Preview(showBackground = true, name = "Calendar Grid Preview", widthDp = 360)
@Composable
private fun PreviewCalendarGrid() {
    // Genera una lista de ejemplo más realista para un mes
    val sampleDays = mutableListOf<EmotionDay>()
    // Añade 3 días vacíos al principio (simulando inicio de mes en Jueves, si Do=0)
    repeat(3) { sampleDays.add(EmotionDay(null, null)) }
    // Añade algunos días con datos
    sampleDays.add(EmotionDay(1, R.drawable.iconhappyimage, isSelected = true, onClick = {}))
    sampleDays.add(EmotionDay(2, R.drawable.iconsadimage, onClick = {}))
    sampleDays.add(EmotionDay(3, R.drawable.iconcalmimage, onClick = {}))
    sampleDays.add(EmotionDay(4, R.drawable.iconangryimage, onClick = {}))
    sampleDays.add(EmotionDay(5, R.drawable.icondisgustimage, onClick = {}))
    sampleDays.add(EmotionDay(6, R.drawable.iconfearimage, onClick = {}))
    sampleDays.add(EmotionDay(7, R.drawable.iconhappyimage, onClick = {}))
    sampleDays.add(EmotionDay(8, null, onClick = {})) // Día sin emoción registrada
    sampleDays.add(EmotionDay(9, R.drawable.iconcalmimage, onClick = {}))
    // Completa hasta 31 días (ejemplo) sin emoción
    for (dayNum in 10..31) {
        sampleDays.add(EmotionDay(dayNum, null, onClick = {}))
    }
    // Añade días vacíos al final para completar la última semana (hasta 35 celdas = 5 semanas)
    repeat(35 - sampleDays.size) { sampleDays.add(EmotionDay(null, null)) }


    MaterialTheme {
        Column(Modifier.padding(16.dp)) { // Añade padding externo
            CalendarGrid(
                month = "Mayo 2025", // Nombre del mes
                days  = sampleDays // Pasa la lista de días generada
            )
        }
    }
}
