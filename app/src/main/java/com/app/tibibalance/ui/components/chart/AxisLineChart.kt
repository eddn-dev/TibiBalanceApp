// src/main/java/com/app/tibibalance/ui/components/chart/AxisLineChart.kt

package com.app.tibibalance.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Composable que dibuja un gráfico de líneas con ejes X/Y,
 * marcas, etiquetas y puntos interactivos.
 *
 * @param values     Lista de valores a graficar.
 * @param modifier   Modifier para tamaño y posición.
 * @param axisColor  Color de los ejes y marcas.
 * @param lineColor  Color de la línea de datos.
 * @param pointColor Color para los puntos seleccionados.
 * @param labelCount Número de divisiones en cada eje.
 */
@Composable
fun AxisLineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    axisColor: Color = Color.Gray,
    lineColor: Color = Color(0xFF3EA8FE),
    pointColor: Color = Color.Red,
    labelCount: Int = 5
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Canvas(
        modifier = modifier.pointerInput(values) {
            detectTapGestures { pos ->
                if (values.isNotEmpty()) {
                    val stepX = size.width / (values.size - 1)
                    selectedIndex = (pos.x / stepX).roundToInt().coerceIn(0, values.lastIndex)
                }
            }
        }
    ) {
        if (values.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val minY = values.minOrNull() ?: 0f
        val maxY = values.maxOrNull() ?: 1f
        val marginLeft = 32.dp.toPx()
        val marginBottom = 24.dp.toPx()
        // Dibujar ejes
        drawLine(axisColor, Offset(marginLeft, 0f), Offset(marginLeft, h - marginBottom), strokeWidth = 2f)
        drawLine(axisColor, Offset(marginLeft, h - marginBottom), Offset(w, h - marginBottom), strokeWidth = 2f)
        // Etiquetas Y
        for (i in 0..labelCount) {
            val y = (h - marginBottom) - (h - marginBottom) * i / labelCount
            drawLine(axisColor, Offset(marginLeft - 4f, y), Offset(marginLeft, y), strokeWidth = 1f)
            val label = String.format("%.1f", minY + (maxY - minY) * i / labelCount)
            drawContext.canvas.nativeCanvas.apply {
                drawText(label, 0f, y + 4.dp.toPx(), android.graphics.Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.LEFT
                })
            }
        }
        // Etiquetas X
        val stepX = (w - marginLeft) / (values.size - 1)
        val xStep = (values.size.coerceAtLeast(1) / labelCount).coerceAtLeast(1)
        for (i in 0 until values.size step xStep) {
            val x = marginLeft + stepX * i
            drawLine(axisColor, Offset(x, h - marginBottom), Offset(x, h - marginBottom + 4f), strokeWidth = 1f)
            drawContext.canvas.nativeCanvas.apply {
                drawText(i.toString(), x, h - marginBottom + 16.dp.toPx(), android.graphics.Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                })
            }
        }
        // Puntos y línea
        val points = values.mapIndexed { i, v ->
            val x = marginLeft + stepX * i
            val y = (h - marginBottom) - (h - marginBottom) * (v - minY) / (maxY - minY)
            Offset(x, y)
        }
        points.zipWithNext { a, b ->
            drawLine(lineColor, start = a, end = b, strokeWidth = 2.dp.toPx())
        }
        points.forEachIndexed { idx, pt ->
            drawCircle(
                color = if (idx == selectedIndex) pointColor else lineColor,
                radius = if (idx == selectedIndex) 6.dp.toPx() else 4.dp.toPx(),
                center = pt
            )
        }
    }
}
