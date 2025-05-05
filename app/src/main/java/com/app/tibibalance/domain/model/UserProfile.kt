/* domain/model/UserProfile.kt */
package com.app.tibibalance.domain.model

data class UserProfile(
    val uid: String,
    val userName: String? = null,
    val photoUrl: String? = null
)
