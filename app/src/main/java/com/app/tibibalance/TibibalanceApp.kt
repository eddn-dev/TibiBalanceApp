// app/src/main/java/com/app/tibibalance/TibibalanceApp.kt
package com.app.tibibalance        // usa EXACTAMENTE el mismo package del código

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TibibalanceApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /* Activa el log VERBOSE de Firestore en Logcat */
        FirebaseFirestore.setLoggingEnabled(true)
    }
}
