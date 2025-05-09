/**
 * @file    ProfileRepository.kt
 * @ingroup data_repository_interface // Grupo específico para interfaces de repositorios
 * @brief   Define el contrato de alto nivel para la gestión del perfil de usuario.
 *
 * @details Esta interfaz abstrae las operaciones necesarias para observar, actualizar
 * y limpiar los datos del perfil del usuario ([UserProfile]), independientemente
 * de las fuentes de datos subyacentes (e.g., Room local, Firestore remoto).
 * Las capas superiores (ViewModels, UseCases) deben interactuar con esta interfaz.
 *
 * Expone las siguientes funcionalidades principales:
 * - Observación reactiva del perfil de usuario actual a través de un [Flow].
 * - Actualización parcial del nombre y/o la foto de perfil.
 * - Limpieza de la caché local del perfil (útil al cerrar sesión).
 */
package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * @brief Interfaz que define las operaciones disponibles para gestionar el perfil del usuario.
 * @see com.app.tibibalance.data.repository.FirebaseProfileRepository Implementación concreta que utiliza Room y Firestore.
 * @see com.app.tibibalance.domain.model.UserProfile Modelo de dominio que representa el perfil.
 */
interface ProfileRepository {

    /**
     * @brief Flujo reactivo que emite el perfil ([UserProfile]) del usuario actualmente autenticado.
     * @details Emite el perfil más reciente disponible (generalmente desde la caché local)
     * y se actualiza automáticamente cuando los datos del perfil cambian.
     * El flujo puede emitir `null` inicialmente o si no hay perfil disponible.
     * La implementación ([FirebaseProfileRepository]) filtra los nulos antes de emitir.
     * @return Un [Flow] que emite la instancia de [UserProfile] actualizada.
     * @throws Exception Si ocurre un error al establecer la observación inicial.
     */
    val profile: Flow<UserProfile>

    /**
     * @brief Actualiza de forma parcial el perfil del usuario actual.
     * @details Permite modificar el nombre visible y/o la URL de la foto de perfil.
     * Si un parámetro es `null`, el campo correspondiente no se modifica.
     * La implementación se encargará de persistir los cambios en las fuentes de datos
     * correspondientes (e.g., Firestore y potencialmente la caché local).
     *
     * @param name El nuevo nombre de usuario a establecer, o `null` para no cambiar el nombre actual.
     * @param photo La [Uri] de la nueva foto de perfil a establecer (que la implementación
     * probablemente subirá a almacenamiento y convertirá a URL), o `null` para no cambiar la foto actual.
     * @throws IllegalStateException Si no hay un usuario autenticado al momento de la llamada.
     * @throws Exception Si ocurre un error durante la actualización en la(s) fuente(s) de datos
     * (e.g., error de red, error de escritura en Firestore, error al subir la foto).
     */
    suspend fun update(name: String?, photo: Uri?)

    /**
     * @brief Elimina la información del perfil de usuario almacenada localmente (caché).
     * @details Esta operación está destinada a limpiar la caché local (e.g., la tabla `profile` en Room)
     * y no necesariamente elimina el perfil del backend (Firestore). Se invoca típicamente
     * durante el proceso de cierre de sesión (`signOut`) para asegurar que los datos del
     * usuario anterior no persistan en el dispositivo.
     * @throws Exception Si ocurre un error al interactuar con la base de datos local.
     */
    suspend fun clearLocal()
}
