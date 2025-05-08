// data/remote/firebase/FirebaseAuthService.kt
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
import com.app.tibibalance.di.IoDispatcher

/**
 * @file    FirebaseAuthService.kt
 * @ingroup data_remote
 * @brief   Implementación de [AuthService] basada en **FirebaseAuth**.
 *
 * Envuelve el SDK de Firebase Authentication y provee:
 * - Flujo reactivo del estado de autenticación.
 * - Inicio de sesión / registro con correo-contraseña.
 * - Registro con verificación de e-mail automática.
 * - Restablecimiento de contraseña.
 * - Inicio de sesión con Google One-Tap (**GIS**).
 *
 * Todas las operaciones se ejecutan en el *dispatcher* inyectado
 * mediante `@IoDispatcher` para evitar bloquear el hilo principal.
 */

/**
 * @class   FirebaseAuthService
 * @brief   Implementación concreta de [AuthService] administrada por Hilt.
 *
 * Se anota como `@Singleton` para compartir la instancia del
 * SDK a lo largo del ciclo de vida de la aplicación.
 *
 * @constructor Inyecta la instancia de [FirebaseAuth] y el
 *              [CoroutineDispatcher] de IO.
 */
@Singleton
class FirebaseAuthService @Inject constructor(

    private val auth: FirebaseAuth,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : AuthService {

    /* ───────────────── Estado de sesión ───────────────── */

    /**
     * @brief Flujo que emite `true` cuando existe un usuario autenticado.
     *
     * Se basa en un `AuthStateListener` interno que delega en
     * `callbackFlow` para exponer la señal de forma reactiva.
     */
    override val authState = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flowOn(io)

    /* ───────────── Correo / contraseña ───────────── */

    /**
     * @copydoc AuthService.signIn
     */
    override suspend fun signIn(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.signInWithEmailAndPassword(email, pass).await().user
            ?: error("User is null after sign-in")
    }

    /**
     * @copydoc AuthService.signUp
     */
    override suspend fun signUp(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.createUserWithEmailAndPassword(email, pass).await().user
            ?: error("User is null after sign-up")
    }

    /**
     * @copydoc AuthService.signUpAndVerify
     *
     * Al completar el registro se envía un correo de verificación.
     */
    override suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser = withContext(io) {
        val user = auth.createUserWithEmailAndPassword(email, pass).await().user!!
        user.sendEmailVerification().await()
        Log.d("AUTH", "Verification e-mail sent to ${user.email}")
        user
    }

    /**
     * @copydoc AuthService.sendPasswordReset
     */
    override suspend fun sendPasswordReset(email: String) = withContext(io) {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    /* ───────────── Google One-Tap / GIS ───────────── */

    /**
     * @copydoc AuthService.signInGoogle
     *
     * Convierte el *ID Token* de Google en credenciales
     * de Firebase (`AuthCredential`) para autenticar al usuario.
     */
    override suspend fun signInGoogle(idToken: String): FirebaseUser = withContext(io) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred).await().user
            ?: error("User is null after Google sign-in")
    }

    /* ───────────────── Cerrar sesión ───────────────── */

    /**
     * @copydoc AuthService.signOut
     */
    override suspend fun signOut() = withContext(io) { auth.signOut() }
}
