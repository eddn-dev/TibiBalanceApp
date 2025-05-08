/**
 * @file    ObserveAuthStateUseCase.kt
 * @ingroup domain_usecase // Grupo específico para casos de uso del dominio
 * @brief   Caso de uso dedicado a exponer el estado de autenticación actual de forma reactiva.
 *
 * @details Este caso de uso encapsula la lógica para obtener un [Flow] que emite
 * el estado de autenticación (`true` si logueado, `false` si no). Delega la
 * obtención de este flujo al [AuthRepository].
 *
 * Sigue el patrón de "Caso de Uso como Clase Invocable", donde la lógica principal
 * se ejecuta al invocar la instancia de la clase como si fuera una función, gracias
 * al operador `invoke`. Esto promueve una arquitectura limpia y testeable.
 *
 * @see AuthRepository Repositorio del cual se obtiene el estado de autenticación.
 * @see Flow Flujo reactivo de Kotlin Coroutines.
 */
package com.app.tibibalance.domain.usecase

import com.app.tibibalance.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @brief Caso de uso que proporciona un [Flow] para observar el estado de autenticación.
 * @details Simplifica el acceso al flujo `isLoggedIn` del [AuthRepository] para los
 * ViewModels u otros componentes de la capa de presentación. La inyección de
 * dependencias (`@Inject`) permite que Hilt proporcione la instancia del repositorio.
 *
 * @constructor Inyecta la dependencia [AuthRepository] a través de Hilt.
 * @param repo Instancia del repositorio de autenticación.
 */
class ObserveAuthStateUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    /**
     * @brief Permite invocar la instancia de la clase como una función.
     * @details Sobrecarga el operador `invoke` para que al llamar a `observeAuthStateUseCase()`
     * se devuelva directamente el flujo `isLoggedIn` del repositorio.
     *
     * @return Un [Flow] que emite `true` si el usuario está actualmente autenticado,
     * y `false` en caso contrario. El flujo se actualiza automáticamente cuando
     * el estado de autenticación cambia.
     */
    operator fun invoke(): Flow<Boolean> = repo.isLoggedIn
}
