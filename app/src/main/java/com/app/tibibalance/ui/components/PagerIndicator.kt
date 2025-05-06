package com.app.tibibalance.ui.components

// ui/components/PagerIndicator.kt

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.PagerState  // foundation 1.4+

/**
 * Indicador de puntos para Horizontal/VerticalPager.
 *
 * @param pagerState   estado del Pager (obligatorio)
 * @param pageCount    número total de páginas
 * @param activeColor  color del punto seleccionado
 * @param inactiveColor color del punto inactivo
 * @param activeSize   diámetro del punto seleccionado
 * @param inactiveSize diámetro de los puntos inactivos
 * @param spacing      espacio horizontal entre puntos
 */
@Composable
fun PagerIndicator(
    pagerState   : PagerState,
    pageCount    : Int,
    activeColor  : Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outline,
    activeSize   : Dp   = 12.dp,
    inactiveSize : Dp   = 8.dp,
    spacing      : Dp   = 8.dp,
    modifier     : Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(pageCount) { index ->
            val selected = pagerState.currentPage == index
            val size     by animateDpAsState(if (selected) activeSize else inactiveSize)
            val color    by animateColorAsState(if (selected) activeColor else inactiveColor)

            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
