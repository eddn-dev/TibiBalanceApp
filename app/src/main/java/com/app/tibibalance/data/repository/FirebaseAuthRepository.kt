// data/repository/FirebaseAuthRepository.kt
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
    private val service: AuthService,
    private val db: FirebaseFirestore,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = service.authState
    override val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    /* ------------ Registro SIN verificación ------------ */
    override suspend fun signUp(email: String, pass: String) {
        val user = service.signUp(email, pass)
        createProfileIfAbsent(user)
    }

    /* ------------ Inicio de sesión e-mail -------------- */
    override suspend fun signIn(email: String, pass: String) {
        val user = service.signIn(email, pass)
        createProfileIfAbsent(user)          // por si inicia en nuevo dispositivo
    }

    /* ------------ Google One-Tap ----------------------- */
    override suspend fun signInGoogle(idToken: String) {
        val user = service.signInGoogle(idToken)
        createProfileIfAbsent(user)
    }

    /* ------------ Utilidades --------------------------- */
    override suspend fun resetPass(email: String) =
        service.sendPasswordReset(email)

    override suspend fun signOut() = withContext(io) { service.signOut() }

    /* ------------ Helper privado ----------------------- */
    private suspend fun createProfileIfAbsent(
        user      : FirebaseUser,
        userName  : String?    = null,
        birthDate : LocalDate? = null
    ) {
        val ref = db.collection("profiles").document(user.uid)
        if (!ref.get().await().exists()) {
            ref.set(
                mapOf(
                    "email"      to user.email,
                    "provider"   to user.providerData.first().providerId,
                    "createdAt"  to FieldValue.serverTimestamp(),
                    "verified"   to user.isEmailVerified,
                    /* nuevos campos */
                    "userName"   to userName,
                    "birthDate"  to birthDate?.toString()       // ISO-8601
                )
            )
        }
    }

    override suspend fun syncVerification(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        if (!user.isEmailVerified) return false                  // nada que hacer

        val ref = db.collection("profiles").document(user.uid)
        val snap = ref.get().await()
        val verifiedInDb = snap.getBoolean("verified") ?: false

        if (!verifiedInDb) {
            ref.update("verified", true)
        }
        return true                                             // estaba o quedó en true
    }

    /* data/repository/FirebaseAuthRepository.kt */
    override suspend fun signUpEmail(
        email    : String,
        pass     : String,
        userName : String,
        birthDate: LocalDate
    ) {
        val user = service.signUpAndVerify(email, pass)
        createProfileIfAbsent(user, userName, birthDate)
    }
}
