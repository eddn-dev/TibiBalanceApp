// data/repository/FirebaseAuthRepository.kt
package com.app.tibibalance.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ActivityRetainedScoped
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    /* ---------- Estado de sesión ---------- */
    override val isLoggedIn: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    /* ---------- Correo/contraseña ---------- */
    override suspend fun signUp(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass).await()
            .user?.sendEmailVerification()?.await()
    }

    // ⚠️  Alias para la firma antigua
    override suspend fun signUpEmail(email: String, pass: String) =
        signUp(email, pass)

    override suspend fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    override suspend fun resetPass(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /* ---------- Google ---------- */
    override suspend fun signInGoogle(idToken: String) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred).await()
    }

    /* ---------- Sign-out ---------- */
    override fun signOut() = auth.signOut()
}
