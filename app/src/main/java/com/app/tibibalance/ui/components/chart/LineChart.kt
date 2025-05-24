package com.app.tibibalance.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Dibuja una serie de valores como una línea con puntos clicables.
 *
 * @param values Lista de floats a graficar.
 * @param modifier Modifier para tamaño/posición.
 * @param onPointClick Callback opcional cuando el usuario toca un punto.
 */
@Composable
fun LineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    onPointClick: ((index: Int, value: Float) -> Unit)? = null
) {
    // Índice y valor seleccionados para resaltar
    var selectedPoint by remember { mutableStateOf<Pair<Int, Float>?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (values.isNotEmpty()) {
                        // Calcular índice aproximado según la posición X del tap
                        val stepX = size.width / (values.size - 1)
                        val idx = (offset.x / stepX).roundToInt().coerceIn(0, values.lastIndex)
                        selectedPoint = idx to values[idx]
                        onPointClick?.invoke(idx, values[idx])
                    }
                }
            }
    ) {
        if (values.isEmpty()) return@Canvas

        // Normalizar a [0..1] en Y
        val maxY = values.maxOrNull() ?: 1f

        // Construir lista de puntos en coordenadas de Canvas
        val points = values.mapIndexed { i, v ->
            Offset(
                x = i * (size.width / (values.size - 1)),
                y = size.height * (1f - (v / maxY))
            )
        }

        // 1) Línea que conecta los puntos
        drawPoints(
            points    = points,
            pointMode = PointMode.Polygon,    // ahora sí se resuelve correctamente
            color     = Color.Cyan,           // color de la línea
            strokeWidth = 2.dp.toPx()
        )

        // 2) Círculo en el último punto (valor actual)
        val last = points.last()
        drawCircle(
            color  = Color.Green,
            center = last,
            radius = 4.dp.toPx()
        )

        // 3) Círculo resaltado en el punto seleccionado
        selectedPoint?.let { (idx, _) ->
            val pt = points[idx]
            drawCircle(
                color  = Color.Red,
                center = pt,
                radius = 6.dp.toPx()
            )
        }
    }
}
