/**
 * @file    MainActivity.kt
 * @ingroup ui_entrypoint
 * @brief   Actividad principal de la aplicación, responsable de inicializar Firebase Auth,
 *          sincronizar datos de plantillas de hábitos y arrancar la navegación Compose.
 *
 * @details
 * - Realiza un login anónimo en Firebase al inicio para obtener un UID válido.
 * - Inicia la descarga inicial y el listener en tiempo real de plantillas de hábitos.
 * - Configura el tema y el grafo de navegación Compose.
 */
package com.app.tibibalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.app.tibibalance.data.repository.HabitTemplateRepository
import com.app.tibibalance.ui.navigation.AppNavGraph
import com.app.tibibalance.ui.theme.TibiBalanceTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /** Repositorio de plantillas de hábitos para sincronización de datos. */
    @Inject
    lateinit var templateRepo: HabitTemplateRepository

    // --------------------------------------------------------------------------------------------
    /**
     * @brief Se llama al crear la actividad.
     * @details
     * - Configura el modo edge-to-edge.
     * - Realiza sign-in anónimo en Firebase.
     * - Tras el éxito, lanza la sincronización de plantillas y carga la UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge: contenido se extiende a áreas de sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Login anónimo en Firebase Auth
        Firebase.auth.signInAnonymously()
            .addOnSuccessListener {
                // ① Descarga inicial y ② listener en tiempo real de plantillas
                lifecycleScope.launch(Dispatchers.IO) {
                    templateRepo.refreshOnce()
                    templateRepo.startSync(this)
                }
                // Monta la UI Compose
                setAppContent()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                // Aunque falle el auth, aún arrancamos la UI (sin sincronización de emociones)
                setAppContent()
            }
    }

    // --------------------------------------------------------------------------------------------
    /**
     * @brief Configura el contenido Compose de la actividad.
     * @details Envuelve el grafo de navegación en el tema de la aplicación.
     */
    private fun setAppContent() {
        setContent {
            TibiBalanceTheme {
                AppNavGraph()
            }
        }
    }
}
