// ui/screens/onboarding/OnboardingScreen.kt
package com.app.tibibalance.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi // Importar de foundation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager // Importar de foundation.pager
import androidx.compose.foundation.pager.rememberPagerState // Importar de foundation.pager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
// Eliminar importaciones de accompanist.pager
// import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

/**
 * @file OnboardingScreen.kt
 * @brief Pantalla de onboarding con Lottie y Pager de Compose Foundation, usando un ViewModel para precarga.
 * @ingroup ui_screens_onboarding
 *
 * @details
 * Muestra una serie de pantallas de introducción con animaciones Lottie precargadas por [OnboardingViewModel].
 * Incluye indicadores de página y botones “Atrás”/“Siguiente/¡Empecemos!”, y aplica un fondo degradado vertical.
 *
 * @param pages Lista de [OnboardingPage] con título, descripción y animación.
 * @param onComplete Callback que se invoca cuando el usuario termina el flujo.
 */
@OptIn(ExperimentalFoundationApi::class) // Usar ExperimentalFoundationApi
@Composable
fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    // 1) Degradado de fondo
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF3EA8FE).copy(alpha = .25f),
            Color.White
        )
    )

    // 2) Observa las composiciones precargadas
    val compositions by viewModel.compositions.collectAsState()

    // 3) Estado del Pager y coroutine scope
    // Usar rememberPagerState de androidx.compose.foundation.pager
    val pagerState = rememberPagerState(initialPage = 0) { pages.size } // La nueva API requiere el count aquí
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center // Centra el contenido de este Box (la Column principal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho
                .padding(16.dp) // Padding general
            // Eliminamos .fillMaxSize() y verticalArrangement.SpaceBetween
            // .fillMaxSize()
            // verticalArrangement = Arrangement.SpaceBetween
            , horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente dentro de esta Column
        ) {
            // Pager de Compose Foundation
            HorizontalPager(
                state = pagerState, // La nueva API solo requiere el state
                modifier = Modifier
                    .fillMaxWidth()
                // Eliminamos .weight(1f) aquí para que el Pager tome solo el espacio necesario
                // .weight(1f)
            ) { pageIndex ->
                PreloadedOnboardingPage(
                    page        = pages[pageIndex],
                    composition = compositions.getOrNull(pageIndex)
                )
            }

            // Indicadores
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Aumentamos un poco el padding vertical para separarlos
            ) {
                repeat(pages.size) { idx ->
                    val selected = pagerState.currentPage == idx
                    Box(
                        modifier = Modifier
                            .size(if (selected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                    Spacer(Modifier.width(4.dp)) // Añadido Spacer para separar los indicadores
                }
            }

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // Añadimos padding superior para separarlos de los indicadores
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom // Asegura que los botones se alineen en la parte inferior de su Row
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text("Atrás")
                    }
                } else {
                    // Usar un Box con el mismo ancho que el botón para mantener el espacio
                    Box(modifier = Modifier.width(IntrinsicSize.Min)) {
                        TextButton(onClick = {}, enabled = false, content = {}) // Botón invisible para ocupar espacio
                    }
                }
                Button(onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.lastIndex) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onComplete()
                        }
                    }
                }) {
                    Text(
                        if (pagerState.currentPage < pages.lastIndex)
                            "Siguiente"
                        else
                            "¡Empecemos!"
                    )
                }
            }
        }
    }
}

/**
 * @brief Página individual de onboarding que recibe una composición Lottie precargada.
 *
 * @param page        Datos de la página (título, descripción y recurso raw de Lottie).
 * @param composition Instancia de [LottieComposition] o null si aún no cargada.
 */
@Composable
private fun PreloadedOnboardingPage(
    page: OnboardingPage,
    composition: LottieComposition?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Ocupa todo el ancho de la página del Pager
            .padding(24.dp), // Padding dentro de cada página
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centra el contenido verticalmente dentro de esta página
    ) {
        composition?.let {
            LottieAnimation(
                composition = it,
                iterations  = LottieConstants.IterateForever,
                modifier    = Modifier
                    .fillMaxWidth(0.8f) // Ocupa el 80% del ancho disponible
                    .aspectRatio(1f) // Mantiene una relación de aspecto 1:1 (cuadrada)
                // Eliminamos weight y sizeIn para simplificar el layout interno
                // .weight(1f, fill = false)
                // .sizeIn(maxHeight = 300.dp)
            )
        }
        Spacer(Modifier.height(24.dp)) // Espacio entre animación y título
        Text(
            text      = stringResource(page.titleRes),
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp)) // Espacio entre título y descripción
        Text(
            text      = stringResource(page.descRes),
            style     = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
        // Eliminamos el Spacer.weight(1f) aquí
        // Spacer(Modifier.weight(1f))
    }
}
