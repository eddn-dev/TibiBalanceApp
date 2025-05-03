// ui/screens/auth/SignUpUiState.kt
package com.app.tibibalance.ui.screens.auth

sealed interface SignUpUiState {
    data object Idle                     : SignUpUiState
    data object Loading                  : SignUpUiState
    data class Success(val email: String): SignUpUiState
    data class Error(val message: String): SignUpUiState
}
