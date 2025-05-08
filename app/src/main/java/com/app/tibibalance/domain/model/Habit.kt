/**
 * @file    Habit.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Modelo de dominio principal que representa un hábito específico del usuario.
 *
 * @details Esta data class encapsula toda la información y configuración asociada a un hábito
 * que el usuario desea seguir o construir. Incluye detalles básicos (nombre, icono),
 * configuración de seguimiento (sesión, frecuencia, periodo), ajustes de notificación,
 * y metadatos como el estado de reto y las fechas de creación/próxima notificación.
 *
 * Es la entidad central utilizada en la lógica de negocio y la capa de presentación.
 * Está marcada como [Serializable] para permitir su (des)serialización, por ejemplo,
 * al persistirla en formato JSON dentro de [com.app.tibibalance.data.local.entity.HabitEntity]
 * o al enviarla/recibirla de Firestore (a través de [com.app.tibibalance.data.remote.mapper]).
 *
 * Los campos de tipo [Instant] utilizan un serializador personalizado ([InstantEpochMillisSerializer])
 * para asegurar la compatibilidad con formatos como JSON, donde se representan como
 * milisegundos desde la época (epoch-ms).
 *
 * @see Serializable
 * @see InstantEpochMillisSerializer
 * @see Session
 * @see RepeatPreset
 * @see Period
 * @see NotifConfig
 * @see HabitCategory
 * @see com.app.tibibalance.data.repository.HabitRepository Repositorio que gestiona esta entidad.
 */
package com.app.tibibalance.domain.model

import com.app.tibibalance.data.serialization.InstantEpochMillisSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * @brief Representa la entidad de negocio para un Hábito.
 * @details Contiene todos los atributos configurables y de estado de un hábito.
 * Proporciona valores por defecto para la mayoría de las propiedades para facilitar la creación.
 *
 * @property id Identificador único del hábito. Generalmente asignado por la fuente de datos
 * (e.g., docId de Firestore o UUID). Vacío por defecto hasta ser asignado.
 * @property name Nombre descriptivo y visible del hábito (e.g., "Meditar 10 minutos").
 * @property description Descripción adicional opcional sobre el hábito.
 * @property category La [HabitCategory] a la que pertenece el hábito (e.g., SALUD, MENTALIDAD).
 * @property icon Nombre del icono (e.g., de Material Icons) que representa visualmente el hábito.
 *
 * @property session Configuración de la [Session] asociada a cada vez que se realiza el hábito
 * (e.g., cantidad y unidad: 10 MINUTOS). Por defecto, sesión indefinida.
 * @property repeatPreset Preconfiguración de la frecuencia ([RepeatPreset]) con la que se debe
 * realizar el hábito (e.g., DIARIO, SEMANAL). Por defecto, INDEFINIDO.
 * @property period Configuración del [Period] total durante el cual el hábito está activo
 * (e.g., duración total: 30 DIAS). Por defecto, periodo indefinido.
 *
 * @property notifConfig Configuración detallada de las notificaciones ([NotifConfig]) para este hábito.
 * Por defecto, configuración vacía/deshabilitada.
 *
 * @property challenge Indica si el hábito está actualmente en un "modo reto" (Boolean). `false` por defecto.
 * @property challengeFrom [Instant] opcional que marca el inicio del periodo de reto. `null` por defecto.
 * Serializado usando [InstantEpochMillisSerializer].
 * @property challengeTo [Instant] opcional que marca el fin del periodo de reto. `null` por defecto.
 * Serializado usando [InstantEpochMillisSerializer].
 *
 * @property createdAt [Instant] que marca el momento en que se creó el registro del hábito.
 * Por defecto, se inicializa al momento actual (`Clock.System.now()`).
 * Serializado usando [InstantEpochMillisSerializer].
 * @property nextTrigger [Instant] opcional que indica la próxima vez que se debe disparar una
 * notificación o recordatorio para este hábito. `null` si no hay próxima notificación programada.
 * **Nota:** La serialización de este campo depende de cómo se gestione; si se persiste
 * directamente como Instant?, requeriría `@Serializable(with = InstantEpochMillisSerializer::class)`.
 * Si se calcula y no se persiste, o se persiste como Long, no necesita el serializer aquí.
 */
@Serializable
data class Habit(
    val id           : String = "", // Default vacío, asignado por el repositorio
    val name         : String,
    val description  : String,
    val category     : HabitCategory,
    val icon         : String,

    // Valores por defecto para objetos anidados
    val session      : Session      = Session(), // Usa constructor por defecto de Session
    val repeatPreset : RepeatPreset = RepeatPreset.INDEFINIDO, // Default a INDEFINIDO
    val period       : Period       = Period(), // Usa constructor por defecto de Period
    val notifConfig  : NotifConfig  = NotifConfig(), // Usa constructor por defecto de NotifConfig

    // Campos de reto con defaults
    val challenge    : Boolean      = false, // No es reto por defecto
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeFrom: Instant?     = null, // Null por defecto
    @Serializable(with = InstantEpochMillisSerializer::class)
    val challengeTo  : Instant?     = null, // Null por defecto

    // Timestamps con defaults y serializer
    @Serializable(with = InstantEpochMillisSerializer::class)
    val createdAt    : Instant = Clock.System.now(), // Default al momento de creación del objeto
    // Asumiendo que nextTrigger se calcula y actualiza externamente, o se maneja su serialización
    // de otra forma (e.g., como Long en el Map de Firestore). Si se serializara directamente
    // como Instant?, necesitaría el @Serializable(with=...)
    val nextTrigger  : Instant?     = null // Null por defecto
)
