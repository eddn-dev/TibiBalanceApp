/**
 * @file    RepositoryModule.kt
 * @ingroup di_module
 * @brief   Módulo Hilt abstracto para declarar los bindings entre las interfaces de Repositorio
 *          y sus implementaciones concretas.
 *
 * @details Este módulo utiliza la anotación `@Binds` de Dagger/Hilt para indicar
 *          cómo satisfacer las solicitudes de dependencias para las interfaces
 *          [AuthRepository], [HabitRepository] y [EmotionalRepository].
 *          Al usar `@Binds`, Hilt sabe que cuando un componente solicite una de estas
 *          interfaces, debe proporcionar la instancia concreta correspondiente:
 *          - `AuthRepository` → [FirebaseAuthRepository]
 *          - `HabitRepository` → [FirebaseHabitRepository]
 *          - `EmotionalRepository` → [FirebaseEmotionRepository]
 *
 *          Está instalado en [SingletonComponent], lo que significa que las dependencias
 *          vinculadas aquí (marcadas con `@Singleton`) tendrán un ciclo de
 *          vida asociado al de la aplicación.
 *
 * @see Binds
 * @see Module
 * @see InstallIn
 * @see SingletonComponent
 */
package com.app.tibibalance.di

import com.app.tibibalance.data.repository.AuthRepository
import com.app.tibibalance.data.repository.EmotionalRepository
import com.app.tibibalance.data.repository.FirebaseAuthRepository
import com.app.tibibalance.data.repository.FirebaseEmotionRepository
import com.app.tibibalance.data.repository.FirebaseHabitRepository
import com.app.tibibalance.data.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @brief Módulo abstracto de Hilt que define los bindings para los repositorios de la aplicación.
 * @details Utiliza `@Binds` para informar a Hilt qué implementación concreta usar cuando se inyecta
 *          una interfaz de repositorio. Cada método abstracto anulado con `@Binds` conecta
 *          la interfaz solicitada con su implementación, y el alcance (`@Singleton`) garantiza
 *          la reutilización de la misma instancia durante todo el ciclo de vida de la aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * @brief Vincula la interfaz [AuthRepository] a su implementación concreta [FirebaseAuthRepository].
     * @details Cuando Hilt necesite proveer una instancia de [AuthRepository], utilizará este binding
     *          para saber que debe instanciar (o reutilizar, si es singleton) [FirebaseAuthRepository].
     *
     * @param impl La instancia de la implementación concreta ([FirebaseAuthRepository]) que Hilt proveerá.
     * @return La instancia de la implementación casteada a la interfaz ([AuthRepository]).
     */
    @Binds
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository
    ): AuthRepository

    /**
     * @brief Vincula la interfaz [HabitRepository] a su implementación concreta [FirebaseHabitRepository].
     * @details Cuando Hilt necesite proveer una instancia de [HabitRepository], utilizará este binding
     *          para saber que debe instanciar (o reutilizar, si es singleton) [FirebaseHabitRepository].
     *          La anotación `@Singleton` asegura que solo exista una instancia durante todo
     *          el ciclo de vida de la aplicación.
     *
     * @param impl La instancia de la implementación concreta ([FirebaseHabitRepository]) que Hilt proveerá.
     * @return La instancia singleton de la implementación casteada a la interfaz ([HabitRepository]).
     */
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: FirebaseHabitRepository
    ): HabitRepository

    /**
     * @brief Vincula la interfaz [EmotionalRepository] a su implementación concreta [FirebaseEmotionRepository].
     * @details Cuando Hilt necesite proveer una instancia de [EmotionalRepository], utilizará este binding
     *          para saber que debe instanciar (o reutilizar, si es singleton) [FirebaseEmotionRepository].
     *          La anotación `@Singleton` asegura que solo exista una instancia durante todo
     *          el ciclo de vida de la aplicación.
     *
     * @param impl La instancia de la implementación concreta ([FirebaseEmotionRepository]) que Hilt proveerá.
     * @return La instancia singleton de la implementación casteada a la interfaz ([EmotionalRepository]).
     */
    @Binds
    @Singleton
    abstract fun bindEmotionalRepository(
        impl: FirebaseEmotionRepository
    ): EmotionalRepository
}
