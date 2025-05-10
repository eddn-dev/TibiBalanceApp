/**
 * @file    SettingsViewModel.kt
 * @ingroup ui_screens_settings // Grupo para ViewModels de pantallas de configuración
 * @brief   ViewModel para la pantalla de Ajustes ([SettingsScreen]).
 *
 * @details Este ViewModel, gestionado por Hilt (`@HiltViewModel`), es responsable de:
 * - **Observar el Perfil:** Obtiene y observa el perfil del usuario actual ([UserProfile])
 * desde el [ProfileRepository].
 * - **Gestionar el Estado de la UI:** Expone el estado de la pantalla de Ajustes (`SettingsUiState`)
 * a través de un [StateFlow] (`ui`), indicando si los datos se están cargando, si están
 * listos, si hubo un error, o si el usuario ha cerrado sesión.
 * - **Manejar el Cierre de Sesión:** Proporciona la función `signOut()` que invoca al
 * [AuthRepository] para cerrar la sesión y al [ProfileRepository] para limpiar
 * la caché local. Actualiza el estado `ui` a `SignedOut` o `Error` según el resultado.
 * - **Consumir Estados:** Ofrece métodos (`consumeSignedOut`, `consumeError`) para que
 * la UI pueda indicar que ha procesado un estado transitorio (como `SignedOut` o `Error`),
 * permitiendo al ViewModel volver a un estado base (normalmente `Loading` mientras se
 * revalida el estado).
 *
 * Las operaciones asíncronas se ejecutan dentro del `viewModelScope`. La observación inicial
 * del perfil utiliza `catch` para manejar errores de flujo y emitir el estado `Error`.
 *
 * @see SettingsScreen Composable que observa este ViewModel.
 * @see SettingsUiState Sealed interface que define los estados de la UI.
 * @see AuthRepository Repositorio para la operación de cierre de sesión.
 * @see ProfileRepository Repositorio para observar y limpiar el perfil local.
 * @see HiltViewModel Anotación para la inyección de dependencias con Hilt.
 * @see ViewModel Clase base de AndroidX ViewModel.
 * @see StateFlow Flujo de datos observable para el estado de la UI.
 * @see viewModelScope Scope de corrutina asociado al ciclo de vida del ViewModel.
 */
/* ui/screens/settings/SettingsViewModel.kt */
package com.app.tibibalance.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @brief ViewModel para [SettingsScreen], encargado de obtener el perfil y manejar el cierre de sesión.
 *
 * @constructor Inyecta [AuthRepository] y [ProfileRepository] mediante Hilt.
 * @param authRepo Repositorio para realizar la acción de cierre de sesión.
 * @param profileRepo Repositorio para observar el perfil del usuario y limpiar la caché local.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo   : AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    /** @brief Flujo mutable interno para el estado de la UI, inicializado en Loading. */
    private val _ui = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    /**
     * @brief Flujo de estado inmutable expuesto a la UI ([SettingsScreen]) para observar los cambios.
     * @return [StateFlow] que emite el [SettingsUiState] actual.
     */
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    /**
     * @brief Bloque de inicialización que lanza una corrutina para observar el perfil.
     * @details Colecciona el flujo `profileRepo.profile`. Cada perfil emitido se mapea
     * a `SettingsUiState.Ready`. Si el flujo emite un error, se captura y se emite
     * `SettingsUiState.Error`.
     */
    init {
        viewModelScope.launch { // Lanza la corrutina en el scope del ViewModel
            profileRepo.profile // Accede al Flow<UserProfile> del repositorio
                .map<_, SettingsUiState>(SettingsUiState::Ready) // Mapea cada UserProfile a SettingsUiState.Ready
                .catch { emit(SettingsUiState.Error(it.message ?: "Sin perfil")) } // Si hay error, emite Error
                .collect { _ui.value = it } // Colecciona las emisiones y actualiza el estado _ui
        }
    }

    /**
     * @brief Inicia el proceso de cierre de sesión del usuario.
     * @details Cambia el estado a `Loading`, llama a `authRepo.signOut()` y `profileRepo.clearLocal()`.
     * Si tiene éxito, emite `SettingsUiState.SignedOut`. Si falla, emite `SettingsUiState.Error`.
     */
    fun signOut() = viewModelScope.launch { // Lanza la operación en el scope del ViewModel
        _ui.value = SettingsUiState.Loading // Indica inicio de la operación
        try {
            authRepo.signOut() // Cierra sesión en el backend/localmente
            profileRepo.clearLocal() // Limpia la caché local del perfil
            _ui.value = SettingsUiState.SignedOut // Emite estado de éxito
        } catch (e: Exception) { // Captura cualquier excepción durante el proceso
            _ui.value = SettingsUiState.Error(e.message ?: "No se pudo cerrar la sesión") // Emite estado de error
        }
    }


    /**
     * @brief Restablece el estado de la UI después de que el estado `SignedOut` ha sido manejado.
     * @details Cambia el estado de `SignedOut` a `Loading`. La UI debería llamar a esto después
     * de completar la navegación tras el cierre de sesión para evitar re-navegaciones si
     * el estado se recompone.
     */
    fun consumeSignedOut() {
        if (_ui.value is SettingsUiState.SignedOut) {
            _ui.value = SettingsUiState.Loading // Vuelve a Loading (o podría ser Idle si se prefiere)
        }
    }

    /**
     * @brief Restablece el estado de la UI después de que el estado `Error` ha sido manejado.
     * @details Cambia el estado de `Error` a `Loading`. La UI debería llamar a esto después
     * de mostrar el mensaje de error al usuario (e.g., en un Snackbar), para permitir
     * posibles reintentos o simplemente limpiar el estado de error visual.
     */
    fun consumeError() {
        if (_ui.value is SettingsUiState.Error) {
            _ui.value = SettingsUiState.Loading // Vuelve a Loading (o Idle)
        }
    }
}