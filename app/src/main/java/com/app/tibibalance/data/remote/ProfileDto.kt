package com.app.tibibalance.data.remote

import com.app.tibibalance.domain.model.UserProfile

/* data/remote/ProfileDto.kt */
data class ProfileDto(
    val userName : String? = null,
    val photoUrl : String? = null
) {
    fun toDomain(uid: String) = UserProfile(
        uid       = uid,
        userName  = userName ?: "Sin nombre",   // fallback visible
        photoUrl  = photoUrl
    )
}
