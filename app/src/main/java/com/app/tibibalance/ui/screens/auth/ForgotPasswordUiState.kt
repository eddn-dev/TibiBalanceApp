package com.app.tibibalance.ui.screens.auth

/** Estado de la UI */
sealed interface ForgotUiState {
    data object Idle                     : ForgotUiState
    data object Loading                  : ForgotUiState
    data object Success                  : ForgotUiState      // e-mail enviado
    data class Error(val message: String): ForgotUiState
}