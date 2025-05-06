package com.app.tibibalance.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties // Necesario para ModalContainer
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title
import kotlinx.coroutines.launch


data class ModalTabItem(
    val title: String,
    val content: @Composable () -> Unit
)

/**
 * Un contenedor modal con pestañas, ancho fijo, altura de pestañas ajustada
 * y espacio para el botón de cierre.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModalWithTabs(
    onDismissRequest: () -> Unit,
    tabs: List<ModalTabItem>,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    properties: DialogProperties = DialogProperties(),
    closeButtonEnabled: Boolean = true,
    initialTabIndex: Int = 0,
    selectedTabColor: Color = MaterialTheme.colorScheme.primaryContainer,
    unselectedTabColor: Color = containerColor,
    selectedTextColor: Color = contentColorFor(selectedTabColor),
    unselectedTextColor: Color = contentColorFor(unselectedTabColor).copy(alpha = 0.7f)
) {
    if (tabs.isEmpty()) return
    val validInitialIndex = initialTabIndex.coerceIn(0, tabs.lastIndex)
    val pagerState = rememberPagerState(initialPage = validInitialIndex) { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    ModalContainer(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        properties = properties,
        closeButtonEnabled = closeButtonEnabled
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fila de Pestañas
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = unselectedTabColor,
                contentColor = unselectedTextColor,
                indicator = {},
                divider = {},
                // CAMBIO: Añadir padding al final para dejar espacio al botón 'X'
                modifier = Modifier.padding(end = if (closeButtonEnabled) 40.dp else 0.dp)
            ) {
                tabs.forEachIndexed { index, tabItem ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier
                            .height(48.dp) // Altura fija
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(if (selected) selectedTabColor else unselectedTabColor)
                            .padding(vertical = 4.dp, horizontal = 12.dp), // Padding interno reducido
                        text = {
                            Text(
                                text = tabItem.title,
                                color = if (selected) selectedTextColor else unselectedTextColor,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            } // Fin TabRow

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    tabs[pageIndex].content()
                }
            } // Fin HorizontalPager
        } // Fin Column
    } // Fin ModalContainer
}


// --- Preview para ModalWithTabs con Espacio para Botón ---
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, name = "Modal Con Pestañas (Espacio Botón)")
@Composable
fun ModalWithTabsPreview() {
    MaterialTheme {
        // Datos de ejemplo con los títulos solicitados
        val sampleTabs = remember {
            listOf(
                ModalTabItem("Semanal") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.fillMaxWidth()) {
                        Title(text = "Contenido Semanal", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Description(
                            text = "Aquí va la información semanal.",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        PrimaryButton(text = "Ver Semana", onClick = {})
                    }
                },
                ModalTabItem("Mensual") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier=Modifier.fillMaxWidth()) {
                        Title(text = "Contenido Mensual", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var checked by remember { mutableStateOf(true) }
                            Description(text = "Activar Resumen:")
                            Spacer(modifier=Modifier.width(8.dp))
                            SwitchToggle(checked = checked, onCheckedChange = {checked = it})
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Description(
                            text = "Resumen detallado del mes.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Puedes añadir más pestañas aquí si lo necesitas
            )
        }
        // Colores y forma (sin cambios)
        val previewContainerColor = Color(0xFFE3F2FD)
        val previewSelectedTabColor = Color(0xFFBBDEFB)
        val previewUnselectedTabColor = previewContainerColor
        val previewSelectedTextColor = Color.Black
        val previewUnselectedTextColor = Color.Black.copy(alpha = 0.6f)
        val shape = RoundedCornerShape(16.dp)
        val closeButtonEnabled = true // Mantenerlo true en preview para ver el efecto
        val closeButtonBackgroundColor = Color.White
        val closeButtonContentColor = contentColorFor(closeButtonBackgroundColor)
        val pagerState = rememberPagerState(initialPage = 0) { sampleTabs.size } // Empezar en la primera
        val coroutineScope = rememberCoroutineScope()

        // Simulación del Card
        Card(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight(),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = previewContainerColor,
                contentColor = contentColorFor(previewContainerColor)
            )
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // TabRow con padding al final
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = previewUnselectedTabColor,
                        contentColor = previewUnselectedTextColor,
                        indicator = {},
                        divider = {},
                        // CAMBIO: Añadir padding al final en la preview también
                        modifier = Modifier.padding(end = if (closeButtonEnabled) 40.dp else 0.dp)
                    ) {
                        sampleTabs.forEachIndexed { index, tabItem ->
                            val selected = pagerState.currentPage == index
                            Tab(
                                selected = selected,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                },
                                modifier = Modifier
                                    .height(48.dp) // Altura fija
                                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                    .background(if (selected) previewSelectedTabColor else previewUnselectedTabColor)
                                    .padding(vertical = 4.dp, horizontal = 12.dp), // Padding interno reducido
                                text = {
                                    Text(
                                        text = tabItem.title, // Usar títulos de ejemplo actualizados
                                        color = if (selected) previewSelectedTextColor else previewUnselectedTextColor,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                    // HorizontalPager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp)
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            sampleTabs[pageIndex].content()
                        }
                    }
                } // Fin Column principal
                // Botón de cierre 'X' (simulado)
                if (closeButtonEnabled) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).size(32.dp),
                        shape = CircleShape, color = closeButtonBackgroundColor, shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = { /* No-op */ }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, "Cerrar modal", tint = closeButtonContentColor, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } // Fin Box externo
        } // Fin Card
    } // Fin MaterialTheme
}

