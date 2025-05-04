/* ui/screens/main/MainViewModel.kt */
package com.app.tibibalance.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.ProfileRepository
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  · currentPage    → sincroniza Pager y BottomBar
 *  · settingsUi     → estado que consume SettingsScreen
 *  · events         → one-shot (SignOut) para navegación
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val authRepo   : AuthRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    /* -------- Navegación entre páginas -------- */
    private val _currentPage = MutableStateFlow(2)              // Home = index 2
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun selectPage(index: Int) { _currentPage.value = index }

    /* -------- Estado de Ajustes -------- */
    private val _settingsUi = MutableStateFlow(SettingsUi())    // loading = true
    val settingsUi: StateFlow<SettingsUi> = _settingsUi.asStateFlow()

    init {
        viewModelScope.launch(io) {                             // colecciona perfil en background
            profileRepo.profile
                .onEach { profile ->
                    _settingsUi.value = SettingsUi(
                        loading = false,
                        profile = profile
                    )
                }
                .catch { e ->
                    _settingsUi.value = SettingsUi(
                        loading = false,
                        error   = e.localizedMessage ?: "Error al cargar perfil"
                    )
                }
                .collect()
        }
    }

    /* -------- Eventos one-shot -------- */
    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    fun signOut() = viewModelScope.launch(io) {
        authRepo.signOut()
        _events.emit(MainEvent.SignedOut)                       // trigger navegación
    }
}

/* --- UI-state & eventos --- */
data class SettingsUi(
    val loading : Boolean      = true,
    val profile : UserProfile? = null,
    val error   : String?      = null
)

sealed interface MainEvent {
    object SignedOut : MainEvent
}
