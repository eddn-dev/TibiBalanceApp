// ui/screens/onboarding/OnboardingViewModel.kt
package com.app.tibibalance.ui.screens.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.app.tibibalance.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @file OnboardingViewModel.kt
 * @brief ViewModel para precargar animaciones Lottie del onboarding usando callbacks.
 * @ingroup ui_screens_onboarding
 *
 * @details
 * Precarga en memoria las composiciones Lottie al inicio, sin usar await(),
 * sino registrando un listener que actualiza un StateFlow cuando la carga termina.
 */
class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    // 1) Lista de raw resources de las animaciones
    private val pagesRaw = listOf(
        R.raw.anim_health,
        R.raw.anim_habit,
        R.raw.anim_stats
    )

    // 2) Estado interno mutable con las composiciones (null = pendiente)
    private val _compositions =
        MutableStateFlow<List<LottieComposition?>>(List(pagesRaw.size) { null })

    /** StateFlow público con las composiciones cargadas o null si aún no. */
    val compositions: StateFlow<List<LottieComposition?>> = _compositions

    init {
        preloadCompositions()
    }

    /**
     * Precarga todas las animaciones registrando un listener en cada tarea de carga.
     */
    private fun preloadCompositions() {
        pagesRaw.forEachIndexed { index, rawRes ->
            viewModelScope.launch {
                // Inicia la carga de la composición (asíncrona)
                val task = LottieCompositionFactory.fromRawRes(getApplication(), rawRes)
                // El callback entrega directamente la composición cuando está lista
                task.addListener { comp: LottieComposition? ->
                    // Actualiza el StateFlow con la nueva composición en su índice
                    val updated = _compositions.value.toMutableList().also {
                        it[index] = comp
                    }
                    _compositions.value = updated
                }
            }
        }
    }

    /**
     * Para evitar el warning de “never used”, asegúrate de inyectar y observar este ViewModel:
     *
     * @code
     * @Composable
     * fun OnboardingScreen(
     *   viewModel: OnboardingViewModel = viewModel(factory = /* tu factory */),
     *   pages: List<OnboardingPage>,
     *   onComplete: () -> Unit
     * ) {
     *   val compositions by viewModel.compositions.collectAsState()
     *   // … usa compositions en tu pager …
     * }
     * @endcode
     */
}
