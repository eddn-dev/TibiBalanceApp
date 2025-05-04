/**
 * data/repository/FirebaseAuthRepository.kt
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

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val service : AuthService,
    private val db      : FirebaseFirestore,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {

    /* ---------- estado de sesión ---------- */
    override val isLoggedIn : Flow<Boolean> = service.authState
    override val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    /* ---------- correo / contraseña ---------- */
    override suspend fun signUp(email: String, pass: String) {
        val user = service.signUp(email, pass)
        createProfileIfAbsent(user)
    }

    override suspend fun signIn(email: String, pass: String) {
        val user = service.signIn(email, pass)
        createProfileIfAbsent(user)
    }

    /* ---------- Google One-Tap ---------- */
    override suspend fun signInGoogle(idToken: String) {
        val user = service.signInGoogle(idToken)
        createProfileIfAbsent(user)
    }

    /* ---------- utilidades ---------- */
    override suspend fun resetPass(email: String) =
        service.sendPasswordReset(email)

    override suspend fun signOut() = withContext(io) { service.signOut() }

    override suspend fun syncVerification(): Boolean = withContext(io) {
        val user = FirebaseAuth.getInstance().currentUser ?: return@withContext false
        if (!user.isEmailVerified) return@withContext false

        val ref  = db.collection("profiles").document(user.uid)
        val snap = ref.get().await()
        val verified = snap.getBoolean("verified") ?: false
        if (!verified) ref.update("verified", true)
        true
    }

    /* ---------- sign-up con datos extra ---------- */
    override suspend fun signUpEmail(
        email    : String,
        pass     : String,
        userName : String,
        birthDate: LocalDate
    ) {
        val user = service.signUpAndVerify(email, pass)
        createProfileIfAbsent(user, userName, birthDate)
    }

    /* ---------- helper privado ---------- */
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
