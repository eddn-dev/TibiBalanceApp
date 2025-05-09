/**
 * @file    LaunchViewModel.kt
 * @ingroup ui_screens_launch // Grupo para ViewModels de la pantalla de inicio/lanzamiento
 * @brief   ViewModel para la [LaunchScreen], responsable de determinar el estado de sesión del usuario.
 *
 * @details
 * Este ViewModel, gestionado por Hilt (`@HiltViewModel`), tiene la función principal de
 * observar el estado de autenticación del usuario y el estado de verificación de su correo
 * electrónico. Combina esta información en un [StateFlow] de [SessionState] que la
 * [LaunchScreen] puede observar para decidir a qué pantalla navegar:
 * - Si no hay sesión ([SessionState.loggedIn] es `false`), la UI permanece en [LaunchScreen].
 * - Si hay sesión y el correo está verificado ([SessionState.verified] es `true`), la UI navega a [Screen.Main].
 * - Si hay sesión pero el correo no está verificado, la UI navega a [Screen.VerifyEmail].
 *
 * Para asegurar que el estado de verificación del correo sea el más reciente, si el usuario
 * está logueado, el ViewModel intenta llamar a `auth.currentUser?.reload()` y luego
 * a `repo.syncVerification()` dentro de la función `refreshAndSync`. Esto es útil
 * si el usuario verifica su correo en otro dispositivo o navegador y luego regresa a la app.
 *
 * @property sessionState Un [StateFlow] que emite el [SessionState] actual (si el usuario está
 * logueado y si su correo está verificado). La [LaunchScreen] observa este flujo.
 *
 * @see LaunchScreen Composable que observa este ViewModel.
 * @see SessionState Data class que representa el estado de la sesión.
 * @see AuthRepository Repositorio utilizado para obtener el estado de autenticación y sincronizar la verificación.
 * @see FirebaseAuth SDK de Firebase para obtener el usuario actual y recargar su estado.
 * @see dagger.hilt.android.lifecycle.HiltViewModel
 * @see androidx.lifecycle.viewModelScope
 * @see kotlinx.coroutines.flow.StateFlow
 * @see kotlinx.coroutines.flow.map
 * @see kotlinx.coroutines.flow.stateIn
 */
// ui/screens/launch/LaunchViewModel.kt
package com.app.tibibalance.ui.screens.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * @brief Data class que representa el estado de sesión mínimo necesario para la lógica de navegación en [LaunchScreen].
 *
 * @param loggedIn `true` si hay un usuario autenticado, `false` en caso contrario. Por defecto `false`.
 * @param verified `true` si el correo electrónico del usuario autenticado está verificado, `false` en caso contrario
 * o si no hay usuario logueado. Por defecto `false`.
 */
data class SessionState(
    val loggedIn: Boolean = false,
    val verified: Boolean = false
)

/**
 * @brief ViewModel para [LaunchScreen].
 *
 * @details Proporciona el [sessionState] que la UI utiliza para decidir a qué pantalla
 * redirigir al usuario. Intenta refrescar el estado de verificación del usuario si
 * se detecta una sesión activa.
 *
 * @constructor Inyecta las dependencias [AuthRepository] y [FirebaseAuth] a través de Hilt.
 * @param repo Repositorio de autenticación para observar el estado de inicio de sesión y sincronizar la verificación.
 * @param auth Instancia de FirebaseAuth para acceder al usuario actual y recargar su estado.
 */
@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val repo : AuthRepository,
    private val auth : FirebaseAuth
) : ViewModel() {

    /**
     * @brief [StateFlow] que expone el [SessionState] actual a la UI.
     * @details Se construye combinando el flujo `repo.isLoggedIn` con una comprobación
     * del estado de verificación del `auth.currentUser`. Si el usuario está logueado,
     * se lanza una corrutina para llamar a [refreshAndSync].
     * El `stateIn` asegura que el flujo se comparta y se mantenga activo mientras haya
     * suscriptores (con un tiempo de espera de 5 segundos antes de detenerse si no hay suscriptores).
     * El valor inicial es un [SessionState] por defecto (no logueado, no verificado).
     */
    val sessionState: StateFlow<SessionState> =
        repo.isLoggedIn // Observa el Flow<Boolean> del repositorio.
            .map { logged -> // Por cada emisión de 'logged'...
                // Si 'logged' es true, intenta refrescar el estado de verificación.
                if (logged) {
                    viewModelScope.launch { refreshAndSync() }
                }
                val user = auth.currentUser // Obtiene el usuario actual de FirebaseAuth.
                // Crea y emite un nuevo SessionState.
                SessionState(loggedIn = logged, verified = user?.isEmailVerified == true)
            }
            // Convierte el Flow en un StateFlow, compartiéndolo y manteniendo el último valor.
            .stateIn(
                scope = viewModelScope, // El scope de corrutina del ViewModel.
                // El flujo se inicia cuando hay un suscriptor y se detiene 5s después de que el último se va.
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SessionState() // Valor inicial mientras se establece el flujo.
            )

    /**
     * @brief Intenta recargar el estado del usuario actual desde Firebase y sincronizar el estado de verificación.
     * @details Esta función privada se llama cuando se detecta que un usuario está logueado.
     * Primero, intenta `reload()` los datos del usuario actual de Firebase para obtener la información
     * más reciente (incluyendo `isEmailVerified`). Luego, llama a `repo.syncVerification()`
     * que, si el correo está verificado en Firebase, asegura que este estado se refleje
     * también en Firestore.
     * Las excepciones (e.g., problemas de red) se capturan y se ignoran silenciosamente
     * para no interrumpir el flujo principal de determinación del estado de sesión.
     */
    private suspend fun refreshAndSync() {
        try {
            auth.currentUser?.reload()?.await() // Espera a que se complete la recarga.
            repo.syncVerification() // Llama al repo para sincronizar (ya devuelve true/false).
        } catch (_: Exception) {
            /* Se ignoran errores como falta de red, etc. El estado de 'verified'
               se basará en la información local de FirebaseAuth o la última sincronizada. */
        }
    }
}