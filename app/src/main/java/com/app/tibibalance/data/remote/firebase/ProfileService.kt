package com.app.tibibalance.data.remote.firebase

import com.app.tibibalance.data.remote.ProfileDto
import com.app.tibibalance.di.IoDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/* data/remote/firebase/ProfileService.kt */
class ProfileService @Inject constructor(

    private val fs: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @param:IoDispatcher private val io: CoroutineDispatcher
) {
    private val doc get() = fs.collection("profiles").document(auth.currentUser!!.uid)

    /** Escucha tiempo-real del documento */
    fun listen(): Flow<ProfileDto> = callbackFlow {
        val reg = doc.addSnapshotListener { snap, err ->
            err?.let { close(it) } ?: snap?.toObject(ProfileDto::class.java)?.also { trySend(it) }
        }
        awaitClose { reg.remove() }
    }.flowOn(io)

    /** Actualiza campos (merge) */
    suspend fun update(fields: Map<String, Any?>) = withContext(io) { doc.set(fields, SetOptions.merge()).await() }
}
