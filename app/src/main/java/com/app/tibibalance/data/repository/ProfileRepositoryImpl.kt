//ProfileRepositoryImpl.kt
package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    /**
     * Flujo que emite el UserProfile cada vez que cambia en Firestore.
     */
    override val profile: Flow<UserProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid
            ?: run { close(); return@callbackFlow }

        val listener = firestore
            .collection("users")
            .document(uid)
            .addSnapshotListener { snap, _ ->
                val profile = snap?.takeIf { it.exists() }?.let { doc ->
                    UserProfile(
                        uid        = uid,
                        userName   = doc.getString("userName"),
                        email      = doc.getString("email"),
                        birthDate  = doc.getString("birthDate"),
                        photoUrl   = doc.getString("photoUrl")
                    )
                }
                trySend(profile).isSuccess
            }

        awaitClose { listener.remove() }
    }


    /**
     * Actualiza nombre, foto y/o fecha de nacimiento según los parámetros no nulos.
     */
    override suspend fun update(
        name: String?,
        photo: Uri?,
        birthDate: String?
    ) {
        val user = auth.currentUser ?: return
        val uid  = user.uid
        val updates = mutableMapOf<String, Any>()

        // 1) Nombre
        name?.let {
            user.updateProfile(userProfileChangeRequest { displayName = it }).await()
            updates["userName"] = it
        }

        // 2) Foto (impleméntalo más adelante)
        photo?.let {
            // sube la Uri a Storage, obtén la URL y:
            updates["photoUrl"] = it.toString()
        }

        // 3) Fecha de nacimiento
        birthDate?.let {
            updates["birthDate"] = it
        }

        // 4) Merge en Firestore
        if (updates.isNotEmpty()) {
            firestore.collection("users")
                .document(uid)
                .set(updates, SetOptions.merge())
                .await()
        }
    }

    /**
     * Limpia la caché local (e.g., Room). Si no usas caché local, déjalo vacío.
     */
    override suspend fun clearLocal() {
        // tu lógica de limpieza local, si aplica
    }
}
