/**
 * @file    ProfileService.kt
 * @ingroup data_remote_service // Grupo específico para servicios remotos
 * @brief   Servicio para interactuar con el documento de perfil del usuario en Firebase Firestore.
 *
 * @details Esta clase encapsula las operaciones de lectura en tiempo real y escritura parcial
 * sobre el documento ubicado en `profiles/{uid}` dentro de Firestore. Utiliza el UID
 * del usuario actualmente autenticado a través de [FirebaseAuth] para identificar
 * el documento correcto.
 *
 * Ofrece las siguientes funcionalidades principales:
 * - **`listen()`**: Proporciona un [Flow] que emite el [ProfileDto] del usuario cada vez
 * que el documento remoto cambia. Utiliza `callbackFlow` y `addSnapshotListener`.
 * - **`update()`**: Permite actualizar campos específicos del perfil utilizando una
 * operación `set` con `SetOptions.merge()`, asegurando que solo los campos
 * proporcionados se modifiquen o añadan.
 *
 * Todas las operaciones de red se ejecutan en el [CoroutineDispatcher] de IO inyectado
 * (marcado con `@IoDispatcher`) para evitar bloqueos en el hilo principal.
 */
package com.app.tibibalance.data.remote.firebase

import com.app.tibibalance.data.remote.ProfileDto
import com.app.tibibalance.di.IoDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject // Asegúrate que este es el import correcto para Hilt en tu proyecto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * @class   ProfileService
 * @brief   Servicio que gestiona la lectura y escritura del perfil de usuario en Firestore.
 * @see com.app.tibibalance.data.repository.FirebaseProfileRepository Repositorio que consume este servicio.
 *
 * @constructor Inyecta las dependencias [FirebaseFirestore], [FirebaseAuth] y el [CoroutineDispatcher] de IO.
 * @param fs Instancia de [FirebaseFirestore] para acceder a la base de datos.
 * @param auth Instancia de [FirebaseAuth] para obtener el UID del usuario actual.
 * @param io Dispatcher de Coroutines configurado para operaciones de entrada/salida.
 */
class ProfileService @Inject constructor(
    private val fs: FirebaseFirestore,        /**< Instancia de Firestore inyectada. */
    private val auth: FirebaseAuth,           /**< SDK de autenticación inyectado. */
    @param:IoDispatcher private val io: CoroutineDispatcher /**< Dispatcher IO inyectado. */
) {

    /**
     * @brief Obtiene la referencia al documento Firestore del perfil del usuario actual.
     * @details Utiliza el UID del `auth.currentUser`. Lanza una [IllegalStateException]
     * si no hay ningún usuario autenticado cuando se accede a esta propiedad.
     * @return [com.google.firebase.firestore.DocumentReference] al documento `profiles/{uid}`.
     * @throws IllegalStateException si `auth.currentUser` es `null`.
     */
    private val userDocRef
        get() = fs.collection("profiles").document(
            auth.currentUser?.uid ?: error("Usuario no autenticado al acceder a ProfileService.userDocRef")
        )

    /* ───────────── Escucha reactiva (Real-time Listener) ───────────── */

    /**
     * @brief Devuelve un [Flow] que emite el [ProfileDto] del usuario en tiempo real.
     *
     * @details Establece un `SnapshotListener` en el documento del perfil del usuario actual.
     * Utiliza `callbackFlow` para convertir el listener basado en callbacks de Firestore
     * en un Flow de Kotlin. Emite un nuevo [ProfileDto] cada vez que el documento cambia.
     * Si el documento no existe o está vacío, emite un `ProfileDto()` por defecto.
     * El Flow se cierra automáticamente (`awaitClose`) cuando el colector deja de escuchar,
     * eliminando el listener de Firestore. Toda la operación se ejecuta en el dispatcher IO.
     *
     * @return [Flow] que emite el [ProfileDto] más reciente. Emite un DTO vacío si el documento no existe.
     * El Flow se cerrará con una excepción si ocurre un error en el listener de Firestore
     * o si no hay un usuario autenticado al momento de la suscripción.
     * @throws IllegalStateException Si no hay usuario autenticado cuando se inicia la colecta del Flow.
     */
    fun listen(): Flow<ProfileDto> = callbackFlow {
        val uid = auth.currentUser?.uid
        // Verifica el UID al inicio de la colecta
        if (uid == null) {
            close(IllegalStateException("No hay usuario autenticado para escuchar el perfil."))
            return@callbackFlow
        }
        val docRef = fs.collection("profiles").document(uid)

        // Registra el listener de Firestore
        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Si hay un error en Firestore, cierra el Flow con la excepción
                close(error)
                return@addSnapshotListener
            }
            // Intenta convertir el snapshot a ProfileDto. Si es null o falla, usa un DTO vacío.
            val profileDto = snapshot?.toObject(ProfileDto::class.java) ?: ProfileDto()
            // Intenta enviar el DTO al Flow. Si falla (e.g., el Flow ya está cerrado), no hace nada.
            trySend(profileDto).isSuccess
        }

        // Define la acción de limpieza que se ejecuta cuando el Flow es cancelado
        awaitClose { listenerRegistration.remove() }
    }.flowOn(io) // Ejecuta el bloque callbackFlow y la emisión en el dispatcher IO

    /* ───────────── Actualización parcial (Update / Merge) ───────────── */

    /**
     * @brief Actualiza campos específicos del perfil del usuario en Firestore.
     *
     * @details Utiliza `set` con `SetOptions.merge()` para actualizar solo los campos
     * proporcionados en el mapa `fields`. Los campos no incluidos en el mapa
     * permanecerán sin cambios en Firestore. Si un valor en el mapa es `null`,
     * Firestore eliminará ese campo del documento. La operación se ejecuta de
     * forma asíncrona en el dispatcher IO.
     *
     * @param fields Un [Map] donde las claves son los nombres de los campos a actualizar
     * y los valores son los nuevos valores. Valores `null` eliminarán el campo.
     * @throws IllegalStateException Si no hay un usuario autenticado al momento de llamar la función.
     * @throws FirebaseFirestoreException Si ocurre un error durante la escritura en Firestore.
     */
    suspend fun update(fields: Map<String, Any?>): Unit = withContext(io) {
        // Obtiene la referencia al documento dentro del contexto de la corrutina
        val docRef = userDocRef // Esto lanzará error si no hay usuario
        docRef.set(fields, SetOptions.merge()).await() // Ejecuta la actualización y espera a que termine
    }
}
