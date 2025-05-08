/**
 * @file    UserProfile.kt
 * @ingroup domain
 * @brief   Información básica del perfil de usuario.
 *
 * @property uid       Identificador único del usuario autenticado.
 * @property userName  Nombre visible; `null` si el usuario no lo ha configurado.
 * @property photoUrl  URL de la foto de perfil; `null` cuando no existe.
 */
package com.app.tibibalance.domain.model

data class UserProfile(
    val uid      : String,
    val userName : String? = null,
    val photoUrl : String? = null
)
