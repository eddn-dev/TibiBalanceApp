/**
 * @file    Habit.kt
 * @ingroup domain
 * @brief   Modelo de dominio que representa un hábito del usuario.
 *
 * Contiene toda la configuración necesaria para el seguimiento — incluyendo
 * sesión, frecuencia, periodo, notificaciones y modo reto — y metadatos
 * del sistema.  Se serializa con Kotlinx Serialization; los campos
 * {@link kotlinx.datetime.Instant} se codifican con
 * {@link InstantEpochMillisSerializer} para compatibilidad JSON.
 */
package com.app.tibibalance.domain.model

import com.app.tibibalance.data.serialization.InstantEpochMillisSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * @brief Entidad de negocio **Habit**.
 *
 * @property id            Identificador único (docId Firestore o UUID local).
 * @property name          Nombre visible del hábito.
 * @property description   Descripción opcional.
 * @property category      Categoría lógica (bienestar, ejercicio, etc.).
 * @property icon          Nombre del icono Material que lo representa.
 *
 * @property session       Duración o cantidad por sesión (opcional).
 * @property repeatPreset  Preconfiguración de frecuencia sugerida.
 * @property period        Límite global del hábito (p.ej., 30 días).
 *
 * @property notifConfig   Ajustes de notificación derivados de la frecuencia.
 *
 * @property challenge     `true` si el hábito está bloqueado en modo reto.
 * @property challengeFrom Inicio del reto; `null` si no aplica.
 * @property challengeTo   Fin del reto; `null` si no aplica.
 *
 * @property createdAt     Marca temporal de creación (epoch-millis).
 * @property nextTrigger   Próxima notificación programada; `null` si no aplica.
 */
@Serializable
data class Habit(
    val id           : String = "",
    val name         : String,
    val description  : String,
    val category     : HabitCategory,
    val icon         : String,

    val session      : Session      = Session(),
    val repeatPreset : RepeatPreset = RepeatPreset.INDEFINIDO,
    val period       : Period       = Period(),

    val notifConfig  : NotifConfig  = NotifConfig(),

    val challenge    : Boolean      = false,
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeFrom: Instant?     = null,
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeTo  : Instant?     = null,

    @Serializable(with = InstantEpochMillisSerializer::class)
    val createdAt    : Instant = Clock.System.now(),
    val nextTrigger  : Instant?     = null
)
