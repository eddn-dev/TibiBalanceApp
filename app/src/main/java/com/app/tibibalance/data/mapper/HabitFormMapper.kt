/* data/mapper/HabitFormMapper.kt */
package com.app.tibibalance.data.mapper

import com.app.tibibalance.domain.model.*
import kotlinx.datetime.Clock

/**
 * @file    HabitFormMapper.kt
 * @ingroup data_mappers
 * @brief   Conversión de [HabitForm] + [NotifConfig] → [Habit].
 *
 * El asistente de creación almacena los datos del borrador en un
 * [HabitForm] y la configuración de notificación en [NotifConfig].
 * Esta extensión los fusiona para producir la entidad de dominio
 * [Habit] lista para persistir.
 *
 * El **ID** se deja vacío (`""`) porque lo asigna el repositorio
 * (`FirebaseHabitRepository.addHabit()`), ya sea con un `UUID`
 * local o con el docId generado por Firestore.
 */

/**
 * @brief Fusiona el borrador del formulario con la configuración
 *        de notificación para construir el modelo final [Habit].
 *
 * @receiver [HabitForm] capturado por la UI.
 * @param   cfg Configuración de notificaciones elegida por el usuario.
 * @return  Instancia de [Habit] completa, con `createdAt` sellado al
 *          momento actual y `id` vacío a la espera de ser asignado.
 */
fun HabitForm.toHabit(cfg: NotifConfig): Habit =
    Habit(
        id           = "",                     // lo asignará addHabit()
        name         = name,
        description  = desc,
        category     = category,
        icon         = icon,
        session      = Session(sessionQty, sessionUnit),
        repeatPreset = repeatPreset,
        period       = Period(periodQty, periodUnit),
        notifConfig  = cfg,
        challenge    = challenge,
        createdAt    = Clock.System.now()
    )
