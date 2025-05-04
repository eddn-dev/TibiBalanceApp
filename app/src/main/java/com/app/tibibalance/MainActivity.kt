package com.app.tibibalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat        // ← NUEVO
import com.app.tibibalance.ui.navigation.AppNavGraph
import com.app.tibibalance.ui.theme.TibiBalanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TibiBalanceTheme {
                AppNavGraph()               // tu nav-graph raíz
            }
        }
    }
}
