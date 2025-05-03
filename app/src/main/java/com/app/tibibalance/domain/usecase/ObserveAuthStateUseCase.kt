// domain/usecase/ObserveAuthStateUseCase.kt
package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repo.isLoggedIn
}
