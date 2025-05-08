/**
 * @file    UserProfileEntity.kt
 * @ingroup data_local
 * @brief   Entidad Room para almacenar el perfil de usuario localmente.
 *
 * @details Esta tabla (`profile`) actúa como caché local para la información básica
 * del usuario obtenida del documento `profiles/{uid}` en Firestore. Almacenar
 * estos datos localmente permite mostrarlos rápidamente en la UI sin necesidad
 * de lecturas remotas constantes. La tabla está diseñada para contener siempre
 * un **único** registro, correspondiente al usuario actualmente autenticado.
 */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @brief Representa un registro único en la tabla **profile**.
 * @see com.app.tibibalance.domain.model.UserProfile Modelo de dominio correspondiente.
 *
 * @property uid Clave primaria de la tabla. Es el identificador único proporcionado por Firebase Authentication para el usuario.
 * @property userName Nombre visible del usuario. Puede ser `null` si el usuario no lo ha configurado aún en su perfil (e.g., durante el registro inicial).
 * @property photoUrl URL de la imagen de perfil del usuario. Puede ser `null` si el usuario no ha subido una foto.
 */
@Entity(tableName = "profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String, /**< Identificador único del usuario (PK), coincide con Firebase Auth UID. */
    val userName: String?,       /**< Nombre de usuario para mostrar en la UI (nullable). */
    val photoUrl: String?        /**< URL de la foto de perfil (nullable). */
)
