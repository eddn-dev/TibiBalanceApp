// data/remote/firebase/AuthService.kt
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Fuente de datos que envuelve **FirebaseAuth**.
 * Devuelve el [FirebaseUser] resultante para que el repositorio
 * pueda crear/actualizar su perfil en Firestore.
 */
interface AuthService {

    /* ─── Sesión ─────────────────────────────────────────────── */
    val authState: Flow<Boolean>        // true si hay usuario logueado

    /* ─── Correo / contraseña ───────────────────────────────── */
    suspend fun signIn(email: String, pass: String): FirebaseUser
    suspend fun signUp(email: String, pass: String): FirebaseUser          // sin e-mail verify
    suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser // crea y envía e-mail
    suspend fun sendPasswordReset(email: String)                           // Unit

    /* ─── Google One-Tap ────────────────────────────────────── */
    suspend fun signInGoogle(idToken: String): FirebaseUser

    /* ─── Cerrar sesión ─────────────────────────────────────── */
    suspend fun signOut()
}
