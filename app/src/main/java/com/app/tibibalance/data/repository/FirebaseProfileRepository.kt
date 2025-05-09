/**
 * @file    FirebaseProfileRepository.kt
 * @ingroup data_repository
 * @brief   Repositorio que sincroniza el perfil de usuario entre Room (caché local) y Firestore (remoto).
 *
 * @details Esta clase implementa la interfaz [ProfileRepository] utilizando una estrategia
 * offline-first. La fuente única de verdad para la UI es la base de datos local Room,
 * gestionada a través del [ProfileDao].
 *
 * La sincronización funciona en dos direcciones:
 * - **Firestore → Room**: Un listener en tiempo real, establecido mediante [ProfileService.listen],
 * observa los cambios en el documento `profiles/{uid}` de Firestore. Cuando se detecta un cambio,
 * los datos (como [ProfileDto]) se mapean al modelo de dominio [UserProfile] y luego a la
 * entidad [UserProfileEntity], que se inserta o actualiza (`upsert`) en la tabla `profile` de Room.
 * - **Room → UI**: La propiedad pública [profile] expone un [Flow] que emite el [UserProfile]
 * desde el [ProfileDao]. La UI observa este flujo para mostrar siempre los datos más recientes
 * disponibles localmente.
 *
 * El repositorio maneja automáticamente la reconexión del listener de Firestore cuando el estado
 * de autenticación del usuario cambia (login/logout), utilizando [FirebaseAuth.addAuthStateListener].
 * Todas las operaciones de base de datos y red se ejecutan en el [CoroutineDispatcher] de IO
 * inyectado (`@IoDispatcher`) para no bloquear el hilo principal.
 *
 * La actualización del perfil ([update]) se realiza primero en Firestore, y la caché local se
 * actualiza posteriormente a través del listener, siguiendo una política implícita de
 * **Last-Write-Wins (LWW)** donde el dato remoto tiene prioridad.
 */
package com.app.tibibalance.data.repository

import android.net.Uri
import android.util.Log // Importado para logging en reconnect
import com.app.tibibalance.data.local.dao.ProfileDao
import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.data.local.mapper.toDomain
import com.app.tibibalance.data.local.mapper.toEntity
import com.app.tibibalance.data.remote.firebase.ProfileService
import com.app.tibibalance.di.IoDispatcher
import com.app.tibibalance.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException // Para @throws
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @class   FirebaseProfileRepository
 * @brief   Implementación singleton de [ProfileRepository] con sincronización Room/Firestore.
 * @see ProfileRepository Contrato que define las operaciones del repositorio de perfil.
 * @see ProfileService Servicio para interactuar con el documento de perfil en Firestore.
 * @see ProfileDao DAO para interactuar con la tabla `profile` en Room.
 *
 * @constructor Inyecta las dependencias necesarias mediante Hilt.
 * @param remote Instancia de [ProfileService] para operaciones remotas.
 * @param dao Instancia de [ProfileDao] para operaciones locales en Room.
 * @param auth Instancia de [FirebaseAuth] para obtener el UID y estado de sesión.
 * @param io Dispatcher de Coroutines para operaciones de IO.
 */
