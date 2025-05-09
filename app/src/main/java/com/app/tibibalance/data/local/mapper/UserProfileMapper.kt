/**
 * @file    UserProfileMapper.kt
 * @ingroup data_local_mapper // Grupo específico para mappers locales
 * @brief   Funciones de extensión para mapeo bidireccional entre [UserProfileEntity] (Room) y [UserProfile] (dominio).
 *
 * @details Almacenar el perfil ([UserProfileEntity]) en la base de datos local (Room)
 * permite mostrar la información básica del usuario (nombre, foto) rápidamente
 * en la UI sin necesidad de una consulta remota a Firestore en cada inicio.
 *
 * Estas funciones de extensión encapsulan la lógica de conversión entre la
 * entidad de persistencia y el modelo de dominio, manteniendo el código de los
 * repositorios ([com.app.tibibalance.data.repository.FirebaseProfileRepository])
 * y ViewModels más limpio y enfocado.
 */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.domain.model.UserProfile

/* ───────────── Entity (Room) ➜ Dominio (UserProfile) ───────────── */

/**
 * @brief Convierte la entidad [UserProfileEntity] persistida en Room a su modelo de dominio [UserProfile].
 *
 * @receiver La instancia de [UserProfileEntity] obtenida desde el DAO [com.app.tibibalance.data.local.dao.ProfileDao].
 * @return   Un objeto [UserProfile] con los mismos datos (uid, userName, photoUrl), listo para ser usado
 * en las capas superiores (ViewModel, UI).
 */
fun UserProfileEntity.toDomain(): UserProfile =
    UserProfile(
        uid = this.uid,
        userName = this.userName,
        photoUrl = this.photoUrl
    )

/* ───────────── Dominio (UserProfile) ➜ Entity (Room) ───────────── */

/**
 * @brief Convierte el modelo de dominio [UserProfile] a su entidad [UserProfileEntity] para persistencia en Room.
 *
 * @receiver La instancia del modelo de dominio [UserProfile] que se desea guardar o actualizar en la caché local.
 * @return   Una instancia de [UserProfileEntity] con los datos correspondientes, lista para ser
 * pasada al método `upsert` del DAO [com.app.tibibalance.data.local.dao.ProfileDao].
 */
fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(
        uid = this.uid,
        userName = this.userName,
        photoUrl = this.photoUrl
    )
