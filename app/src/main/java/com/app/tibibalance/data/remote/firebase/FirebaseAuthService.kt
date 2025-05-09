/**
 * @file    FirebaseAuthService.kt
 * @ingroup data_remote_service // Grupo específico para servicios remotos
 * @brief   Implementación concreta de [AuthService] utilizando el SDK de Firebase Authentication.
 *
 * @details Esta clase, marcada como `@Singleton` y gestionada por Hilt, envuelve las llamadas
 * al SDK de [FirebaseAuth] para realizar las operaciones de autenticación definidas en la
 * interfaz [AuthService]. Proporciona funcionalidades como:
 * - Observación del estado de autenticación mediante un [Flow].
 * - Inicio de sesión y registro con correo electrónico y contraseña.
 * - Registro que incluye el envío automático de un correo de verificación.
 * - Envío de correos para restablecer contraseña.
 * - Inicio de sesión/registro utilizando credenciales de Google (ID Token de One-Tap/GIS).
 * - Cierre de sesión.
 *
 * Todas las operaciones asíncronas que interactúan con Firebase se ejecutan en el
 * [CoroutineDispatcher] de IO inyectado (marcado con `@IoDispatcher`) para evitar
 * bloquear el hilo principal de la aplicación.
 */
package com.app.tibibalance.data.remote.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.app.tibibalance.di.IoDispatcher // Asegura que el Qualifier está importado

/**
 * @class   FirebaseAuthService
 * @brief   Implementación singleton de [AuthService] basada en Firebase Authentication.
 * @see AuthService Contrato que define las operaciones de autenticación.
 * @see com.app.tibibalance.data.repository.FirebaseAuthRepository Repositorio que consume este servicio.
 *
 * @constructor Inyecta las dependencias [FirebaseAuth] y el [CoroutineDispatcher] de IO
 * proporcionadas por Hilt.
 * @param auth Instancia singleton de [FirebaseAuth] para interactuar con el backend de autenticación.
 * @param io   Dispatcher de Coroutines configurado para operaciones de entrada/salida (inyectado con `@IoDispatcher`).
 */
@Singleton
class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth,
    @param:IoDispatcher private val io: CoroutineDispatcher // Inyección del dispatcher IO
) : AuthService {

    /* ───────────────── Estado de sesión ───────────────── */

    /**
     * @brief Flujo reactivo que emite `true` si hay un usuario autenticado, `false` en caso contrario.
     * @details Utiliza un [FirebaseAuth.AuthStateListener] y lo envuelve en un [callbackFlow]
     * para proporcionar una API reactiva basada en Kotlin Flows. El flujo se ejecuta
     * en el dispatcher de IO (`flowOn(io)`).
     * @return [Flow] que emite el estado de autenticación actual y futuras actualizaciones.
     */
    override val authState = callbackFlow {
        // Define el listener que envía el estado actual a través del Flow
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null) // Emite true si hay usuario, false si no
        }
        auth.addAuthStateListener(listener) // Registra el listener

        // Define la acción a ejecutar cuando el Flow se cancela (el colector deja de escuchar)
        awaitClose { auth.removeAuthStateListener(listener) } // Desregistra el listener
    }.flowOn(io) // Asegura que la lógica del Flow (incluyendo add/remove listener) se ejecute en el dispatcher IO

    /* ───────────── Correo / contraseña ───────────── */

    /**
     * @copydoc AuthService.signIn
     * @throws IllegalStateException Si Firebase devuelve un usuario nulo después de un inicio de sesión exitoso.
     */
    override suspend fun signIn(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.signInWithEmailAndPassword(email, pass).await().user
            ?: error("Firebase devolvió un usuario nulo después de signIn exitoso.")
    }

    /**
     * @copydoc AuthService.signUp
     * @throws IllegalStateException Si Firebase devuelve un usuario nulo después de un registro exitoso.
     */
    override suspend fun signUp(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.createUserWithEmailAndPassword(email, pass).await().user
            ?: error("Firebase devolvió un usuario nulo después de signUp exitoso.")
    }

    /**
     * @copydoc AuthService.signUpAndVerify
     * @details Implementación específica: Después de crear el usuario con éxito,
     * invoca `sendEmailVerification()` en el [FirebaseUser] y registra un mensaje en Logcat.
     * @throws IllegalStateException Si Firebase devuelve un usuario nulo después de un registro exitoso.
     */
    override suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser = withContext(io) {
        val user = auth.createUserWithEmailAndPassword(email, pass).await().user
            ?: error("Firebase devolvió un usuario nulo después de signUp exitoso.")
        // Envía el correo de verificación de forma asíncrona (no bloquea el retorno)
        user.sendEmailVerification().await() // Espera a que la tarea de envío se complete
        Log.d("FirebaseAuthService", "Correo de verificación enviado a ${user.email}")
        user // Devuelve el usuario creado
    }

    /**
     * @copydoc AuthService.sendPasswordReset
     */
    override suspend fun sendPasswordReset(email: String): Unit = withContext(io) {
        auth.sendPasswordResetEmail(email).await()
        // No es necesario devolver Unit explícitamente, pero es buena práctica para claridad.
    }

    /* ───────────── Google One-Tap / GIS ───────────── */

    /**
     * @copydoc AuthService.signInGoogle
     * @details Implementación específica: Convierte el `idToken` de Google en una
     * `AuthCredential` usando [GoogleAuthProvider.getCredential] y luego la utiliza
     * para iniciar sesión en Firebase con `auth.signInWithCredential`.
     * @throws IllegalStateException Si Firebase devuelve un usuario nulo después de un inicio de sesión exitoso con Google.
     */
    override suspend fun signInGoogle(idToken: String): FirebaseUser = withContext(io) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await().user
            ?: error("Firebase devolvió un usuario nulo después de signIn con Google exitoso.")
    }

    /* ───────────────── Cerrar sesión ───────────────── */

    /**
     * @copydoc AuthService.signOut
     */
    override suspend fun signOut(): Unit = withContext(io) {
        auth.signOut()
    }
}
