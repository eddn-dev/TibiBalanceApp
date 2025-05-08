/**
 * @file    RepositoryModule.kt
 * @ingroup di_module // Grupo específico para módulos Hilt/DI
 * @brief   Módulo Hilt abstracto para declarar los bindings entre las interfaces de Repositorio y sus implementaciones concretas.
 *
 * @details Este módulo utiliza la anotación `@Binds` de Dagger/Hilt para indicar
 * cómo satisfacer las solicitudes de dependencias para las interfaces [AuthRepository]
 * y [HabitRepository]. Al usar `@Binds`, Hilt sabe que cuando un componente
 * solicite una `AuthRepository`, debe proporcionar una instancia de `FirebaseAuthRepository`,
 * y lo mismo para `HabitRepository` con `FirebaseHabitRepository`.
 *
 * El módulo se declara como `abstract class` porque los métodos anotados con `@Binds`
 * deben ser abstractos (no tienen cuerpo, solo definen el enlace).
 *
 * Está instalado en [SingletonComponent], lo que significa que las dependencias
 * vinculadas aquí (especialmente las marcadas con `@Singleton`) tendrán un ciclo de
 * vida asociado al de la aplicación.
 *
 * @see Binds
 * @see Module
 * @see InstallIn
 * @see SingletonComponent
 * @see AuthRepository
 * @see FirebaseAuthRepository
 * @see HabitRepository
 * @see FirebaseHabitRepository
 */
package com.app.tibibalance.di

import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.FirebaseAuthRepository
import com.app.tibibalance.data.repository.FirebaseHabitRepository
import com.app.tibibalance.data.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @brief Módulo abstracto de Hilt que define los bindings para los repositorios de la aplicación.
 * @details Utiliza `@Binds` para informar a Hilt qué implementación concreta usar cuando se inyecta una interfaz de repositorio.
 */
@Module
@InstallIn(SingletonComponent::class) // Instala los bindings en el componente Singleton (nivel de aplicación)
abstract class RepositoryModule {

    /**
     * @brief Vincula la interfaz [AuthRepository] a su implementación concreta [FirebaseAuthRepository].
     * @details Cuando Hilt necesite proveer una instancia de [AuthRepository], utilizará este binding
     * para saber que debe instanciar (o reutilizar, si es singleton) [FirebaseAuthRepository].
     * Hilt se encarga de construir `impl` con sus propias dependencias.
     * El scope de este binding será el predeterminado (unscoped) a menos que `FirebaseAuthRepository`
     * esté anotado con un scope como `@Singleton`.
     *
     * @param impl La instancia de la implementación concreta ([FirebaseAuthRepository]) que Hilt proveerá.
     * @return La instancia de la implementación casteada a la interfaz ([AuthRepository]).
     */
    @Binds // Indica a Hilt que este método abstracto define un binding de implementación a interfaz
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository // Hilt sabe cómo construir FirebaseAuthRepository
    ): AuthRepository // La interfaz que se solicita

    /**
     * @brief Vincula la interfaz [HabitRepository] a su implementación concreta [FirebaseHabitRepository] como un Singleton.
     * @details Similar a `bindAuthRepository`, pero la anotación `@Singleton` aquí asegura que Hilt
     * cree una única instancia de [FirebaseHabitRepository] y la reutilice para todas las
     * inyecciones de [HabitRepository] dentro del [SingletonComponent].
     *
     * @param impl La instancia de la implementación concreta ([FirebaseHabitRepository]) que Hilt proveerá.
     * @return La instancia singleton de la implementación casteada a la interfaz ([HabitRepository]).
     */
    @Binds
    @Singleton // Especifica que este binding debe ser tratado como Singleton
    abstract fun bindHabitRepository(
        impl: FirebaseHabitRepository // Hilt sabe cómo construir FirebaseHabitRepository
    ): HabitRepository // La interfaz que se solicita
}
