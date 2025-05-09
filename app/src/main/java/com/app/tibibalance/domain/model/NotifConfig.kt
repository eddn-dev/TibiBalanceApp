/**
 * @file    NotifConfig.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define la configuración detallada para las notificaciones de un hábito ([Habit]).
 *
 * @details Esta data class encapsula todos los parámetros necesarios para programar
 * y mostrar notificaciones o recordatorios asociados a un hábito. Incluye si las
 * notificaciones están habilitadas, el mensaje a mostrar, los horarios y días
 * específicos, el modo de alerta (sonido, vibración, silencio), y fechas opcionales
 * de inicio y fin para la vigencia de las notificaciones.
 *
 * Es parte del modelo de dominio [Habit] y también se utiliza en [HabitTemplate]
 * para sugerir una configuración inicial.
 * Está marcada como [Serializable] para permitir su persistencia (e.g., dentro del JSON de Habit).
 *
 * @see Habit Modelo principal que contiene esta configuración.
 * @see HabitTemplate Plantilla que puede contener una configuración sugerida.
 * @see NotifMode Enumeración para el estilo de la notificación.
 * @see WeekDays Clase para representar los días de la semana seleccionados.
 * @see kotlinx.datetime.LocalDate Tipo utilizado para las fechas de inicio/fin.
 * @see Serializable
 */
package com.app.tibibalance.domain.model

import kotlinx.datetime.LocalDate // Import necesario para startsAt y expiresAt
import kotlinx.serialization.Serializable

/**
 * @brief Data class que representa la configuración de notificación para un [Habit].
 * @details Contiene todos los ajustes necesarios para determinar cuándo, cómo y qué
 * notificar al usuario sobre un hábito. Proporciona valores por defecto sensatos
 * (notificaciones desactivadas, modo silencioso, etc.).
 *
 * @property enabled Flag booleano que indica si las notificaciones están activadas (`true`)
 * o desactivadas (`false`) globalmente para este hábito. Por defecto `false`.
 * @property message El texto [String] que se mostrará como cuerpo de la notificación. Por defecto `""`.
 * @property timesOfDay Una [List] de [String] que representa las horas del día en formato `"HH:mm"`
 * en las que se debe programar la notificación (e.g., `["08:00", "21:30"]`).
 * Por defecto, lista vacía.
 * @property weekDays Instancia de [WeekDays] que encapsula el conjunto de días de la semana
 * (1=Lunes...7=Domingo) en los que la notificación debe activarse, relevante especialmente
 * para frecuencias personalizadas. Por defecto [WeekDays.NONE].
 * @property advanceMin Entero que representa los minutos de antelación con los que se debe
 * mostrar la notificación respecto a la hora programada en `timesOfDay`. Un valor de `0`
 * indica que se muestra puntualmente a la hora. Por defecto `0`.
 * @property startsAt [LocalDate] opcional que indica la fecha a partir de la cual las notificaciones
 * deben empezar a mostrarse. Si es `null`, se asume que empiezan inmediatamente (o desde
 * la creación del hábito). Por defecto `null`.
 * @property mode El modo de alerta [NotifMode] para la notificación (e.g., SILENT, SOUND, VIBRATE).
 * Por defecto [NotifMode.SILENT].
 * @property vibrate Flag booleano que indica si la vibración debe activarse (`true`) junto con
 * la notificación, siempre que el [mode] no sea [NotifMode.SILENT]. Por defecto `true`.
 * @property expiresAt [LocalDate] opcional que indica la fecha límite (inclusiva) hasta la cual
 * las notificaciones deben mostrarse. Si es `null`, las notificaciones no tienen fecha de
 * expiración definida. Por defecto `null`.
 */
@Serializable // Permite serializar/deserializar esta clase (e.g., a JSON)
data class NotifConfig(
    val enabled    : Boolean      = false, // Notificaciones desactivadas por defecto
    val message    : String       = "", // Mensaje vacío por defecto
    val timesOfDay : List<String> = emptyList(), // Sin horas específicas por defecto
    val weekDays   : WeekDays     = WeekDays.NONE, // Ningún día específico por defecto
    val advanceMin : Int          = 0, // Puntual por defecto (0 min antelación)
    val startsAt   : LocalDate?   = null, // Sin fecha de inicio específica por defecto
    val mode       : NotifMode    = NotifMode.SILENT, // Modo silencioso por defecto
    val vibrate    : Boolean      = true, // Vibración activada por defecto (si el modo lo permite)
    val expiresAt  : LocalDate?   = null // Sin fecha de expiración por defecto
)

/**
 * @brief Enumeración que define los posibles modos de alerta para una notificación.
 * @details Determina cómo se presenta la notificación al usuario en términos de sonido y vibración.
 */
enum class NotifMode {
    /** @brief La notificación se muestra de forma silenciosa, sin sonido ni vibración. */
    SILENT,
    /** @brief La notificación se muestra con el sonido predeterminado del sistema (y potencialmente vibración si `NotifConfig.vibrate` es `true`). */
    SOUND,
    /** @brief La notificación se muestra principalmente mediante vibración (y potencialmente un sonido mínimo o ninguno, dependiendo del sistema). */
    VIBRATE
}
