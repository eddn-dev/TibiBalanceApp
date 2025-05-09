/**
 * @file    PagerIndicator.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o específicos de paginación
 * @brief   Define un [Composable] para mostrar un indicador de puntos que refleja el estado de un [PagerState].
 *
 * @details Este archivo contiene el [Composable] `PagerIndicator`, que renderiza una fila horizontal
 * de puntos ([Box] con [CircleShape]). El punto correspondiente a la página actual
 * del [PagerState] proporcionado se diferencia visualmente de los demás (puede cambiar
 * de color y/o tamaño). Utiliza animaciones ([animateColorAsState], [animateDpAsState])
 * para transiciones suaves entre los estados activo e inactivo de los puntos.
 *
 * Es útil para acompañar a componentes como [HorizontalPager] o [VerticalPager],
 * proporcionando al usuario una referencia visual de su posición actual dentro
 * de un conjunto de páginas.
 *
 * @see androidx.compose.foundation.pager.PagerState Estado del Pager que controla este indicador.
 * @see androidx.compose.foundation.layout.Row Layout utilizado para disponer los puntos horizontalmente.
 * @see androidx.compose.foundation.layout.Box Usado para dibujar cada punto individual.
 * @see androidx.compose.foundation.shape.CircleShape Forma aplicada a cada punto.
 * @see androidx.compose.animation.animateColorAsState Animación para el cambio de color.
 * @see androidx.compose.animation.core.animateDpAsState Animación para el cambio de tamaño.
 */
package com.app.tibibalance.ui.components

// ui/components/PagerIndicator.kt

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme // Importar para usar colores del tema por defecto
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview // Para Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.PagerState // Importar PagerState
import androidx.compose.foundation.pager.rememberPagerState // Para Preview
import androidx.compose.foundation.ExperimentalFoundationApi // Para PagerState

/**
 * @brief Muestra una fila de puntos indicadores para un [PagerState] de Jetpack Compose Foundation.
 *
 * @details Renderiza una serie de círculos ([Box] con [CircleShape]), uno por cada página
 * definida en `pageCount`. El círculo correspondiente a la `pagerState.currentPage`
 * adopta el `activeColor` y `activeSize`, mientras que los demás usan `inactiveColor`
 * e `inactiveSize`. Las transiciones de color y tamaño son animadas.
 *
 * @param pagerState El [PagerState] obligatorio del Pager que se está indicando. Se usa para
 * obtener la página actual (`currentPage`).
 * @param pageCount El número total de páginas ([Int]) que tiene el Pager. Determina cuántos
 * puntos se dibujarán.
 * @param modifier Un [Modifier] opcional que se aplica al [Row] contenedor de los puntos.
 * Permite personalizar la alineación o el padding del indicador completo.
 * @param activeColor El [Color] del punto que representa la página actual. Por defecto,
 * el color primario del tema (`MaterialTheme.colorScheme.primary`).
 * @param inactiveColor El [Color] de los puntos que representan las páginas inactivas.
 * Por defecto, el color `outline` del tema (`MaterialTheme.colorScheme.outline`),
 * que suele ser un gris claro.
 * @param activeSize El diámetro ([Dp]) del punto activo. Por defecto `8.dp`.
 * @param inactiveSize El diámetro ([Dp]) de los puntos inactivos. Por defecto `8.dp` (igual que el activo).
 * Puedes usar valores diferentes para crear un efecto de tamaño variable.
 * @param spacing El espaciado horizontal ([Dp]) entre cada punto indicador. Por defecto `8.dp`.
 */
@OptIn(ExperimentalFoundationApi::class) // Requerido por PagerState
@Composable
fun PagerIndicator(
    pagerState   : PagerState, // Estado del Pager (obligatorio)
    pageCount    : Int,        // Número total de páginas
    modifier     : Modifier = Modifier, // Modificador para el Row contenedor
    activeColor  : Color = MaterialTheme.colorScheme.primary, // Color activo por defecto
    inactiveColor: Color = MaterialTheme.colorScheme.outline, // Color inactivo por defecto
    activeSize   : Dp   = 8.dp,  // Tamaño activo por defecto
    inactiveSize : Dp   = 8.dp,  // Tamaño inactivo por defecto
    spacing      : Dp   = 8.dp   // Espaciado por defecto
) {
    // Fila para colocar los puntos indicadores horizontalmente
    Row(
        modifier = modifier, // Aplica modificador externo
        horizontalArrangement = Arrangement.spacedBy(spacing) // Espacio entre puntos
        // verticalAlignment = Alignment.CenterVertically // Opcional si el Row tiene altura definida
    ) {
        // Repite la creación de un punto por cada página
        repeat(pageCount) { index ->
            // Determina si el punto actual (index) es la página seleccionada
            val selected = pagerState.currentPage == index
            // Anima el tamaño del punto entre activeSize e inactiveSize
            val size     by animateDpAsState(targetValue = if (selected) activeSize else inactiveSize, label = "IndicatorSizeAnim")
            // Anima el color del punto entre activeColor e inactiveColor
            val color    by animateColorAsState(targetValue = if (selected) activeColor else inactiveColor, label = "IndicatorColorAnim")

            // Dibuja el punto como un Box circular con el tamaño y color animados
            Box(
                Modifier
                    .size(size) // Aplica el tamaño animado
                    .clip(CircleShape) // Forma circular
                    .background(color) // Aplica el color animado
            )
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [PagerIndicator] con diferentes estados.
 */
@OptIn(ExperimentalFoundationApi::class) // Requerido por rememberPagerState
@Preview(showBackground = true, name = "PagerIndicator Preview")
@Composable
private fun PagerIndicatorPreview() {
    // Simula el estado de un Pager con 5 páginas
    val pagerState1 = rememberPagerState(initialPage = 0) { 5 }
    val pagerState2 = rememberPagerState(initialPage = 2) { 5 }
    val pagerState3 = rememberPagerState(initialPage = 4) { 5 }

    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre previews
        ) {
            // Indicador con la primera página activa
            PagerIndicator(pagerState = pagerState1, pageCount = 5)

            // Indicador con la página central activa y tamaños diferentes
            PagerIndicator(
                pagerState = pagerState2,
                pageCount = 5,
                activeSize = 10.dp,
                inactiveSize = 6.dp,
                spacing = 6.dp
            )

            // Indicador con la última página activa y colores personalizados
            PagerIndicator(
                pagerState = pagerState3,
                pageCount = 5,
                activeColor = MaterialTheme.colorScheme.tertiary,
                inactiveColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}