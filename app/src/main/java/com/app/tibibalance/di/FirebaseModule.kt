/**
 * @file    FirebaseModule.kt
 * @ingroup di
 * @brief   Módulo Hilt que provee instancias de **Firebase** y su repositorio de perfil.
 *
 * <h4>Responsabilidades</h4>
 * - Configurar e inyectar <code>FirebaseAuth</code> y <code>FirebaseFirestore</code>.
 * - Exponer un <code>ProfileService</code> remoto y su correspondiente
 *   <code>ProfileRepository</code> (sincronización Room ↔ Firestore).
 * - Declarar el *Qualifier* {@link IoDispatcher} para diferenciar el
 *   <code>CoroutineDispatcher</code> destinado a operaciones de I/O.
 */
package com.app.tibibalance.di

import com.app.tibibalance.data.local.dao.ProfileDao
import com.app.tibibalance.data.remote.firebase.ProfileService
import com.app.tibibalance.data.repository.FirebaseProfileRepository
import com.app.tibibalance.data.repository.ProfileRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.FirebaseFirestoreSettings


/* ────────────────────────────────────────────────────────────── */
/*                           Qualifier                            */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief Qualifier que identifica el *dispatcher* **IO-bound**.
 *
 * Se utiliza para inyectar el dispatcher apropiado en los servicios y
 * repositorios que realizan operaciones de entrada/salida intensivas
 * (red, disco, Room, etc.).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/* ────────────────────────────────────────────────────────────── */
/*                           Módulo                               */
/* ────────────────────────────────────────────────────────────── */

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /* ─────────────── Firebase Auth ─────────────── */

    /**
     * @brief Provee la instancia singleton de {@link FirebaseAuth}.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /* ─────────────── Firebase Firestore ─────────────── */

    /**
     * @brief Provee la instancia singleton de {@link FirebaseFirestore}.
     *
     * @details Configura una caché en memoria de 40 MiB (valor por defecto
     *          del builder) para habilitar funcionamiento offline.
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val cache = MemoryCacheSettings.newBuilder().build()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cache)
            .build()
        return Firebase.firestore.apply { firestoreSettings = settings }
    }

    /* ─────────────── Remote Service ─────────────── */

    /**
     * @brief Provee el servicio remoto que gestiona el documento
     *        <code>profiles/{uid}</code> en Firestore.
     *
     * @param fs  Instancia de Firestore.
     * @param auth Instancia de FirebaseAuth.
     * @param io  Dispatcher **IO-bound** inyectado.
     * @return    {@link ProfileService}.
     */
    @Provides
    fun provideProfileService(
        fs: FirebaseFirestore,
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher
    ): ProfileService = ProfileService(fs, auth, io)

    /* ─────────────── Repositorio ─────────────── */

    /**
     * @brief Provee la implementación de {@link ProfileRepository}.
     *
     * @param svc  Servicio remoto de Firestore.
     * @param dao  DAO local para la tabla <code>profile</code>.
     * @param auth SDK de autenticación.
     * @param io   Dispatcher **IO-bound**.
     * @return     {@link FirebaseProfileRepository}.
     */
    @Provides
    @Singleton
    fun provideProfileRepository(
        svc : ProfileService,
        dao : ProfileDao,              // ← lo provee DatabaseModule
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher
    ): ProfileRepository = FirebaseProfileRepository(svc, dao, auth, io)
}
