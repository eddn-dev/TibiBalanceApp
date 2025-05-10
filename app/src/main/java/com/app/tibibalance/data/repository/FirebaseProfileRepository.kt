package com.app.tibibalance.data.repository

import android.net.Uri
import android.util.Log
import com.app.tibibalance.data.local.dao.ProfileDao
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.ProfileDto
import com.app.tibibalance.data.remote.firebase.ProfileService
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @file    FirebaseProfileRepository.kt
 * @ingroup data_repository
 * @brief   Sincroniza el perfil entre Room (caché) y Firestore (remoto).
 *
 * Estrategia offline-first:
 * 1) Firestore → Room: listener en tiempo real via ProfileService.listen()
 * 2) Room      → UI: expone Flow<UserProfile?> desde Room
 *
 * Implementa ProfileRepository.update(name,photo,birthDate) y clearLocal().
 */
@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val remote: ProfileService,
    private val dao: ProfileDao,
    private val auth: FirebaseAuth,
    @IoDispatcher private val io: CoroutineDispatcher
) : ProfileRepository {

    /** Flujo que emite el perfil actual desde Room. */
    override val profile: Flow<UserProfile?> =
        dao.observe()                              // Flow<UserProfileEntity?>
            .map { it?.toDomain() }                // Flow<UserProfile?>
            .distinctUntilChanged()                // evita duplicados
            .flowOn(io)                            // en IO

    // Scope + lambda para gestionar el listener de Firestore
    private val scope = CoroutineScope(SupervisorJob() + io)
    private var stopListener: () -> Unit = {}

    init {
        // Reconectar al cambiar auth (login/logout)
        auth.addAuthStateListener { fAuth ->
            scope.launch { reconnect(fAuth.currentUser?.uid) }
        }
        // Listener inicial si ya estaba logueado
        scope.launch { reconnect(auth.currentUser?.uid) }
    }

    /**
     * Si uid=null limpia caché; si no arranca listener remoto→Room.
     */
    private suspend fun reconnect(uid: String?) {
        // Detener cualquier listener previo
        stopListener()
        stopListener = {}

        if (uid == null) {
            dao.clear()
            Log.d("ProfileRepo", "Usuario deslogueado: caché local limpia")
            return
        }

        Log.d("ProfileRepo", "Iniciando listener para UID=$uid")
        val job = remote.listen()                      // Flow<ProfileDto>
            .map { dto ->
                // convierte DTO a UserProfile y reformatea fecha
                val raw = dto.birthDate  // asume ProfileDto.birthDate = "yyyy-MM-dd"
                val formatted = raw
                    ?.split("-")
                    ?.takeIf { it.size == 3 }
                    ?.let { (yy, mm, dd) -> "%02d/%02d/%04d".format(dd.toInt(), mm.toInt(), yy.toInt()) }
                dto
                    .toDomain(uid)
                    .copy(birthDate = formatted)
            }
            .onEach { up ->
                dao.upsert(up.toEntity())
                Log.d("ProfileRepo", "Perfil sync en Room para UID=$uid")
            }
            .catch { e ->
                Log.e("ProfileRepo", "Error listener UID=$uid", e)
            }
            .launchIn(scope)

        // Guardar cómo cancelarlo
        stopListener = { job.cancel() }
    }

    /**
     * Actualiza solo los campos no nulos en Firestore:
     * - userName, photoUrl, birthDate.
     * La caché local se sincroniza vía listener.
     */
    override suspend fun update(
        name: String?,
        photo: Uri?,
        birthDate: String?
    ) {
        withContext(io) {
            val patch = mutableMapOf<String, Any>()
            name?.let      { patch["userName"]  = it }
            photo?.let     { patch["photoUrl"]  = it.toString() }
            birthDate?.let { patch["birthDate"] = it }

            if (patch.isNotEmpty()) {
                Log.d("ProfileRepo", "Actualizando Firestore: $patch")
                remote.update(patch)
            } else {
                Log.d("ProfileRepo", "Sin cambios; no se actualiza Firestore")
            }
        }
    }

    /**
     * Limpia la entidad de perfil en Room (caché local).
     */
    override suspend fun clearLocal() {
        withContext(io) {
            dao.clear()
            Log.d("ProfileRepo", "Caché local de perfil limpiada")
        }
    }
}
