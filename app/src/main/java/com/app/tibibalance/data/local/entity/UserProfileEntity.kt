package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val userName: String?,
    val photoUrl: String?
)
