package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * @brief Contrato para la gestión del perfil de usuario.
 *
 * Define métodos para observar, actualizar y limpiar el perfil.
 */
interface ProfileRepository {

    /**
     * @brief Flujo que emite el perfil actual del usuario autenticado.
     */
    val profile: Flow<UserProfile?>

    /**
     * @brief Actualiza de forma parcial el perfil.
     *
     * @param name       Nuevo nombre de usuario, o `null` para no modificar.
     * @param photo      URI de la nueva foto, o `null` para no modificar.
     * @param birthDate  Nueva fecha de nacimiento ("dd/MM/yyyy"), o `null` para no modificar.
     */
    suspend fun update(
        name: String? = null,
        photo: Uri? = null,
        birthDate: String? = null
    )

    /**
     * @brief Limpia la información de perfil de la caché local.
     */
    suspend fun clearLocal()
}
