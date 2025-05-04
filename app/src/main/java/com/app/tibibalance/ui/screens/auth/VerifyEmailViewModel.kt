// ui/screens/auth/VerifyEmailViewModel.kt
package com.app.tibibalance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _ui = MutableStateFlow<VerifyEmailUiState>(VerifyEmailUiState.Idle)
    val ui: StateFlow<VerifyEmailUiState> = _ui

    fun resend() = viewModelScope.launch {
        _ui.value = VerifyEmailUiState.Loading
        runCatching {
            auth.currentUser?.sendEmailVerification()?.await()
        }.onSuccess {
            _ui.value = VerifyEmailUiState.Success("Correo reenviado")
        }.onFailure {
            _ui.value = VerifyEmailUiState.Error(it.localizedMessage ?: "Error al reenviar")
        }
    }

    fun verify() = viewModelScope.launch {
        _ui.value = VerifyEmailUiState.Loading
        runCatching {
            auth.currentUser?.reload()?.await()
            if (auth.currentUser?.isEmailVerified == true) {
                repo.syncVerification()
                true
            } else false
        }.onSuccess { ok ->
            if (ok)
                _ui.value = VerifyEmailUiState.Success("¡Verificado!", goHome = true)
            else
                _ui.value = VerifyEmailUiState.Error("Aún no está verificado")
        }.onFailure {
            _ui.value = VerifyEmailUiState.Error(it.localizedMessage ?: "Error de red")
        }
    }

    fun signOut() = viewModelScope.launch {          // recomendado por Google :contentReference[oaicite:1]{index=1}
        repo.signOut()
        _ui.value = VerifyEmailUiState.SignedOut
    }

    fun clear() {
        if (_ui.value !is VerifyEmailUiState.Loading)
            _ui.value = VerifyEmailUiState.Idle
    }
}
