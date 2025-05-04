package com.app.tibibalance.ui.screens.auth

/**
 * Estados que expone VerifyEmailViewModel.
 *
 *  • Idle      – pantalla tranquila.
 *  • Loading   – spinner modal (reenviando o consultando verificación).
 *  • Success   – mensaje informativo (p.ej. “Correo reenviado”, “¡Verificado!”).
 *                Cuando [goHome] = true la pantalla debería navegar a Home
 *                tras pulsar “Aceptar”.
 *  • Error     – fallo global (red, permisos, etc.).
 */
sealed interface VerifyEmailUiState {
    object Idle : VerifyEmailUiState
    object Loading : VerifyEmailUiState
    data class Success(val message: String, val goHome: Boolean = false)
        : VerifyEmailUiState
    data class Error(val message: String) : VerifyEmailUiState
}
