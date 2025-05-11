// AccountRepository.kt
package com.app.tibibalance.data.repository

import android.content.Context

interface AccountRepository {
    /** Borra la cuenta del usuario; lanza ReauthWithGoogleRequiredException si toca re-auth. */
    suspend fun deleteAccount(context: Context)

    /** Re-autentica la cuenta Google usando el idToken obtenido del launcher. */
    suspend fun reauthenticateWithGoogle(idToken: String)
}
