/* data/repository/ProfileRepository.kt */
package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * @file    ProfileRepository.kt
 * @ingroup data_repository
 * @brief   Contrato para la gestión del perfil de usuario.
 *
 * Abstrae la fuente de datos (Room + Firestore) y expone:
 * - Flujo reactivo de [UserProfile] cacheado.
 * - Actualización parcial de nombre y/o foto.
 * - Limpieza de la caché local (p.ej. al cerrar sesión).
 */
interface ProfileRepository {

    /** @brief Flujo que emite el perfil de usuario actualizado. */
    val profile: Flow<UserProfile>

    /**
     * @brief   Actualiza los campos del perfil.
     *
     * @param name  Nuevo nombre visible (`null` para mantener).
     * @param photo URI de la nueva foto (`null` para mantener).
     */
    suspend fun update(name: String?, photo: Uri?)

    /**
     * @brief Elimina la información de perfil almacenada localmente.
     *
     * Se invoca normalmente durante el flujo de *sign-out* o para
     * forzar una recarga completa desde el backend.
     */
    suspend fun clearLocal()
}
