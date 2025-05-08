/**
 * @file    HabitRepository.kt
 * @ingroup data_repository_interface // Grupo específico para interfaces de repositorios
 * @brief   Define el contrato para la gestión de los hábitos del usuario.
 *
 * @details Esta interfaz abstrae las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * y de observación para los hábitos ([Habit]), independientemente de si se almacenan
 * localmente (Room), remotamente (Firestore), o ambos (estrategia offline-first).
 * Las capas superiores (ViewModels, UseCases) deben interactuar con esta interfaz
 * para acceder y modificar los datos de los hábitos.
 */
package com.app.tibibalance.data.repository

import com.app.tibibalance.domain.model.Habit
import kotlinx.coroutines.flow.Flow

/**
 * @brief Interfaz que define las operaciones disponibles para gestionar los hábitos.
 * @see com.app.tibibalance.data.repository.FirebaseHabitRepository Implementación concreta que utiliza Room y Firestore.
 * @see com.app.tibibalance.domain.model.Habit Modelo de dominio que representa un hábito.
 */
interface HabitRepository {

    /**
     * @brief   Observa la lista completa de hábitos del usuario actual de forma reactiva.
     *
     * @details Devuelve un [Flow] que emite la lista actual de hábitos almacenados
     * (generalmente desde la caché local, como Room). El flujo se actualiza y
     * emite una nueva lista cada vez que se detecta un cambio en los datos
     * subyacentes, ya sea por una operación local o por sincronización desde el backend.
     * El orden de la lista emitida no está garantizado por este contrato.
     *
     * @return  Un [Flow] que emite `List<Habit>`. El flujo puede emitir una lista vacía
     * si el usuario no tiene hábitos.
     * @throws Exception Si ocurre un error al establecer la observación inicial (e.g., error de base de datos).
     * Los errores posteriores durante la emisión del flujo suelen manejarse con `.catch` en el colector.
     */
    fun observeHabits(): Flow<List<Habit>>

    /**
     * @brief   Inserta un nuevo hábito en la fuente de datos (local y/o remota).
     *
     * @details Persiste la instancia de [Habit] proporcionada. La implementación
     * se encarga de generar y asignar un identificador único si el `id` del [Habit]
     * pasado está vacío o no es válido para la fuente de datos.
     *
     * @param habit El objeto [Habit] a crear. El campo `id` puede ser ignorado por la implementación.
     * @return El [String] que representa el identificador único asignado al nuevo hábito
     * (e.g., el `docId` de Firestore o un UUID generado localmente).
     * @throws Exception Si ocurre un error durante la inserción en la base de datos local o remota
     * (e.g., error de red, violación de restricciones).
     */
    suspend fun addHabit(habit: Habit): String

    /**
     * @brief   Actualiza un hábito existente en la fuente de datos.
     *
     * @details Modifica el registro correspondiente al `id` del [Habit] proporcionado,
     * actualizando todos sus campos con los valores de la instancia `habit`.
     *
     * @param habit La instancia de [Habit] con los datos actualizados. El campo `id` debe
     * corresponder a un hábito existente.
     * @throws Exception Si el hábito con el `id` especificado no existe o si ocurre un error
     * durante la actualización local o remota.
     */
    suspend fun updateHabit(habit: Habit)

    /**
     * @brief   Elimina un hábito específico identificado por su ID.
     *
     * @details Borra el hábito tanto de la caché local como de la fuente remota (si aplica).
     *
     * @param id El identificador único ([Habit.id]) del hábito que se desea eliminar.
     * @throws Exception Si el hábito con el `id` especificado no existe o si ocurre un error
     * durante la eliminación local o remota.
     */
    suspend fun deleteHabit(id: String)

    /**
     * @brief   Marca o desmarca un hábito como completado para la fecha actual.
     *
     * @details Esta operación actualiza el estado de "completado hoy" del hábito.
     * La implementación específica determinará cómo se almacena este estado
     * (e.g., un campo booleano, una entrada en una lista de fechas completadas).
     *
     * @param id      El identificador único ([Habit.id]) del hábito a marcar/desmarcar.
     * @param checked `true` para marcar el hábito como completado hoy, `false` para desmarcarlo.
     * @throws Exception Si el hábito con el `id` especificado no existe o si ocurre un error
     * durante la actualización del estado.
     */
    suspend fun setCheckedToday(id: String, checked: Boolean)
}
