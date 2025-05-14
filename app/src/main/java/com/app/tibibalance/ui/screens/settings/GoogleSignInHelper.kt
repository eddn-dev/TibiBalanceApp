package com.app.tibibalance.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException

class GoogleSignInHelper(private val context: Context) {

    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(context)
    }

    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("467927540157-tvu0re0msga2o01tsj9t1r1o6kqvek3j.apps.googleusercontent.com") // ← usa el tuyo
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun getSignInIntent(
        onSuccess: (IntentSender) -> Unit,
        onError: (Exception) -> Unit
    ) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                onSuccess(result.pendingIntent.intentSender)
            }
            .addOnFailureListener { onError(it) }
    }

    fun getCredentialFromIntent(intent: Intent?): String? {
        return try {
            val credential: SignInCredential =
                Identity.getSignInClient(context).getSignInCredentialFromIntent(intent)
            credential.googleIdToken
        } catch (e: ApiException) {
            null
        }
    }
}
