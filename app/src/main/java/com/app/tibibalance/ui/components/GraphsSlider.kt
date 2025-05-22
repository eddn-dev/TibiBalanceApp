// src/main/java/com/app/tibibalance/ui/components/GraphsSlider.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GraphsSlider(
    charts: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    pageHeight: Dp = 200.dp,
    thumbSize: Dp = 60.dp,
    spacing: Dp = 8.dp
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight)
        ) {
            HorizontalPager(
                count = charts.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                charts[page]()
            }

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Prev",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .clickable {
                        scope.launch {
                            val prev = (pagerState.currentPage - 1 + charts.size) % charts.size
                            pagerState.animateScrollToPage(prev)
                        }
                    }
                    .alpha(0.7f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp)
                    .clickable {
                        scope.launch {
                            val next = (pagerState.currentPage + 1) % charts.size
                            pagerState.animateScrollToPage(next)
                        }
                    }
                    .alpha(0.7f)
            )
        }
/*
        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            itemsIndexed(charts) { index, _ ->
                Card(
                    modifier = Modifier
                        .size(thumbSize)
                        .clickable {
                            scope.launch { pagerState.scrollToPage(index) }
                        },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        // miniatura: mismo contenido a escala
                        Box(Modifier.fillMaxSize().padding(8.dp)) {
                            charts[index]()
                        }
                        if (pagerState.currentPage == index) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.2f),
                                modifier = Modifier.matchParentSize()
                            ) {}
                        }
                    }
                }
            }
        }

        */
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun GraphsSliderPreview() {
    // Creamos tres gráficas sencillas directamente en el preview:
    val dummyCharts = listOf<@Composable () -> Unit>(
        {
            Canvas(Modifier.fillMaxSize().background(Color(0xFF3EA8FE))) {
                drawCircle(Color.White, style = Stroke(8f))
            }
        },
        {
            Canvas(Modifier.fillMaxSize().background(Color(0xFFFE3E3E))) {
                drawLine(
                    Color.White,
                    start = center.copy(x = 0f, y = size.height),
                    end = center.copy(x = size.width, y = 0f),
                    strokeWidth = 8f
                )
            }
        },
        {
            Canvas(Modifier.fillMaxSize().background(Color(0xFF3EDE3E))) {
                drawRect(Color.White, style = Stroke(8f))
            }
        }
    )

    MaterialTheme {
        GraphsSlider(
            charts = dummyCharts,
            pageHeight = 180.dp,
            thumbSize  = 50.dp,
            spacing    = 8.dp
        )
    }
}
