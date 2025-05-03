// data/remote/firebase/AuthService.kt
package com.app.tibibalance.data.remote.firebase

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de autenticación que oculta el SDK de Firebase.
 */
interface AuthService {
    val authState: Flow<Boolean>            // true si hay usuario logeado
    suspend fun signIn(email: String, pass: String)
    suspend fun signUp(email: String, pass: String)
    suspend fun sendPasswordReset(email: String)
    fun signOut()
}
