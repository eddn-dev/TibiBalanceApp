/* data/repository/FirebaseProfileRepository.kt */
package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.data.local.dao.ProfileDao
import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.firebase.ProfileService
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @file    FirebaseProfileRepository.kt
 * @ingroup data_repository
 * @brief   Repositorio que sincroniza el perfil de usuario entre Room y Firestore.
 *
 * - **Firestore → Room**: Un listener en tiempo real (`ProfileService.listen`)
 *   actualiza la caché local (`ProfileDao.upsert`).
 * - **Room → UI**: La capa de presentación observa [profile] para obtener
 *   el estado más reciente.
 *
 * El repositorio re-conecta el listener cuando cambia la sesión en
 * [FirebaseAuth].  Todas las operaciones de E/S se ejecutan en el
 * *dispatcher* inyectado con `@IoDispatcher`.
 */
@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val remote : ProfileService,   /**< Servicio Firestore.   */
    private val dao    : ProfileDao,       /**< DAO de Room (tabla profile). */
    private val auth   : FirebaseAuth,     /**< SDK de autenticación. */
    @param:IoDispatcher private val io: CoroutineDispatcher
) : ProfileRepository {

    /* ─────────────── Flujo observable ─────────────── */

    /**
     * @brief Flujo reactivo del perfil de usuario cacheado.
     *
     * Emite un [UserProfile] cuando cambia la fila única en la tabla
     * `profile`.  El operador `distinctUntilChanged` evita emisiones
     * redundantes.
     */
    override val profile: Flow<UserProfile> =
        dao.observe()                        // Flow<UserProfileEntity?>
            .filterNotNull()
            .map(UserProfileEntity::toDomain)
            .distinctUntilChanged()

    /* ─────────────── Sincronización ─────────────── */

    /** Ámbito dedicado a la sincronización remota → local. */
    private val scope = CoroutineScope(SupervisorJob() + io)

    /** Función nula que almacena la cancelación del listener remoto. */
    private var stopListener: () -> Unit = {}

    init {
        // Re-conecta cuando cambia el UID (login / logout).
        auth.addAuthStateListener { fb ->
            scope.launch { reconnect(fb.currentUser?.uid) }
        }
        // Conexión inicial.
        scope.launch { reconnect(auth.currentUser?.uid) }
    }

    /**
     * @brief Reinicia el listener remoto y mantiene Room sincronizado.
     *
     * @param uid UID del usuario autenticado o `null` (logout).
     */
    private suspend fun reconnect(uid: String?) {
        stopListener(); stopListener = {}

        if (uid == null) {               // logout ⇒ limpiar caché
            dao.clear(); return
        }

        stopListener = remote.listen()   // Flow<ProfileDto>
            .map { it.toDomain(uid) }    // DTO → dominio
            .onEach { dao.upsert(it.toEntity()) }  // dominio → Room
            .launchIn(scope)
            .let { { it.cancel() } }     // devuelve lambda para cancelar
    }

    /* ─────────────── API pública ─────────────── */

    /**
     * @brief Actualiza nombre y/o foto de perfil.
     *
     * @param name  Nuevo nombre visible (`null` para no modificar).
     * @param photo Nueva URI de foto (`null` para no modificar).
     */
    override suspend fun update(name: String?, photo: Uri?): Unit = withContext(io) {
        val patch = buildMap<String, Any?> {
            name ?.let { put("userName", it) }
            photo?.let { put("photoUrl", it.toString()) }
        }
        remote.update(patch)        // Firestore; el listener actualizará Room
        Unit                        // asegura tipo Unit
    }

    /**
     * @brief Elimina la caché local del perfil (p.ej., al cerrar sesión).
     */
    override suspend fun clearLocal() = dao.clear()
}
