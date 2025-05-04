package com.app.tibibalance.di

import android.content.Context
import androidx.room.Room
import com.app.tibibalance.data.local.AppDb
import com.app.tibibalance.data.local.ProfileDao
import com.app.tibibalance.data.remote.firebase.ProfileService
import com.app.tibibalance.data.repository.FirebaseProfileRepository
import com.app.tibibalance.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/* ------------ Qualifier para IO Dispatcher ------------ */
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
        val cache = MemoryCacheSettings
            .newBuilder()
            .build()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cache)      // API recomendada (reemplaza setPersistenceEnabled)
            .build()

        return Firebase.firestore.apply { firestoreSettings = settings }
    }

    /* -------- Room -------- */
    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext ctx: Context): AppDb =
        Room.databaseBuilder(ctx, AppDb::class.java, "local.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProfileDao(db: AppDb): ProfileDao = db.profileDao()

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
        dao : ProfileDao,
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher
    ): ProfileRepository = FirebaseProfileRepository(svc, dao, auth, io)
}