@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val remote : ProfileService,   /**< Servicio para interactuar con Firestore. */
    private val dao    : ProfileDao,       /**< DAO para la base de datos local Room. */
    private val auth   : FirebaseAuth,     /**< Instancia de Firebase Authentication. */
    @param:IoDispatcher private val io: CoroutineDispatcher /**< Dispatcher para operaciones de IO. */
) : ProfileRepository {

    /* ─────────────── Flujo observable (Room -> UI) ─────────────── */

    /**
     * @copydoc ProfileRepository.profile
     * @details Observa la tabla `profile` de Room a través del [dao]. Filtra los casos
     * en que no hay perfil (`null`), mapea la [UserProfileEntity] al modelo de dominio
     * [UserProfile], y utiliza `distinctUntilChanged` para evitar emisiones si los
     * datos del perfil no han cambiado. La operación de mapeo se ejecuta en el dispatcher IO.
     */
    override val profile: Flow<UserProfile> =
        dao.observe()                        // Observa Flow<UserProfileEntity?> desde Room
            .filterNotNull()                 // Solo emite si no es null
            .map { entity -> entity.toDomain() } // Mapea Entity a Domain
            .distinctUntilChanged()          // Emite solo si el objeto UserProfile ha cambiado
            .flowOn(io)                      // Asegura que map y filter se ejecuten en IO

    /* ─────────────── Sincronización (Firestore -> Room) ─────────────── */

    /**
     * @brief Ámbito de Coroutine para gestionar el ciclo de vida del listener de Firestore.
     * @details Utiliza un [SupervisorJob] para que el fallo en una corrutina hija
     * (como un error en el listener) no cancele el scope completo. Se combina con el
     * dispatcher [io] para ejecutar las operaciones de sincronización fuera del hilo principal.
     */
    private val scope = CoroutineScope(SupervisorJob() + io)

    /**
     * @brief Almacena la función lambda que permite detener el listener de Firestore activo.
     * @details Se inicializa con una lambda vacía. Cuando se establece un nuevo listener en [reconnect],
     * esta variable se actualiza con la función `remove()` del [com.google.firebase.firestore.ListenerRegistration].
     * Se llama a `stopListener()` antes de crear uno nuevo para evitar listeners duplicados.
     */
    private var stopListener: () -> Unit = {}

    /**
     * @brief Bloque de inicialización del repositorio.
     * @details Registra un [FirebaseAuth.AuthStateListener] para reaccionar a los cambios
     * de sesión (login/logout). Cuando el estado cambia, llama a [reconnect] con el UID
     * actual (o `null`). También llama a [reconnect] una vez al inicio para establecer
     * el listener inicial si ya hay un usuario logueado.
     */
    init {
        auth.addAuthStateListener { firebaseAuth ->
            // Lanza la reconexión en el scope del repositorio cuando cambia el estado de auth
            scope.launch { reconnect(firebaseAuth.currentUser?.uid) }
        }
        // Establece el listener inicial al crear la instancia del repositorio
        scope.launch { reconnect(auth.currentUser?.uid) }
    }

    /**
     * @brief Detiene el listener de Firestore anterior (si existe) y establece uno nuevo si hay un UID válido.
     * @details Si `uid` es `null` (logout), detiene el listener y limpia la tabla `profile` en Room.
     * Si `uid` no es `null`, establece un nuevo listener usando `remote.listen()`. Este listener
     * recibe [ProfileDto] de Firestore, los mapea a [UserProfile] (añadiendo el `uid`),
     * luego a [UserProfileEntity] y finalmente los guarda/actualiza (`upsert`) en Room.
     * Cualquier error durante la escucha del Flow se loguea. La función para cancelar
     * el nuevo listener se guarda en [stopListener].
     *
     * @param uid El identificador único del usuario autenticado, o `null` si no hay sesión.
     */
    private suspend fun reconnect(uid: String?) {
        stopListener() // Cancela y limpia el listener anterior
        stopListener = {}

        if (uid == null) { // Si el usuario cerró sesión
            dao.clear()    // Limpia la tabla local
            Log.d("ProfileRepo", "Usuario deslogueado, listener detenido y caché limpiada.")
            return
        }

        Log.d("ProfileRepo", "Estableciendo listener de perfil para UID: $uid")
        // Inicia el nuevo listener y guarda la función para detenerlo
        stopListener = remote.listen() // Flow<ProfileDto> desde el servicio
            .map { dto -> dto.toDomain(uid) } // DTO -> UserProfile (Dominio)
            .onEach { userProfile -> // Por cada UserProfile emitido
                dao.upsert(userProfile.toEntity()) // Guarda/Actualiza la entidad en Room
                Log.d("ProfileRepo", "Perfil actualizado en Room desde Firestore para UID: $uid")
            }
            .catch { e -> // Captura errores del Flow (del listener de Firestore)
                Log.e("ProfileRepo", "Error escuchando cambios de perfil para UID: $uid", e)
                // Considerar emitir un estado de error a la UI si es necesario
            }
            .launchIn(scope) // Lanza la colección del Flow en el scope del repositorio
            .let { job -> { // Guarda la lambda para cancelar el Job
                Log.d("ProfileRepo", "Deteniendo listener de perfil para UID: $uid")
                job.cancel()
            }}
    }

    /* ─────────────── API Pública (Operaciones de Escritura) ─────────────── */

    /**
     * @copydoc ProfileRepository.update
     * @details Construye un [Map] conteniendo solo los campos `userName` y/o `photoUrl`
     * que no sean `null`. Llama a `remote.update()` para aplicar estos cambios en el
     * documento de Firestore usando `SetOptions.merge()`. La actualización en la caché
     * local (Room) se reflejará automáticamente a través del listener establecido por [reconnect].
     * La operación se ejecuta en el contexto del dispatcher IO.
     * @throws IllegalStateException Si no hay un usuario autenticado.
     * @throws FirebaseFirestoreException Si la operación de escritura en Firestore falla.
     */
    override suspend fun update(name: String?, photo: Uri?): Unit = withContext(io) {
        // Construye el mapa solo si hay algo que actualizar
        val patchData = buildMap<String, Any?> {
            name?.let { put("userName", it) } // Añade si name no es null
            photo?.let { put("photoUrl", it.toString()) } // Añade si photo no es null (como String)
        }

        if (patchData.isNotEmpty()) { // Solo actualiza si hay cambios
            try {
                Log.d("ProfileRepo", "Actualizando perfil en Firestore: $patchData")
                remote.update(patchData) // Llama al servicio para actualizar Firestore
            } catch (e: Exception) {
                Log.e("ProfileRepo", "Error al actualizar perfil en Firestore", e)
                throw e // Relanza la excepción para que la capa superior la maneje
            }
        } else {
            Log.d("ProfileRepo", "Llamada a update sin cambios para actualizar.")
        }
        // No es necesario devolver Unit explícitamente
    }

    /**
     * @copydoc ProfileRepository.clearLocal
     * @details Ejecuta `dao.clear()` en el contexto del dispatcher IO para eliminar
     * el registro del perfil de la base de datos local Room.
     */
    override suspend fun clearLocal(): Unit = withContext(io) {
        Log.d("ProfileRepo", "Limpiando caché local del perfil.")
        dao.clear()
    }
}
