/* data/remote/ProfileDto.kt */
package com.app.tibibalance.data.remote

import com.app.tibibalance.domain.model.UserProfile

/**
 * @file    ProfileDto.kt
 * @ingroup data_remote
 * @brief   DTO usado para (des)serializar el documento **profiles/{uid}**.
 *
 * Representa únicamente los campos editables por el usuario
 * (nombre y URL de la foto).  El UID se mantiene fuera del DTO
 * porque proviene siempre del contexto de autenticación.
 */

/**
 * @brief  Data-class que modela el perfil de usuario en Firestore.
 *
 * @property userName Nombre visible del usuario (puede ser `null`).
 * @property photoUrl URL de la foto de perfil en Cloud Storage o CDN.
 */
data class ProfileDto(
    val userName : String? = null,
    val photoUrl : String? = null
) {
    /**
     * @brief   Convierte el DTO a modelo de dominio [UserProfile].
     *
     * @param uid Identificador único del usuario autenticado.
     * @return    Instancia de [UserProfile] lista para la capa de presentación.
     *
     * Si *userName* es `null`, se asigna el texto “Sin nombre” para evitar
     * mostrar un campo vacío en la UI.
     */
    fun toDomain(uid: String) = UserProfile(
        uid       = uid,
        userName  = userName ?: "Sin nombre",   // fallback visible
        photoUrl  = photoUrl
    )
}
