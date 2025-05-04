// data/remote/firebase/FirebaseAuthService.kt
package com.app.tibibalance.data.remote.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.app.tibibalance.di.IoDispatcher

@Singleton
class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthService {

    /* ───────── Estado de sesión (reactivo) ───────── */
    override val authState = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flowOn(io)

    /* ───────── Correo / contraseña ───────── */
    override suspend fun signIn(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.signInWithEmailAndPassword(email, pass).await().user
            ?: error("User is null after sign-in")
    }

    override suspend fun signUp(email: String, pass: String): FirebaseUser = withContext(io) {
        auth.createUserWithEmailAndPassword(email, pass).await().user
            ?: error("User is null after sign-up")
    }

    override suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser = withContext(io) {
        val user = auth.createUserWithEmailAndPassword(email, pass).await().user!!
        user.sendEmailVerification().await()
        Log.d("AUTH", "Verification e-mail sent to ${user.email}")
        user
    }


    override suspend fun sendPasswordReset(email: String) = withContext(io) {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    /* ───────── Google One-Tap / GIS ───────── */
    override suspend fun signInGoogle(idToken: String): FirebaseUser = withContext(io) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred).await().user
            ?: error("User is null after Google sign-in")
    }

    /* ───────── Sign-out ───────── */
    override suspend fun signOut() = withContext(io) { auth.signOut() }
}
