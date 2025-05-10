package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.domain.model.UserProfile

/* ───────────── Entity (Room) ➜ Dominio (UserProfile) ───────────── */

/**
 * Convierte la entidad [UserProfileEntity] de Room a su modelo de dominio [UserProfile].
 * Los campos `email` y `birthDate` no están en la entidad local, se inicializan con null.
 */
fun UserProfileEntity.toDomain(): UserProfile =
    UserProfile(
        uid       = this.uid,
        userName  = this.userName,
        email     = null,
        birthDate = null,
        photoUrl  = this.photoUrl
    )

/* ───────────── Dominio (UserProfile) ➜ Entity (Room) ───────────── */

/**
 * Convierte el modelo de dominio [UserProfile] a su entidad [UserProfileEntity] para Room.
 * Se ignoran `email` y `birthDate`, que no se almacenan localmente.
 */
fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(
        uid      = this.uid,
        userName = this.userName,
        photoUrl = this.photoUrl
    )
