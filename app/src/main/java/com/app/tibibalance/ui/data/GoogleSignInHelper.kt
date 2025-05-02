package com.app.tibibalance.ui.data


import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

class GoogleSignInHelper(
    private val activity: Activity,
    private val webClientId: String   // 👉 tu Client‑ID Web de Firebase
) {
    private val auth: FirebaseAuth = Firebase.auth

    /** Cliente clásico de Google Sign‑In */
    private val signInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)   // necesario para Firebase
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    /** Devuelve el Intent para lanzar el selector de cuentas */
    fun getSignInIntent(): Intent = signInClient.signInIntent

    /** Cierra sesión en el cliente para que el selector se muestre de nuevo */
    fun signOut(onComplete: () -> Unit) =
        signInClient.signOut().addOnCompleteListener { onComplete() }

    /**
     * Maneja el resultado del ActivityResultLauncher.
     *
     * @param data  El Intent recibido en onActivityResult
     * @param onSuccess Llamado cuando el usuario se autentica correctamente
     * @param onFailure Llamado con un mensaje cuando algo falla
     */
    fun handleResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (!task.isSuccessful) {
            onFailure(task.exception?.localizedMessage ?: "Inicio cancelado")
            return
        }

        val account  = task.result
        val email    = account.email
        val idToken  = account.idToken

        if (email == null || idToken == null) {
            onFailure("No se pudo obtener la cuenta de Google")
            return
        }

        // 1 – ¿Existe el correo en Firebase?
        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                if (result.signInMethods.isNullOrEmpty()) {
                    // No está registrado
                    onFailure("Este correo no está registrado.")
                } else {
                    // 2 – Sí existe → autenticar
                    val cred = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(cred)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { ex ->
                            onFailure(ex.localizedMessage ?: "Fallo al autenticar con Google.")
                        }
                }
            }
            .addOnFailureListener { ex ->
                onFailure(ex.localizedMessage ?: "Error al verificar el correo.")
            }
    }
}