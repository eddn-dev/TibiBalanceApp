/**
 * @file    HabitFormMapper.kt
 * @ingroup data_mapper // Grupo para mappers generales (entre capas)
 * @brief   Función de extensión para convertir [HabitForm] + [NotifConfig] a [Habit].
 *
 * @details El asistente de creación de hábitos ([com.app.tibibalance.ui.wizard.AddHabitViewModel])
 * utiliza [HabitForm] para recopilar los datos básicos y de seguimiento, y
 * [NotifConfig] para los ajustes específicos de notificación. Esta función
 * de extensión actúa como un constructor final, fusionando ambos objetos
 * para crear la entidad de dominio [Habit] que será persistida por el
 * repositorio ([com.app.tibibalance.data.repository.HabitRepository]).
 *
 * Es importante notar que el campo `id` del [Habit] resultante se inicializa
 * como una cadena vacía (`""`). El ID real será asignado por el repositorio
 * durante la operación de inserción (e.g., `FirebaseHabitRepository.addHabit()`
 * obtendrá el `docId` de Firestore o generará un UUID si es necesario).
 * El campo `createdAt` se establece automáticamente al momento de la conversión
 * usando `Clock.System.now()`. El campo `nextTrigger` se deja `null` inicialmente,
 * ya que su cálculo y actualización son responsabilidad de la lógica de
 * programación de notificaciones.
 */
package com.app.tibibalance.data.mapper

import com.app.tibibalance.domain.model.* // Importa todos los modelos necesarios
import kotlinx.datetime.Clock

/**
 * @brief Fusiona los datos de un [HabitForm] y una [NotifConfig] para crear un objeto [Habit].
 *
 * @receiver La instancia de [HabitForm] que contiene los datos básicos y de seguimiento
 * recopilados del usuario a través del asistente de creación/edición.
 * @param cfg La instancia de [NotifConfig] que contiene los ajustes específicos
 * de notificación elegidos por el usuario.
 * @return Una instancia completa del modelo de dominio [Habit], lista para ser
 * persistida. El `id` estará vacío y `createdAt` se establecerá al momento actual.
 */
fun HabitForm.toHabit(cfg: NotifConfig): Habit =
    Habit(
        id           = "", // El ID real será asignado por el repositorio al guardar.
        name         = this.name,
        description  = this.desc,
        category     = this.category,
        icon         = this.icon,
        // Construye el objeto Session anidado
        session      = Session(qty = this.sessionQty, unit = this.sessionUnit),
        repeatPreset = this.repeatPreset,
        // Construye el objeto Period anidado
        period       = Period(qty = this.periodQty, unit = this.periodUnit),
        // Asigna la configuración de notificación completa
        notifConfig  = cfg,
        challenge    = this.challenge,
        // Establece la marca de tiempo de creación ahora mismo
        createdAt    = Clock.System.now()
        // nextTrigger, challengeFrom, challengeTo se dejan null por defecto
    )
