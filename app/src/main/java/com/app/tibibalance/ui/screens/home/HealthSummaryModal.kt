
// src/main/java/com/app/tibibalance/ui/screens/home/HealthSummaryModal.kt

package com.app.tibibalance.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.GraphsSlider
import com.app.tibibalance.ui.components.chart.AxisLineChart
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * @file    HealthSummaryModal.kt
 * @brief   Modal “Resumen de Salud” con pestañas Semanal/Mensual mostrando datos filtrados.
 *
 * @param weekly   Serie resumen de la semana actual (hasta hoy, máximo 7 días).
 * @param monthly  Serie resumen del mes actual (desde día 1 hasta hoy).
 * @param onDismissRequest  Callback para cerrar el modal.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HealthSummaryModal(
    weekly: HealthSeries2,
    monthly: HealthSeries2,
    onDismissRequest: () -> Unit
) {
    // Definición de pestañas
    val tabs = listOf("Semanal", "Mensual")
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    // Permitir cerrar con botón atrás
    BackHandler { onDismissRequest() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cerrar")
            }
        },
        title = {
            Text("Resumen de Salud", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // TabRow de selección
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Pager con dos páginas
                HorizontalPager(
                    count = tabs.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) { page ->
                    // Selección de datos según página
                    val data = if (page == 0) weekly else monthly

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Carrusel de tres gráficas: pasos, calorías, FC
                        GraphsSlider(
                            charts = listOf<@Composable () -> Unit>(
                                {
                                    Column(Modifier.fillMaxSize()) {
                                        Text("Pasos", Modifier.align(Alignment.CenterHorizontally))
                                        AxisLineChart(
                                            values = data.steps,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                        )
                                    }
                                },
                                {
                                    Column(Modifier.fillMaxSize()) {
                                        Text("Calorías", Modifier.align(Alignment.CenterHorizontally))
                                        AxisLineChart(
                                            values = data.cals,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                        )
                                    }
                                },
                                {
                                    Column(Modifier.fillMaxSize()) {
                                        Text("Frecuencia Cardíaca", Modifier.align(Alignment.CenterHorizontally))
                                        AxisLineChart(
                                            values = data.hr,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                        )
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            pageHeight = 200.dp,
                            thumbSize = 60.dp,
                            spacing = 8.dp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Indicador de página
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    )
}

