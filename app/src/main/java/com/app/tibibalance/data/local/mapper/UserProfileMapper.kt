/* data/local/mapper/UserProfileMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.domain.model.UserProfile

/**
 * @file    UserProfileMapper.kt
 * @ingroup data_local
 * @brief   Conversión entre [UserProfileEntity] (Room) y [UserProfile] (dominio).
 *
 * Almacenar el perfil en la base local permite mostrar la información del usuario
 * sin acudir a Firestore en cada arranque.
 * Estas extensiones encapsulan la conversión bidireccional para mantener limpio
 * el código de los repositorios y ViewModels.
 */

/* ───────────── Entity ➜ Dominio ───────────── */

/**
 * @brief Convierte la entidad persistida a su modelo de dominio.
 *
 * @receiver [UserProfileEntity] obtenido de Room.
 * @return   Objeto [UserProfile] listo para la capa de presentación.
 */
fun UserProfileEntity.toDomain(): UserProfile =
    UserProfile(uid, userName, photoUrl)

/* ───────────── Dominio ➜ Entity ───────────── */

/**
 * @brief Convierte el modelo de dominio a entidad para persistirlo.
 *
 * @receiver [UserProfile] gestionado por la lógica de negocio.
 * @return   [UserProfileEntity] compatible con Room.
 */
fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(uid, userName, photoUrl)
