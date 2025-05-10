/**
 * @file MainViewModel.kt
 * @ingroup ui_screens_main
 * @brief ViewModel para la pantalla principal ([MainScreen]) de la aplicación.
 *
 * @details
 * Este ViewModel, gestionado por Hilt (`@HiltViewModel`), es responsable de:
 * - **Gestión del estado de la página actual:** Controla el índice de la página seleccionada
 * en la `BottomNavBar` y el `HorizontalPager` a través de `currentPage`.
 * - **Provisión del estado de la pantalla de Ajustes:** Mantiene y expone `settingsUi`
 * ([SettingsUi]), que contiene la información del perfil de usuario cargada
 * asíncronamente desde [ProfileRepository]. Este estado es consumido por
 * `SettingsScreen` (a través de `SettingsTab`).
 * - **Manejo de eventos one-shot:** Expone un [SharedFlow] `events` para comunicar
 * eventos únicos a la UI, como `MainEvent.SignedOut`, que se utiliza para
 * desencadenar la navegación de vuelta al flujo de autenticación.
 * - **Coordinación del cierre de sesión:** La función `signOut` invoca al [AuthRepository]
 * para cerrar la sesión del usuario y luego emite el evento `MainEvent.SignedOut`.
 *
 * Las operaciones de red o de base de datos (como la carga del perfil y el cierre de sesión)
 * se ejecutan en el [CoroutineDispatcher] de IO inyectado (`@IoDispatcher`).
 *
 * @property currentPage [StateFlow] que emite el índice de la página actualmente seleccionada (0-indexed).
 * @property settingsUi [StateFlow] que emite el estado [SettingsUi] para la pantalla de Ajustes.
 * @property events [SharedFlow] para emitir eventos únicos que la UI debe manejar (e.g., navegación tras cierre de sesión).
 *
 * @see MainScreen Composable que observa este ViewModel.
 * @see AuthRepository Repositorio para operaciones de autenticación.
 * @see ProfileRepository Repositorio para acceder a los datos del perfil de usuario.
 * @see SettingsUi Data class que representa el estado de la UI para la pantalla de Ajustes.
 * @see MainEvent Sealed interface que define los eventos one-shot.
 * @see IoDispatcher Qualifier para el CoroutineDispatcher de IO.
 * @see HiltViewModel Anotación para la inyección de dependencias con Hilt.
 * @see ViewModel Clase base de AndroidX ViewModel.
 * @see viewModelScope Scope de corrutina para operaciones asíncronas.
 */
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
 * @brief ViewModel para [MainScreen], gestionando la navegación entre pestañas y el estado de la pantalla de Ajustes.
 *
 * @constructor Inyecta [ProfileRepository], [AuthRepository] y un [CoroutineDispatcher] para IO.
 * @param profileRepo Repositorio para obtener los datos del perfil del usuario.
 * @param authRepo Repositorio para manejar las operaciones de autenticación (cierre de sesión).
 * @param io Dispatcher para ejecutar operaciones en hilos de background.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val authRepo   : AuthRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    /* -------- Navegación entre páginas -------- */
    /** @brief Flujo mutable interno para la página actual, inicializado en 2 (Home). */
    private val _currentPage = MutableStateFlow(2)              // Home = index 2
    /** @brief Flujo de estado expuesto que representa el índice de la página actual seleccionada. */
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    /**
     * @brief Actualiza la página actualmente seleccionada.
     * @param index El nuevo índice de la página.
     */
    fun selectPage(index: Int) { _currentPage.value = index }

    /* -------- Estado de Ajustes -------- */
    /** @brief Flujo mutable interno para el estado de la UI de Ajustes, inicializado como cargando. */
    private val _settingsUi = MutableStateFlow(SettingsUi())    // loading = true
    /** @brief Flujo de estado expuesto para la UI de Ajustes, consumido por [SettingsScreen]. */
    val settingsUi: StateFlow<SettingsUi> = _settingsUi.asStateFlow()

    /**
     * @brief Bloque de inicialización que observa el perfil del usuario.
     * @details Lanza una corrutina en `viewModelScope` (usando el dispatcher `io`)
     * para coleccionar el [Flow] de `profileRepo.profile`.
     * Cada emisión actualiza `_settingsUi` con el perfil cargado o un mensaje de error.
     */
    init {
        viewModelScope.launch(io) {                             // colecciona perfil en background
            profileRepo.profile
                .onEach { profile -> // Cuando se emite un nuevo perfil
                    _settingsUi.value = SettingsUi(
                        loading = false, // Se completó la carga
                        profile = profile // Se asigna el perfil
                    )
                }
                .catch { e -> // Si ocurre un error en el flujo
                    _settingsUi.value = SettingsUi(
                        loading = false, // Se completó la carga (con error)
                        error   = e.localizedMessage ?: "Error al cargar perfil" // Mensaje de error
                    )
                }
                .collect() // Inicia la colección del flujo
        }
    }

    /* -------- Eventos one-shot -------- */
    /** @brief Flujo compartido mutable interno para eventos únicos, como el cierre de sesión. */
    private val _events = MutableSharedFlow<MainEvent>()
    /** @brief Flujo compartido expuesto para que la UI observe eventos únicos. */
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    /**
     * @brief Realiza el proceso de cierre de sesión.
     * @details Llama a `authRepo.signOut()` en el dispatcher `io` y luego emite
     * el evento [MainEvent.SignedOut] para que la UI navegue a la pantalla de inicio.
     */
    fun signOut() = viewModelScope.launch(io) {
        authRepo.signOut() // Cierra la sesión del usuario
        _events.emit(MainEvent.SignedOut) // Emite el evento para la navegación
    }
}

/* --- UI-state & eventos --- */

/**
 * @brief Data class que representa el estado de la interfaz de usuario para la pantalla de Ajustes.
 *
 * @property loading Indica si los datos del perfil están actualmente cargándose. `true` por defecto.
 * @property profile El [UserProfile] del usuario, o `null` si no está cargado o no existe.
 * @property error Un mensaje [String] opcional que describe un error ocurrido al cargar el perfil.
 */
data class SettingsUi(
    val loading : Boolean      = true,
    val profile : UserProfile? = null,
    val error   : String?      = null
)

/**
 * @brief Interfaz sellada que define los eventos únicos que [MainViewModel] puede emitir a [MainScreen].
 */
sealed interface MainEvent {
    /**
     * @brief Evento emitido cuando el usuario ha cerrado sesión exitosamente.
     * @details La UI (MainScreen) debe manejar este evento para navegar de vuelta al flujo de autenticación.
     */
    object SignedOut : MainEvent
}