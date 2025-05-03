// ui/screens/launch/LaunchViewModel.kt
package com.app.tibibalance.ui.screens.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estado mínimo que la UI necesita para decidir a dónde navegar */
data class SessionState(
    val loggedIn: Boolean = false,
    val verified: Boolean = false
)

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val repo : AuthRepository,
    private val auth : FirebaseAuth
) : ViewModel() {

    /** Combina el flujo de autenticación con la bandera e-mail verificado */
    val sessionState: StateFlow<SessionState> =
        repo.isLoggedIn
            .map { logged ->
                // si está logueado intenta refrescar y sincronizar Firestore
                if (logged) viewModelScope.launch { refreshAndSync() }
                val user = auth.currentUser
                SessionState(loggedIn = logged, verified = user?.isEmailVerified == true)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                SessionState()
            )

    /** Fuerza reload() y, si ya verificó, actualiza “verified” en Firestore */
    private suspend fun refreshAndSync() {
        try {
            auth.currentUser?.reload()
            repo.syncVerification()                 // ya devuelve true/false
        } catch (_: Exception) {
            /* ignoramos: sin red, etc. */
        }
    }
}
