/**
 * @file    ServiceModule.kt
 * @ingroup di_module // Grupo específico para módulos Hilt/DI
 * @brief   Módulo Hilt abstracto para declarar el binding entre la interfaz [AuthService] y su implementación [FirebaseAuthService].
 *
 * @details Este módulo utiliza la anotación `@Binds` de Dagger/Hilt para indicar
 * que cuando un componente solicite una dependencia de tipo [AuthService], Hilt
 * debe proporcionar una instancia de [FirebaseAuthService].
 *
 * Al igual que [RepositoryModule], se declara como `abstract class` porque
 * los métodos anotados con `@Binds` deben ser abstractos.
 *
 * Está instalado en [SingletonComponent] y el binding está anotado con `@Singleton`,
 * asegurando que se cree una única instancia de [FirebaseAuthService] para toda la aplicación.
 *
 * @see Binds
 * @see Module
 * @see InstallIn
 * @see SingletonComponent
 * @see Singleton
 * @see AuthService
 * @see FirebaseAuthService
 */
package com.app.tibibalance.di

import com.app.tibibalance.data.remote.firebase.AuthService
import com.app.tibibalance.data.remote.firebase.FirebaseAuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @brief Módulo abstracto de Hilt que define los bindings para los servicios remotos de la aplicación.
 * @details Utiliza `@Binds` para informar a Hilt qué implementación concreta usar cuando se inyecta una interfaz de servicio.
 * Instalado en [SingletonComponent] para que los servicios vinculados como `@Singleton` tengan ciclo de vida de aplicación.
 */
@Module
@InstallIn(SingletonComponent::class) // Instala los bindings en el componente Singleton (nivel de aplicación)
abstract class ServiceModule {

    /**
     * @brief Vincula la interfaz [AuthService] a su implementación concreta [FirebaseAuthService] como un Singleton.
     * @details Indica a Hilt que, cuando se solicite una inyección de [AuthService], debe proveer
     * la instancia singleton de [FirebaseAuthService]. Hilt se encarga de construir
     * la instancia de `impl` con sus propias dependencias (como [FirebaseAuth] y el dispatcher IO,
     * provistos en otros módulos).
     *
     * @param impl La instancia de la implementación concreta ([FirebaseAuthService]) que Hilt proveerá.
     * @return La instancia singleton de la implementación casteada a la interfaz ([AuthService]).
     */
    @Binds // Define un binding de interfaz a implementación
    @Singleton // Asegura que la instancia de FirebaseAuthService sea única en la app
    abstract fun bindAuthService(
        impl: FirebaseAuthService // Hilt sabe cómo construir FirebaseAuthService
    ): AuthService // La interfaz que se solicita inyectar
}
