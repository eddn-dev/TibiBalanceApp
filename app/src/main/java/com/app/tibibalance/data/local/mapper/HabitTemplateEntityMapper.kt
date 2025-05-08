/* data/local/mapper/HabitTemplateEntityMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import com.app.tibibalance.domain.model.*

/**
 * @file    HabitTemplateEntityMapper.kt
 * @ingroup data_local
 * @brief   Funciones de mapeo entre [HabitTemplate] (dominio) y [HabitTemplateEntity] (Room).
 *
 * Estas extensiones encapsulan la conversión bidireccional:
 * - **Dominio → Entity**: serializa enumeraciones y objetos anidados a tipos primitivos
 *   aptos para la base de datos (p.ej. `HabitCategory` a `String`).
 * - **Entity → Dominio**: reconstruye el modelo de dominio completo, incluyendo la
 *   conversión de colecciones (`List<Int>` → `WeekDays`).
 */

/* ───────────── Dominio ➜ Entity ───────────── */

/**
 * @brief Convierte un [HabitTemplate] de dominio a [HabitTemplateEntity] para persistencia.
 *
 * - `category: HabitCategory` se mapea a su nombre (`String`).
 * - `NotifConfig.weekDays (Set<Int>)` se transforma a `List<Int>` para Room.
 *
 * @receiver Instancia de [HabitTemplate] obtenida de la lógica de negocio.
 * @return   Entidad [HabitTemplateEntity] lista para ser almacenada en la base local.
 */
fun HabitTemplate.toEntity() = HabitTemplateEntity(
    id              = id,
    name            = name,
    description     = description,
    category        = category.name,
    icon            = icon,

    sessionQty      = sessionQty,
    sessionUnit     = sessionUnit,

    repeatPreset    = repeatPreset,
    periodQty       = periodQty,
    periodUnit      = periodUnit,

    notifMode       = notifCfg.mode,
    notifMessage    = notifCfg.message,
    notifTimesOfDay = notifCfg.timesOfDay,
    /* ⬇⬇ convierte Set<Int> → List<Int> */
    notifDaysOfWeek = notifCfg.weekDays.days.toList(),
    notifAdvanceMin = notifCfg.advanceMin,
    notifVibrate    = notifCfg.vibrate,

    scheduled       = scheduled
)

/* ───────────── Entity ➜ Dominio ───────────── */

/**
 * @brief Convierte un [HabitTemplateEntity] recuperado de Room a su modelo de dominio [HabitTemplate].
 *
 * - `category` (String) se convierte en `HabitCategory` mediante `fromRaw`.
 * - `notifDaysOfWeek` (List<Int>) se transforma a `WeekDays`.
 *
 * @receiver Entidad [HabitTemplateEntity] obtenida de la base de datos.
 * @return   Modelo de dominio [HabitTemplate] listo para su uso en la capa de negocio.
 */
fun HabitTemplateEntity.toDomain() = HabitTemplate(
    id            = id,
    name          = name,
    description   = description,
    category      = HabitCategory.fromRaw(category),
    icon          = icon,

    sessionQty    = sessionQty,
    sessionUnit   = sessionUnit,

    repeatPreset  = repeatPreset,
    periodQty     = periodQty,
    periodUnit    = periodUnit,

    notifCfg      = NotifConfig(
        mode        = notifMode,
        message     = notifMessage,
        timesOfDay  = notifTimesOfDay,
        /* ⬇⬇ convierte List<Int> → Set<Int> → WeekDays */
        weekDays    = WeekDays(notifDaysOfWeek.toSet()),
        advanceMin  = notifAdvanceMin,
        vibrate     = notifVibrate
    ),

    scheduled     = scheduled
)
