// data/repository/AuthRepositoryImpl.kt
package com.app.tibibalance.data.repository

import com.app.tibibalance.data.remote.firebase.AuthService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = service.authState
    override suspend fun signIn(e: String, p: String)   = service.signIn(e, p)
    override suspend fun signUp(e: String, p: String)   = service.signUp(e, p)
    override suspend fun resetPass(e: String)           = service.sendPasswordReset(e)
    override suspend fun signUpEmail(email: String, pass: String) {
        TODO("Not yet implemented")
    }

    override suspend fun signInGoogle(idToken: String) {
        TODO("Not yet implemented")
    }

    override fun signOut()                              = service.signOut()
}
