/**
 * @file    ObserveAuthStateUseCase.kt
 * @ingroup usecase
 * @brief   Caso de uso que expone el estado de autenticación como flujo reactivo.
 *
 * Inyecta el [AuthRepository] y delega en su propiedad `isLoggedIn` para que
 * la capa de presentación pueda observar los cambios de sesión.
 */
package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @constructor Recibe la dependencia [AuthRepository] mediante Hilt.
 */
class ObserveAuthStateUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    /**
     * @return Flujo que emite `true` cuando hay usuario autenticado.
     */
    operator fun invoke(): Flow<Boolean> = repo.isLoggedIn
}
