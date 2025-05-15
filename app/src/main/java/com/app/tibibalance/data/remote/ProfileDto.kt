package com.app.tibibalance.data.remote

import com.app.tibibalance.domain.model.UserProfile

/**
 * @file    ProfileDto.kt
 * @ingroup data_remote_dto // Grupo específico para DTOs remotos
 * @brief   Data Transfer Object (DTO) para (des)serializar el documento de perfil en Firestore.
 */

data class ProfileDto(
    val userName : String? = null,
    val photoUrl : String? = null,
    val birthDate: String? = null    // Fecha en formato ISO (yyyy-MM-dd)
) {
    /**
     * Convierte este DTO al modelo de dominio [UserProfile].
     *
     * @param uid El identificador único del usuario (ID del documento).
     */
    fun toDomain(uid: String): UserProfile = UserProfile(
        uid        = uid,
        userName   = this.userName ?: "Sin nombre",
        email      = null,            // Este DTO no incluye email
        birthDate  = this.birthDate,  // Fecha de nacimiento en ISO, se formatea luego en el repo
        photoUrl   = this.photoUrl
    )
}
