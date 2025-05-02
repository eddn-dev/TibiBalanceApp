package com.app.tibibalance.ui.data

import com.google.firebase.auth.FirebaseAuth

object AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error desconocido")
            }

    }
}