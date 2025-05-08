/**
 * @file    FirebaseAuthRepository.kt
 * @ingroup data_repository
 * @brief   Implementación de [AuthRepository] que delega en **FirebaseAuth** y
 *          **Firestore**.
 *
 * - Usa un [AuthService] para las operaciones directas de autenticación.
 * - Garantiza que exista un documento en `profiles/{uid}` con la metadata
 *   mínima del usuario tras cualquier flujo de sign-in / sign-up.
 * - Expone utilidades como restablecimiento de contraseña y sincronización
 *   del estado de verificación de e-mail.
 *
 * Todas las tareas intensivas se ejecutan en el *dispatcher* inyectado
 * con `@IoDispatcher` para preservar la fluidez de la UI.
 */
package com.app.tibibalance.data.repository

import com.app.tibibalance.data.remote.firebase.AuthService
import com.app.tibibalance.di.IoDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @class  FirebaseAuthRepository
 * @brief  Repositorio de autenticación basado en Firebase.
 *
 * @constructor Inyecta las dependencias necesarias mediante Hilt.
 * @param service   Implementación de [AuthService] usada para autenticarse.
 * @param db        Referencia a [FirebaseFirestore] para persistir perfiles.
 * @param io        *Dispatcher* de IO para operaciones en segundo plano.
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val service : AuthService,
    private val db      : FirebaseFirestore,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {

    /* ───────── Estado de sesión ───────── */

    /** Flujo reactivo que emite `true` si hay sesión activa. */
    override val isLoggedIn : Flow<Boolean> = service.authState

    /** Usuario actual proporcionado por Firebase; `null` si no hay sesión. */
    override val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    /* ───────── Correo / contraseña ───────── */

    /**
     * @copydoc AuthRepository.signUp
     *
     * Crea o actualiza el documento de perfil si aún no existe.
     */
    override suspend fun signUp(email: String, pass: String) {
        val user = service.signUp(email, pass)
        createProfileIfAbsent(user)
    }

    /**
     * @copydoc AuthRepository.signIn
     */
    override suspend fun signIn(email: String, pass: String) {
        val user = service.signIn(email, pass)
        createProfileIfAbsent(user)
    }

    /* ─────────── Google One-Tap ─────────── */

    /**
     * @copydoc AuthRepository.signInGoogle
     */
    override suspend fun signInGoogle(idToken: String) {
        val user = service.signInGoogle(idToken)
        createProfileIfAbsent(user)
    }

    /* ───────────── Utilidades ───────────── */

    /**
     * @copydoc AuthRepository.resetPass
     */
    override suspend fun resetPass(email: String) =
        service.sendPasswordReset(email)

    /**
     * @copydoc AuthRepository.signOut
     */
    override suspend fun signOut() = withContext(io) { service.signOut() }

    /**
     * @copydoc AuthRepository.syncVerification
     *
     * @return `true` si el correo ya estaba o quedó marcado como verificado.
     */
    override suspend fun syncVerification(): Boolean = withContext(io) {
        val user = FirebaseAuth.getInstance().currentUser ?: return@withContext false
        if (!user.isEmailVerified) return@withContext false

        val ref  = db.collection("profiles").document(user.uid)
        val snap = ref.get().await()
        val verified = snap.getBoolean("verified") ?: false
        if (!verified) ref.update("verified", true)
        true
    }

    /* ───── Registro con datos extra ───── */

    /**
     * @copydoc AuthRepository.signUpEmail
     */
    override suspend fun signUpEmail(
        email    : String,
        pass     : String,
        userName : String,
        birthDate: LocalDate
    ) {
        val user = service.signUpAndVerify(email, pass)
        createProfileIfAbsent(user, userName, birthDate)
    }

    /* ─────────── Helper privado ─────────── */

    /**
     * @brief Crea el documento de perfil si aún no existe.
     *
     * @param user       Usuario autenticado.
     * @param userName   Nombre visible opcional.
     * @param birthDate  Fecha de nacimiento opcional.
     */
    private suspend fun createProfileIfAbsent(
        user      : FirebaseUser,
        userName  : String?    = null,
        birthDate : LocalDate? = null
    ) = withContext(io) {
        val ref = db.collection("profiles").document(user.uid)
        if (!ref.get().await().exists()) {
            ref.set(
                mapOf(
                    "email"     to user.email,
                    "provider"  to user.providerData.first().providerId,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "verified"  to user.isEmailVerified,
                    "userName"  to userName,
                    "birthDate" to birthDate?.toString()
                )
            )
        }
    }
}
