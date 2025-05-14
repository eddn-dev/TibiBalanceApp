package com.app.tibibalance.domain.usecase

import com.app.tibibalance.domain.model.UserProfile
import com.app.tibibalance.data.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): UserProfile {
        return repository.profile.first() ?: throw Exception("Perfil no encontrado")
    }
}
