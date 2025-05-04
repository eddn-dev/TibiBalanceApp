package com.app.tibibalance.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio de solo-lectura para acceder a la colección pública /habitTemplates.
 * Mantiene la lógica en un único punto y facilita la inyección en repositorios.
 */
@Singleton
class TemplateService @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend fun list(): List<Pair<String, Map<String, Any>>> =
        db.collection("habitTemplates").get().await().documents
            .mapNotNull { doc -> doc.id to (doc.data ?: emptyMap()) }

    /**
     * Devuelve el mapa “crudo” del documento de plantilla.
     *
     * • Funciona offline: si el documento ya está en la caché local,
     *   Firestore lo servirá sin red (persistence = true por defecto). :contentReference[oaicite:0]{index=0}
     * • Lanza la excepción de Firebase si el get() falla.
     * • Retorna `emptyMap()` cuando el documento no existe (plantilla eliminada).
     */
    suspend fun fetch(templateId: String): Map<String, Any> =
        db.collection("habitTemplates")
            .document(templateId)
            .get()
            .await()                               // convierte Task a suspensión :contentReference[oaicite:1]{index=1}
            .data ?: emptyMap()
}
