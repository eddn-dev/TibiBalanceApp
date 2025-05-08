/**
 * @file    ProfileDto.kt
 * @ingroup data_remote_dto // Grupo específico para DTOs remotos
 * @brief   Data Transfer Object (DTO) para (des)serializar el documento de perfil en Firestore (`profiles/{uid}`).
 *
 * @details Esta data class representa la estructura de datos específica utilizada para leer y escribir
 * la información del perfil de usuario en Firebase Firestore. Se centra en los campos que
 * son gestionados directamente en el documento `profiles/{uid}`, como el nombre visible
 * y la URL de la foto.
 *
 * El identificador único del usuario (`uid`) no forma parte de este DTO porque se obtiene
 * del contexto de autenticación ([com.google.firebase.auth.FirebaseAuth]) y se utiliza como
 * ID del documento en Firestore, no como un campo dentro del documento mismo.
 *
 * Incluye una función de extensión `toDomain` para facilitar la conversión de este DTO
 * al modelo de dominio [UserProfile], que es el que utilizan las capas superiores de la aplicación.
 */
package com.app.tibibalance.data.remote

import com.app.tibibalance.domain.model.UserProfile

/**
 * @brief Data class que modela la estructura del documento de perfil de usuario en Firebase Firestore.
 * @see UserProfile Modelo de dominio correspondiente.
 * @see com.app.tibibalance.data.remote.firebase.ProfileService Servicio que utiliza este DTO para interactuar con Firestore.
 *
 * @property userName El nombre visible elegido por el usuario. Puede ser `null` si el usuario
 * no lo ha establecido o si el campo no existe en Firestore.
 * @property photoUrl La URL completa de la imagen de perfil del usuario, generalmente alojada
 * en Firebase Cloud Storage o un CDN. Puede ser `null` si el usuario no ha subido una foto.
 */
data class ProfileDto(
    val userName : String? = null,
    val photoUrl : String? = null
) {
    /**
     * @brief Convierte este [ProfileDto] (obtenido de Firestore) al modelo de dominio [UserProfile].
     *
     * @details Esta función es esencial para desacoplar la representación de datos remota
     * de la lógica de negocio y la UI. Toma el `uid` (que no está en el DTO) como
     * parámetro y lo combina con los campos `userName` y `photoUrl` del DTO.
     *
     * Proporciona un valor por defecto ("Sin nombre") para `userName` si este es `null` en el DTO,
     * asegurando que la UI siempre tenga un nombre para mostrar.
     *
     * @param uid El identificador único del usuario, obtenido desde [com.google.firebase.auth.FirebaseAuth].
     * @return Una instancia de [UserProfile] lista para ser utilizada en las capas de dominio y presentación.
     */
    fun toDomain(uid: String): UserProfile = UserProfile(
        uid       = uid, // Asigna el UID pasado como parámetro
        // Si userName es null en el DTO, usa "Sin nombre" como fallback para la UI
        userName  = this.userName ?: "Sin nombre",
        photoUrl  = this.photoUrl // Mantiene el photoUrl (puede ser null)
    )
}
