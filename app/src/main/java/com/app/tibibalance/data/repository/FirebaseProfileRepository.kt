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

@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val remote : ProfileService,
    private val dao    : ProfileDao,
    private val auth   : FirebaseAuth,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : ProfileRepository {

    /* ───────── flujo observable ───────── */
    /* data/repository/FirebaseProfileRepository.kt */
    override val profile: Flow<UserProfile> =
        dao.observe()                        // Flow<UserProfileEntity?>
            .filterNotNull()
            .map(UserProfileEntity::toDomain)   // ✅ referencia de función = suspend
            .distinctUntilChanged()


    /* scope propio del repo para sincronización */
    private val scope = CoroutineScope(SupervisorJob() + io)
    private var stopListener: () -> Unit = {}

    init {
        auth.addAuthStateListener { fb ->
            scope.launch { reconnect(fb.currentUser?.uid) }
        }
        scope.launch { reconnect(auth.currentUser?.uid) }
    }

    /** Reinicia listener remoto y sincroniza con Room */
    private suspend fun reconnect(uid: String?) {
        stopListener(); stopListener = {}

        if (uid == null) {               // logout
            dao.clear(); return
        }

        stopListener = remote.listen()   // Flow<ProfileDto>
            .map { it.toDomain(uid) }    // dto -> dominio
            .onEach { dao.upsert(it.toEntity()) }  // dominio -> Room
            .launchIn(scope)
            .let { { it.cancel() } }
    }

    /* ───────── API pública ───────── */
    override suspend fun update(name: String?, photo: Uri?): Unit = withContext(io) {
        val patch = buildMap<String, Any?> {
            name ?.let { put("userName", it) }
            photo?.let { put("photoUrl", it.toString()) }
        }
        remote.update(patch)        // Firestore; ignoramos retorno
        Unit                        // asegura tipo Unit
    }

    override suspend fun clearLocal() = dao.clear()
}
