/* data/remote/firebase/ProfileService.kt */
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

/**
 * @file    ProfileService.kt
 * @ingroup data_remote
 * @brief   Servicio Firestore para el documento **profiles/{uid}**.
 *
 * Ofrece:
 * - **`listen()`**: flujo en tiempo real del perfil de usuario.
 * - **`update()`**: actualización parcial (_merge_) de campos.
 *
 * El UID se obtiene del usuario autenticado vía [FirebaseAuth].
 * Todas las operaciones se ejecutan en el *dispatcher* de IO inyectado para
 * evitar bloquear el hilo principal.
 */
class ProfileService @Inject constructor(

    private val fs: FirebaseFirestore,        /**< Instancia de Firestore. */
    private val auth: FirebaseAuth,           /**< SDK de autenticación.  */
    @param:IoDispatcher private val io: CoroutineDispatcher
) {

    /** @brief Referencia perezosa al documento del usuario actual. */
    private val doc
        get() = fs.collection("profiles").document(auth.currentUser!!.uid)

    /* ───────────── Escucha reactiva ───────────── */

    /**
     * @brief Devuelve un flujo que emite el perfil en tiempo real.
     *
     * @return [Flow] que emite [ProfileDto] cada vez que el documento cambia.
     * @throws Exception Propaga errores de escucha si los hubiera.
     */
    fun listen(): Flow<ProfileDto> = callbackFlow {
        val reg = doc.addSnapshotListener { snap, err ->
            err?.let { close(it) }
                ?: snap?.toObject(ProfileDto::class.java)?.also { trySend(it) }
        }
        awaitClose { reg.remove() }
    }.flowOn(io)

    /* ───────────── Actualización parcial ───────────── */

    /**
     * @brief Actualiza campos del perfil haciendo *merge* con el documento existente.
     *
     * @param fields Mapa `clave → valor` con los campos a actualizar.
     *               Los valores `null` eliminan la clave del documento.
     */
    suspend fun update(fields: Map<String, Any?>) = withContext(io) {
        doc.set(fields, SetOptions.merge()).await()
    }
}
