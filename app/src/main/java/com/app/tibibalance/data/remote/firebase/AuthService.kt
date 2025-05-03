// data/remote/firebase/AuthService.kt
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Contrato de autenticación que oculta el SDK de Firebase.
 */
interface AuthService {
    val authState: Flow<Boolean>            // true si hay usuario logeado
    suspend fun signIn(email: String, pass: String)
    suspend fun signUp(email: String, pass: String)
    suspend fun sendPasswordReset(email: String)
    suspend fun updatePassword(password: String)
    fun signOut()
}
