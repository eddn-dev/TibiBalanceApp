package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.deleteAccount()
    }
}
