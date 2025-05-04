package com.app.tibibalance.ui.screens.auth


sealed interface ForgotPasswordUiState {
    data object Idle                     : ForgotPasswordUiState
    data object Loading                  : ForgotPasswordUiState
    data object Success                  : ForgotPasswordUiState      // e-mail enviado
    data class Error(val message: String): ForgotPasswordUiState
}