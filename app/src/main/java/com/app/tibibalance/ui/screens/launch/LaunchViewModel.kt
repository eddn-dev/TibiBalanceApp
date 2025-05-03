// ui/screens/launch/LaunchViewModel.kt
package com.app.tibibalance.ui.screens.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.app.tibibalance.domain.usecase.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    observeAuth: ObserveAuthStateUseCase
) : ViewModel() {
    val isLoggedIn = observeAuth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}
