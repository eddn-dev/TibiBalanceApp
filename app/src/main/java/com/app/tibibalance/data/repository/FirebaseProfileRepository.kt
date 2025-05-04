/* data/repository/FirebaseProfileRepository.kt */
package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.data.local.ProfileDao
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
    @IoDispatcher private val io: CoroutineDispatcher
) : ProfileRepository {

    override val profile: Flow<UserProfile> =
        dao.observe().filterNotNull().distinctUntilChanged()

    /* scope propio del repo */
    private val scope = CoroutineScope(SupervisorJob() + io)

    private var stopListener: () -> Unit = {}

    init {
        auth.addAuthStateListener { fb -> scope.launch { reconnect(fb.currentUser?.uid) } }
        scope.launch { reconnect(auth.currentUser?.uid) }
    }

    /** Reinicia listener y cache local */
    private suspend fun reconnect(uid: String?) {
        stopListener()
        stopListener = {}

        if (uid == null) { dao.clear(); return }

        stopListener = remote.listen()              // Flow<ProfileDto>
            .map { it.toDomain(uid) }
            .onEach { dao.upsert(it) }
            .launchIn(scope)
            .let { { it.cancel() } }
    }

    /* ------------ API pública ------------ */
    override suspend fun update(name: String?, photo: Uri?) = withContext(io) {
        val patch = buildMap<String, Any?> {
            name ?.let { put("userName", it) }
            photo?.let { put("photoUrl", it.toString()) }
        }
        remote.update(patch)   // devuelve lo que sea → lo ignoramos
        Unit                   // ← asegura tipo Unit
    }

    override suspend fun clearLocal() = dao.clear()
}
