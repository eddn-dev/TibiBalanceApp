// AccountRepositoryImpl.kt
package com.app.tibibalance.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val auth        : FirebaseAuth,
    private val db          : FirebaseDatabase,
    private val storage     : FirebaseStorage,
    private val googleClient: GoogleSignInClient
) : AccountRepository {

    override suspend fun deleteAccount(context: Context) {
        val user = auth.currentUser ?: throw IllegalStateException("Usuario nulo")

        // Si es Google, forzamos reauth vía excepción
        if (user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
            throw ReauthWithGoogleRequiredException()
        }

        // Si fuera email/password, pedirías contraseña aquí...
        // … tu código existente para deleteAccount (RTDB, Storage, user.delete(), signOut)
    }

    override suspend fun reauthenticateWithGoogle(idToken: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Usuario nulo")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        user.reauthenticate(credential).await()
    }
}
