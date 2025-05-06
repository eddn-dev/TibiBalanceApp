/* di/FirebaseModule.kt */
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

/* ───────── Qualifier para Dispatcher IO ───────── */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /* -------- Firebase Auth -------- */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /* -------- Firestore con caché en RAM (40 MiB) -------- */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val cache = MemoryCacheSettings.newBuilder().build()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cache)
            .build()
        return Firebase.firestore.apply { firestoreSettings = settings }
    }

    /* -------- Remote Service -------- */
    @Provides
    fun provideProfileService(
        fs: FirebaseFirestore,
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher
    ): ProfileService = ProfileService(fs, auth, io)

    /* -------- Repository -------- */
    @Provides
    @Singleton
    fun provideProfileRepository(
        svc : ProfileService,
        dao : ProfileDao,              // ← lo provee DatabaseModule
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher
    ): ProfileRepository = FirebaseProfileRepository(svc, dao, auth, io)
}
