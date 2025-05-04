package com.app.tibibalance.ui.screens.auth

/**
 * Estados que expone VerifyEmailViewModel.
 *
 *  • Idle      – pantalla tranquila.
 *  • Loading   – spinner modal (reenviando o consultando verificación).
 *  • Success   – mensaje informativo
 *  • Error     – fallo global (red, permisos, etc.).
 */
/* ui/screens/auth/VerifyEmailUiState.kt */
sealed interface VerifyEmailUiState {
    object Idle    : VerifyEmailUiState
    object Loading : VerifyEmailUiState
    data class Success(val message: String, val goHome: Boolean = false)
        : VerifyEmailUiState
    data class Error(val message: String) : VerifyEmailUiState

    /** Emite cuando el usuario ya está deslogueado */
    object SignedOut : VerifyEmailUiState
}
