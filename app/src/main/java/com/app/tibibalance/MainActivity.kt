package com.app.tibibalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.app.tibibalance.data.repository.HabitTemplateRepository
import com.app.tibibalance.ui.navigation.AppNavGraph
import com.app.tibibalance.ui.theme.TibiBalanceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /* ─── inyectamos el repo de plantillas ─── */
    @Inject
    lateinit var templateRepo: HabitTemplateRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Edge-to-edge */
        WindowCompat.setDecorFitsSystemWindows(window, false)

        /* ① descarga inicial + ② listener en tiempo real */
        lifecycleScope.launch(Dispatchers.IO) {
            templateRepo.refreshOnce()          // ← una sola lectura
            templateRepo.startSync(this)        // ← Flow de cambios
        }

        setContent {
            TibiBalanceTheme {
                AppNavGraph()
            }
        }
    }
}
