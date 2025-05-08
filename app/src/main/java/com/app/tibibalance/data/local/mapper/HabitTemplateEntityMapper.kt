/* data/local/mapper/HabitTemplateEntityMapper.kt */
package com.app.tibibalance.data.local.mapper

import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import com.app.tibibalance.domain.model.*

/**
 * @file    HabitTemplateEntityMapper.kt
 * @ingroup data_local_mapper // Grupo específico para mappers locales
 * @brief   Funciones de mapeo entre [HabitTemplate] (dominio) y [HabitTemplateEntity] (Room).
 *
 * @details Estas extensiones encapsulan la conversión bidireccional necesaria
 * para persistir y recuperar plantillas de hábitos en la base de datos local:
 * - **Dominio → Entity**: Convierte el objeto de dominio [HabitTemplate] a la entidad
 * [HabitTemplateEntity], adaptando tipos complejos como Enums ([HabitCategory] a `String`)
 * y colecciones ([NotifConfig.weekDays] `Set<Int>` a `List<Int>`) para ser
 * almacenados por Room (algunos Enums son manejados directamente por Room,
 * mientras que las listas requieren [TypeConverter]).
 * - **Entity → Dominio**: Reconstruye el modelo de dominio [HabitTemplate] completo
 * a partir de la entidad [HabitTemplateEntity] recuperada de Room, realizando
 * las conversiones inversas necesarias (e.g., `String` a [HabitCategory], `List<Int>` a [WeekDays]).
 */

/* ───────────── Dominio ➜ Entity (HabitTemplate -> HabitTemplateEntity) ───────────── */

/**
 * @brief Convierte un [HabitTemplate] de dominio a [HabitTemplateEntity] para persistencia en Room.
 *
 * @details Realiza las siguientes transformaciones clave:
 * - `category: HabitCategory` se mapea a su nombre (`String`) para almacenamiento.
 * - `notifCfg.weekDays: WeekDays` (que contiene un `Set<Int>`) se transforma a `List<Int>`
 * para ser manejada por el [com.app.tibibalance.data.local.mapper.NotifConverters].
 * - Los Enums como [SessionUnit], [RepeatPreset], [PeriodUnit], [NotifMode] son manejados
 * directamente por Room y no requieren conversión explícita aquí.
 * - La lista `notifTimesOfDay` también requiere un [com.app.tibibalance.data.local.mapper.NotifConverters].
 *
 * @receiver La instancia del modelo de dominio [HabitTemplate].
 * @return   La entidad [HabitTemplateEntity] correspondiente, lista para ser insertada o actualizada en Room.
 */
fun HabitTemplate.toEntity() = HabitTemplateEntity(
    id              = id,
    name            = name,
    description     = description,
    category        = category.name, // HabitCategory Enum -> String
    icon            = icon,

    sessionQty      = sessionQty,
    sessionUnit     = sessionUnit, // Room maneja Enums

    repeatPreset    = repeatPreset, // Room maneja Enums
    periodQty       = periodQty,
    periodUnit      = periodUnit, // Room maneja Enums

    notifMode       = notifCfg.mode, // Room maneja Enums
    notifMessage    = notifCfg.message,
    notifTimesOfDay = notifCfg.timesOfDay, // Convertidor necesario
    /* ⬇⬇ convierte WeekDays (Set<Int>) → List<Int> para el convertidor ⬇⬇ */
    notifDaysOfWeek = notifCfg.weekDays.days.toList(), // Convertidor necesario
    notifAdvanceMin = notifCfg.advanceMin,
    notifVibrate    = notifCfg.vibrate,

    scheduled       = scheduled
)

/* ───────────── Entity ➜ Dominio (HabitTemplateEntity -> HabitTemplate) ───────────── */

/**
 * @brief Convierte una entidad [HabitTemplateEntity] recuperada de Room a su modelo de dominio [HabitTemplate].
 *
 * @details Realiza las siguientes transformaciones clave:
 * - `category` (`String`) se convierte de nuevo al Enum [HabitCategory] usando `HabitCategory.fromRaw()`.
 * - `notifDaysOfWeek` (`List<Int>`, recuperada vía [com.app.tibibalance.data.local.mapper.NotifConverters])
 * se convierte a un `Set<Int>` y luego se encapsula en un objeto [WeekDays].
 * - Los campos Enum almacenados directamente por Room se asignan sin conversión.
 *
 * @receiver La entidad [HabitTemplateEntity] leída desde la base de datos Room.
 * @return   El modelo de dominio [HabitTemplate] reconstruido.
 */
fun HabitTemplateEntity.toDomain() = HabitTemplate(
    id            = id,
    name          = name,
    description   = description,
    category      = HabitCategory.fromRaw(category), // String -> HabitCategory Enum
    icon          = icon,

    sessionQty    = sessionQty,
    sessionUnit   = sessionUnit, // Directo

    repeatPreset  = repeatPreset, // Directo
    periodQty     = periodQty,
    periodUnit    = periodUnit, // Directo

    // Reconstruye el objeto NotifConfig
    notifCfg      = NotifConfig(
        enabled     = true, // Asume habilitado si está en la entidad, ajustar si hay campo 'enabled'
        mode        = notifMode, // Directo
        message     = notifMessage,
        timesOfDay  = notifTimesOfDay, // Directo (TypeConverter hizo su trabajo)
        /* ⬇⬇ convierte List<Int> (de Room) → Set<Int> → WeekDays ⬇⬇ */
        weekDays    = WeekDays(notifDaysOfWeek.toSet()), // Convertidor hizo su trabajo
        advanceMin  = notifAdvanceMin,
        vibrate     = notifVibrate
        // startsAt y expiresAt no están en HabitTemplateEntity, se omiten o se ponen a null
    ),

    scheduled     = scheduled
)
