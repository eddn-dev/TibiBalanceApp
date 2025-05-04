/* ui/screens/settings/SettingsUiState.kt */
package com.app.tibibalance.ui.screens.settings

import com.app.tibibalance.domain.model.UserProfile
import com.app.tibibalance.ui.screens.main.SettingsUi

sealed interface SettingsUiState {
    object Loading                      : SettingsUiState
    data class Ready(val profile: UserProfile) : SettingsUiState
    data class Error(val message: String)      : SettingsUiState
    object SignedOut                    : SettingsUiState
}

fun SettingsUiState.toUi(): SettingsUi = when (this) {
    SettingsUiState.Loading        -> SettingsUi()
    is SettingsUiState.Ready       -> SettingsUi(loading = false, profile = profile)
    is SettingsUiState.Error       -> SettingsUi(loading = false, error = message)
    SettingsUiState.SignedOut      -> SettingsUi(loading = false)
}
