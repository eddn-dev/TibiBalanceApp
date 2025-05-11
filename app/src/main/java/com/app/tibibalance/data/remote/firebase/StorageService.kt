package com.app.tibibalance.data.remote.firebase

import java.io.InputStream
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageService @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfileImage(inputStream: InputStream, uid: String): String {
        val ref = storage.reference.child("profile_images/$uid.jpg")

        // Subir imagen al Storage
        val result = ref.putStream(inputStream).await()

        // Verificar que la subida fue exitosa
        if (result.metadata == null) {
            throw Exception("Falló la carga de imagen: metadata nula")
        }

        // Obtener la URL de descarga
        return ref.downloadUrl.await().toString()
    }
}
