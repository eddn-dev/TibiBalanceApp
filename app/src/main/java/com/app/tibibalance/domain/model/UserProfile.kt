/* domain/model/UserProfile.kt */
package com.app.tibibalance.domain.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class UserProfile(
    @PrimaryKey val uid: String,
    val userName: String? = null,
    val photoUrl: String? = null
)
