package com.app.tibibalance.ui.screens.profile

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)