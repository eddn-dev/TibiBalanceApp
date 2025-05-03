package com.app.tibibalance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    fun signIn(email: String, pass: String, onError: (String) -> Unit) = viewModelScope.launch {
        try {
            repo.signIn(email.trim(), pass)
        } catch (e: Exception) {
            onError(e.message ?: "Error")
        }
    }

    fun signInWithGoogle(/* TODO token */) {
        /*  implementar flujo One-Tap / Credential  */
    }
}
