package com.app.tibibalance.ui.screens.auth

sealed interface RecoverPasswordUiState {
    object Idle : RecoverPasswordUiState
    object Loading : RecoverPasswordUiState
    object Success : RecoverPasswordUiState
    data class Error(val message: String) : RecoverPasswordUiState
}
