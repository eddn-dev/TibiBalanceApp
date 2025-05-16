package com.app.wear

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.app.wear.data.WearRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Repositorio de datos para Firestore
    @Inject lateinit var repo: WearRepository

    // Cliente de Auth de Firebase
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Paso 1: iniciar sesión anónimo
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                Log.i("WearAuth", "Autenticación anónima OK, uid=${result.user?.uid}")

                // Paso 3: una vez autenticado, leer hábitos
                lifecycleScope.launch {
                    try {
                        val habits = repo.fetchHabits()
                        Log.i("WearRepo", "Hábitos obtenidos: ${habits.size}")
                        habits.forEach { Log.i("WearRepo", it.toString()) }
                    } catch (e: Exception) {
                        Log.e("WearRepo", "Error al leer hábitos", e)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("WearAuth", "Error en autenticación anónima", e)
            }

        // UI Compose
        setContent {
            MaterialTheme {
                Text(text = "¡Hola desde Wear!", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
