/**
 * @file    NotifConfig.kt
 * @ingroup domain
 * @brief   Configuración de notificaciones asociada a un {@link Habit}.
 *
 * Contiene parámetros de programación (horas, días), contenido del mensaje,
 * modo de alerta y límites de vigencia.
 *
 * @property enabled     Activa o desactiva todas las notificaciones.
 * @property message     Texto mostrado en la notificación.
 * @property timesOfDay  Horas del día en formato «HH:mm».
 * @property weekDays    Días concretos de la semana (1‥7) cuando la frecuencia es personalizada.
 * @property advanceMin  Minutos de antelación respecto a la hora programada (0 = puntual).
 * @property startsAt    Fecha a partir de la cual comienzan los avisos (`null` = inmediata).
 * @property mode        Estilo del aviso (silencioso, sonido, vibración).
 * @property vibrate     `true` para habilitar vibración cuando el modo lo permite.
 * @property expiresAt   Fecha límite para dejar de mostrar notificaciones (`null` = indefinida).
 */
package com.app.tibibalance.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NotifConfig(
    val enabled    : Boolean      = false,
    val message    : String       = "",
    val timesOfDay : List<String> = emptyList(),
    val weekDays   : WeekDays     = WeekDays.NONE,
    val advanceMin : Int          = 0,
    val startsAt   : kotlinx.datetime.LocalDate? = null,
    val mode       : NotifMode    = NotifMode.SILENT,
    val vibrate    : Boolean      = true,
    val expiresAt  : kotlinx.datetime.LocalDate? = null,
)

/** Estilo de la notificación. */
enum class NotifMode { SILENT, SOUND, VIBRATE }
