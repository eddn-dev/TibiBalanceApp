// data/repository/AuthRepositoryImpl.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.data.remote.firebase.AuthService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = service.authState

    /* ---------------- Correo / contraseña ---------------- */
    override suspend fun signIn(email: String, pass: String) {
        service.signIn(email, pass)          // devuelve FirebaseUser
        Unit                                 // …pero el contrato pide Unit
    }

    override suspend fun signUp(email: String, pass: String) {
        service.signUpAndVerify(email, pass)
        Unit
    }

    override suspend fun signUpEmail(email: String, pass: String) {
        service.signUpAndVerify(email, pass)
        Unit
    }

    override suspend fun resetPass(email: String) {
        service.sendPasswordReset(email)
        Unit
    }

    /* ---------------- Google One-Tap --------------------- */
    override suspend fun signInGoogle(idToken: String) {
        service.signInGoogle(idToken)
        Unit
    }

    /* ---------------- Sign-out --------------------------- */
    override fun signOut() = service.signOut()
}
