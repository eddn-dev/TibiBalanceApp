package com.app.wear

import android.app.Application
import android.provider.Settings
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WearApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // 1) Autenticación anónima
        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener

                // 2) Obtén un ID único para el dispositivo (ANDROID_ID)
                val deviceId = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )

                // 3) Escribe/actualiza el documento de “conexión” en Firestore
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("connections")
                    .document(deviceId)
                    .set(mapOf(
                        "lastSeen" to FieldValue.serverTimestamp(),
                        "platform" to "wear"
                    ))
                    .addOnSuccessListener {
                        Log.i("WearApp", "Ping de conexión enviado: $deviceId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("WearApp", "Error al enviar ping de conexión", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("WearApp", "Error en autenticación anónima", e)
            }
    }
}
