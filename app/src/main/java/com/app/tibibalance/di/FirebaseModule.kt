/**
 * @file    FirebaseModule.kt
 * @ingroup di_module // Grupo específico para módulos Hilt/DI
 * @brief   Módulo Hilt que provee instancias singleton relacionadas con Firebase (Auth, Firestore)
 * y las dependencias asociadas al perfil de usuario (Service, Repository).
 *
 * @details Este módulo es fundamental para la integración con Firebase en la aplicación.
 * <h4>Responsabilidades</h4>
 * <ul>
 * <li>Configurar y proveer la instancia singleton de [FirebaseAuth].</li>
 * <li>Configurar y proveer la instancia singleton de [FirebaseFirestore], habilitando
 * una caché en memoria para soporte offline básico.</li>
 * <li>Proveer la instancia de [ProfileService], que interactúa directamente con Firestore
 * para los datos del perfil.</li>
 * <li>Proveer la instancia singleton de la implementación [FirebaseProfileRepository]
 * para la interfaz [ProfileRepository], inyectando sus dependencias (servicio remoto, DAO local, Auth, Dispatcher).</li>
 * <li>Declarar el [Qualifier] personalizado [IoDispatcher] para identificar el
 * [CoroutineDispatcher] destinado a operaciones de I/O (red, disco).</li>
 * </ul>
 * Se instala en [SingletonComponent] para que todas las instancias proporcionadas
 * (excepto [ProfileService], que no está anotado) sean singletons a nivel de aplicación.
 *
 * @see FirebaseAuth
 * @see FirebaseFirestore
 * @see ProfileService
 * @see ProfileRepository
 * @see FirebaseProfileRepository
 * @see IoDispatcher
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
/* Qualifier para Dispatcher IO             */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief Qualifier que identifica el [CoroutineDispatcher] optimizado para operaciones de **I/O-bound**.
 * @details Se utiliza para inyectar el dispatcher [Dispatchers.IO] (proporcionado por [DispatcherModule])
 * en los servicios y repositorios que realizan operaciones intensivas de entrada/salida
 * como llamadas de red (Firestore), acceso a disco o interacciones con bases de datos locales (Room).
 * @see DispatcherModule Módulo que provee la instancia real de Dispatchers.IO con este Qualifier.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/* ────────────────────────────────────────────────────────────── */
/* Módulo Hilt para Firebase y Perfil         */
/* ────────────────────────────────────────────────────────────── */

/**
 * @brief Módulo de Dagger Hilt que proporciona dependencias de Firebase y perfil.
 * @details Instalado en [SingletonComponent], la mayoría de las instancias son singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /* ─────────────── Proveedor Firebase Auth ─────────────── */

    /**
     * @brief Provee la instancia singleton de [FirebaseAuth].
     * @details Obtiene la instancia estándar de Firebase Authentication.
     * @return La instancia singleton de [FirebaseAuth].
     */
    @Provides
    @Singleton // Asegura una única instancia de FirebaseAuth
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /* ─────────────── Proveedor Firebase Firestore ─────────────── */

    /**
     * @brief Provee la instancia singleton de [FirebaseFirestore] configurada con caché en memoria.
     * @details Configura Firestore para usar una caché en memoria (tamaño por defecto del builder,
     * usualmente alrededor de 40 MiB), lo que permite cierto grado de funcionamiento offline
     * para lecturas de datos previamente cacheados.
     * @return La instancia singleton de [FirebaseFirestore] configurada.
     */
    @Provides
    @Singleton // Asegura una única instancia de FirebaseFirestore
    fun provideFirestore(): FirebaseFirestore {
        // Configuración de caché en memoria (permite lecturas offline si los datos están cacheados)
        val cacheSettings = MemoryCacheSettings.newBuilder().build()
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cacheSettings)
            // .setPersistenceEnabled(true) // Considerar habilitar persistencia en disco para offline más robusto
            .build()

        // Obtiene la instancia de Firestore y aplica la configuración
        return Firebase.firestore.apply {
            this.firestoreSettings = firestoreSettings
        }
    }

    /* ─────────────── Proveedor del Servicio Remoto (ProfileService) ─────────────── */

    /**
     * @brief Provee una instancia de [ProfileService].
     * @details Este provider crea una nueva instancia de [ProfileService] cada vez que se solicita
     * (a menos que el scope del componente donde se inyecta sea más limitado y lo reutilice).
     * Inyecta [FirebaseFirestore], [FirebaseAuth] y el [CoroutineDispatcher] IO.
     * **Nota:** No está anotado como `@Singleton`. Considerar si debería serlo para reutilizar la instancia.
     *
     * @param fs La instancia singleton de [FirebaseFirestore].
     * @param auth La instancia singleton de [FirebaseAuth].
     * @param io El [CoroutineDispatcher] para operaciones IO (inyectado con `@IoDispatcher`).
     * @return Una instancia de [ProfileService].
     */
    @Provides
    // @Singleton // Descomentar si ProfileService debe ser singleton
    fun provideProfileService(
        fs: FirebaseFirestore,
        auth: FirebaseAuth,
        @IoDispatcher io: CoroutineDispatcher // Inyecta el dispatcher IO
    ): ProfileService = ProfileService(fs, auth, io)

    /* ─────────────── Proveedor del Repositorio (ProfileRepository) ─────────────── */

    /**
     * @brief Provee la instancia singleton de la implementación [FirebaseProfileRepository] para la interfaz [ProfileRepository].
     * @details Hilt inyectará las dependencias requeridas por [FirebaseProfileRepository]:
     * [ProfileService] (provisto arriba), [ProfileDao] (provisto por [DatabaseModule]),
     * [FirebaseAuth] (provisto arriba), y el [CoroutineDispatcher] IO.
     *
     * @param svc La instancia de [ProfileService].
     * @param dao La instancia de [ProfileDao] (provista por [DatabaseModule]).
     * @param auth La instancia singleton de [FirebaseAuth].
     * @param io El [CoroutineDispatcher] para operaciones IO (inyectado con `@IoDispatcher`).
     * @return La instancia singleton de [FirebaseProfileRepository] casteada a la interfaz [ProfileRepository].
     */
    @Provides
    @Singleton // Asegura una única instancia del repositorio
    fun provideProfileRepository(
        svc : ProfileService, // Inyecta el servicio provisto arriba
        dao : ProfileDao,     // Inyecta el DAO provisto por DatabaseModule
        auth: FirebaseAuth,   // Inyecta FirebaseAuth
        @IoDispatcher io: CoroutineDispatcher // Inyecta el dispatcher IO
    ): ProfileRepository = FirebaseProfileRepository(svc, dao, auth, io) // Crea y devuelve la implementación
}
