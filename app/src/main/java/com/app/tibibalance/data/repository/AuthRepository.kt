// data/repository/AuthRepository.kt
package com.app.tibibalance.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val currentUser: FirebaseUser?
    suspend fun signIn(email: String, pass: String)
    suspend fun signUp(email: String, pass: String)
    suspend fun resetPass(email: String)
    suspend fun signUpEmail(email: String, pass: String): Unit
    suspend fun signInGoogle(idToken: String): Unit
    suspend fun syncVerification(): Boolean
    fun signOut()
}
