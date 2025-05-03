// data/remote/firebase/FirebaseAuthService.kt
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.app.tibibalance.di.IoDispatcher

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthService {

    override val authState = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flowOn(io)

    override suspend fun signIn(email: String, pass: String) {
        withContext(io) {
            auth.signInWithEmailAndPassword(email, pass).await()
        }
    }

    override suspend fun signUp(email: String, pass: String) {
        withContext(io) {
            auth.createUserWithEmailAndPassword(email, pass).await()
        }
    }

    override suspend fun sendPasswordReset(email: String) {
        withContext(io) {
            auth.sendPasswordResetEmail(email).await()
        }
    }

    override fun signOut() = auth.signOut()
}
