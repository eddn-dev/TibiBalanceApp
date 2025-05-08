/**
 * @file    UserProfile.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Modelo de dominio que representa la información básica del perfil de un usuario autenticado.
 *
 * @details Esta data class contiene los datos esenciales del perfil que se utilizan
 * comúnmente en la aplicación, como el identificador único, el nombre visible
 * y la URL de la foto de perfil. Es el modelo utilizado por las capas superiores
 * (UI, ViewModel) para representar al usuario.
 *
 * Se obtiene típicamente a través del [com.app.tibibalance.data.repository.ProfileRepository],
 * que a su vez lo obtiene de la caché local (Room) o de la fuente remota (Firestore),
 * mapeando desde [com.app.tibibalance.data.local.entity.UserProfileEntity] o
 * [com.app.tibibalance.data.remote.ProfileDto].
 *
 * @see com.app.tibibalance.data.repository.ProfileRepository Repositorio que provee este modelo.
 * @see com.app.tibibalance.data.local.entity.UserProfileEntity Entidad Room correspondiente.
 * @see com.app.tibibalance.data.remote.ProfileDto DTO de Firestore correspondiente.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Representa el perfil básico de un usuario dentro de la aplicación.
 * @details Contiene la información mínima necesaria para identificar y mostrar
 * información relevante del usuario en la interfaz.
 *
 * @property uid El identificador único (UID) del usuario, proporcionado por el sistema
 * de autenticación (e.g., Firebase Authentication). Este campo es obligatorio y no nulo.
 * @property userName El nombre visible del usuario, tal como lo ha configurado.
 * Puede ser `null` si el usuario no ha establecido un nombre o si aún no se ha cargado.
 * Por defecto es `null`.
 * @property photoUrl La URL [String] completa de la imagen de perfil del usuario.
 * Puede ser `null` si el usuario no tiene foto de perfil o si aún no se ha cargado.
 * Por defecto es `null`.
 */
data class UserProfile(
    val uid      : String,         // Identificador único, no nulo.
    val userName : String? = null, // Nombre visible, puede ser null. Default null.
    val photoUrl : String? = null  // URL de la foto, puede ser null. Default null.
)
