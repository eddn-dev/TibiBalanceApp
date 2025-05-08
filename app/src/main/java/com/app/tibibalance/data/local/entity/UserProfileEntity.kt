/* data/local/entity/UserProfileEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @file    UserProfileEntity.kt
 * @ingroup data_local
 * @brief   Entidad Room para almacenar el perfil de usuario.
 *
 * Esta tabla mantiene un **único** registro correspondiente al documento
 * `profiles/{uid}` en Firestore.  Sirve para cachear la información básica
 * del usuario y mostrarla sin necesidad de realizar lecturas remotas.
 */

/**
 * @brief Registro de la tabla **profile**.
 *
 * @property uid       Identificador único del usuario (clave primaria).
 * @property userName  Nombre visible; `null` si el usuario no lo ha configurado.
 * @property photoUrl  URL de la foto de perfil; `null` si no se ha establecido.
 */
@Entity(tableName = "profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String, /**< UID del usuario. */
    val userName: String?,       /**< Nombre para mostrar. */
    val photoUrl: String?        /**< URL de la foto de perfil. */
)
