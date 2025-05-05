package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.UserProfileEntity
import com.app.tibibalance.domain.model.UserProfile

fun UserProfileEntity.toDomain() = UserProfile(uid, userName, photoUrl)
fun UserProfile.toEntity()       = UserProfileEntity(uid, userName, photoUrl)
