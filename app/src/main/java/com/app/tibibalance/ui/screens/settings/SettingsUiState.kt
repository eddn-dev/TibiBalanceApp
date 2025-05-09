/**
 * @file SettingsUiState.kt
 * @ingroup ui_screens_settings
 * @brief Define los diferentes estados de la interfaz de usuario para la pantalla de Ajustes.
 *
 * @details
 * Esta interfaz sellada (`sealed interface`) encapsula todos los posibles estados por los que
 * puede pasar la `SettingsScreen` mientras interactúa con el [SettingsViewModel].
 * Cada estado representa una situación visual o de datos diferente:
 * - [Loading]: La pantalla está esperando que se carguen los datos del perfil.
 * - [Ready]: Los datos del perfil ([UserProfile]) se han cargado exitosamente y están disponibles.
 * - [Error]: Ocurrió un error al intentar cargar los datos del perfil.
 * - [SignedOut]: El usuario ha cerrado sesión y la UI debería reaccionar (e.g., navegar a la pantalla de inicio).
 *
 * También incluye una función de extensión [toUi] que convierte una instancia de
 * [SettingsUiState] al modelo de UI [SettingsUi] (utilizado por [MainViewModel]),
 * simplificando la propagación del estado del perfil hacia componentes padres.
 *
 * @see SettingsScreen Composable que observa estos estados.
 * @see SettingsViewModel ViewModel que emite estos estados.
 * @see UserProfile Modelo de dominio para la información del perfil de usuario.
 * @see com.app.tibibalance.ui.screens.main.SettingsUi Modelo de UI utilizado en MainViewModel.
 */
/* ui/screens/settings/SettingsUiState.kt */
package com.app.tibibalance.ui.screens.settings

import com.app.tibibalance.domain.model.UserProfile
import com.app.tibibalance.ui.screens.main.SettingsUi // Modelo de UI usado en MainViewModel

/**
 * @brief Interfaz sellada que representa los diferentes estados de la UI para la pantalla de Ajustes.
 */
sealed interface SettingsUiState {
    /**
     * @brief Estado de carga inicial o mientras se realizan operaciones asíncronas.
     * @details La UI típicamente mostrará un indicador de progreso.
     */
    object Loading                      : SettingsUiState

    /**
     * @brief Estado que indica que los datos del perfil se han cargado exitosamente.
     * @param profile El [UserProfile] cargado, listo para ser mostrado en la UI.
     */
    data class Ready(val profile: UserProfile) : SettingsUiState

    /**
     * @brief Estado que indica que ocurrió un error al cargar o procesar los datos.
     * @param message Un [String] descriptivo del error, para ser mostrado al usuario
     * (e.g., en un Snackbar).
     */
    data class Error(val message: String)      : SettingsUiState

    /**
     * @brief Estado que indica que el usuario ha cerrado sesión exitosamente.
     * @details La UI debería manejar este estado para, por ejemplo, navegar a la pantalla de inicio de sesión.
     */
    object SignedOut                    : SettingsUiState
}

/**
 * @brief Función de extensión para convertir un [SettingsUiState] al modelo [SettingsUi]
 * que utiliza [com.app.tibibalance.ui.screens.main.MainViewModel].
 *
 * @details Este mapper simplifica la lógica en `MainViewModel` para actualizar su propio
 * estado `settingsUi` basado en el estado más detallado de `SettingsViewModel`.
 * Esencialmente, extrae la información relevante ([UserProfile], estado de carga, error)
 * de [SettingsUiState].
 *
 * @receiver La instancia de [SettingsUiState] a convertir.
 * @return Una instancia de [SettingsUi] correspondiente.
 * - Para [SettingsUiState.Loading], devuelve `SettingsUi()` (loading=true, profile=null, error=null).
 * - Para [SettingsUiState.Ready], devuelve `SettingsUi(loading = false, profile = profile)`.
 * - Para [SettingsUiState.Error], devuelve `SettingsUi(loading = false, error = message)`.
 * - Para [SettingsUiState.SignedOut], devuelve `SettingsUi(loading = false)` (implica que el perfil será null).
 */
fun SettingsUiState.toUi(): SettingsUi = when (this) {
    SettingsUiState.Loading        -> SettingsUi() // loading = true por defecto en SettingsUi
    is SettingsUiState.Ready       -> SettingsUi(loading = false, profile = profile)
    is SettingsUiState.Error       -> SettingsUi(loading = false, error = message)
    SettingsUiState.SignedOut      -> SettingsUi(loading = false) // El perfil será null, lo que indica no logueado
}