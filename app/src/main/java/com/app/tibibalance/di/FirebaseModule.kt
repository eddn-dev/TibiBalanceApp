package com.app.tibibalance.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de inyección para exponer singletons de Firebase.
 *
 * Al mantener las dependencias de Firebase en la capa *di*:
 *  • Evitamos importarlas en la UI ó Domain         (→ menor acoplamiento).
 *  • Garantizamos una única instancia               (→ mismo user/credenciales).
 *
 * > **Nota**: Si más adelante añades Firestore, Storage, etc.,
 * > agrega aquí sus `@Provides` siguiendo el mismo patrón.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provee la instancia global de [FirebaseApp].
     *
     * Hilt crea el objeto sólo UNA vez por proceso.  Si el desarrollador ya
     * inicializó Firebase en el `Application` (habitual), `initializeApp`
     * devolverá `null`; en tal caso recuperamos la existente vía `getInstance()`.
     */
    @Provides
    @Singleton
    fun provideFirebaseApp(
        @ApplicationContext context: Context
    ): FirebaseApp = FirebaseApp.initializeApp(context) ?: FirebaseApp.getInstance()

    /**
     * Provee [FirebaseAuth] asociado al [FirebaseApp] inyectado arriba.
     *
     * Mantener la referencia principal en DI simplifica tests y permite
     * sustituirla por un *fake* en instrumentación.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(
        firebaseApp: FirebaseApp
    ): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)
}
