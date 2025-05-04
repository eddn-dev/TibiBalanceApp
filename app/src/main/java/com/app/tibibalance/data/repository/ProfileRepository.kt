package com.app.tibibalance.data.repository

import android.net.Uri
import com.app.tibibalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/* en data/repository/ProfileRepository.kt */
interface ProfileRepository {
    val profile: Flow<UserProfile>
    suspend fun update(name: String?, photo: Uri?)
    suspend fun clearLocal()        // 👈 NUEVO
}

