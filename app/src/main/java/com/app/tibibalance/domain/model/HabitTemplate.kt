/**
 * @file    HabitTemplate.kt
 * @ingroup domain
 * @brief   Modelo de dominio que representa una plantilla de hábito.
 *
 * Corresponde a los documentos de la colección **`habitTemplates`** en
 * Firestore y sirve como punto de partida para el asistente de creación
 * de hábitos.
 *
 * @property id            Identificador único del documento.
 * @property name          Nombre visible de la plantilla.
 * @property description   Descripción breve mostrada en la UI.
 * @property category      Categoría lógica (salud, productividad, etc.).
 * @property icon          Nombre del icono Material que la representa.
 *
 * @property sessionQty    Cantidad por sesión; `null` si no aplica.
 * @property sessionUnit   Unidad de la sesión.
 *
 * @property repeatPreset  Frecuencia sugerida.
 * @property weekDays      Días concretos (1‥7) si la frecuencia es personalizada.
 *
 * @property periodQty     Límite total del periodo; `null` si no aplica.
 * @property periodUnit    Unidad del periodo.
 *
 * @property notifCfg      Configuración avanzada de notificaciones.
 *
 * @property scheduled     `true` si la plantilla ya fue planificada por el scheduler.
 */
package com.app.tibibalance.domain.model

data class HabitTemplate(
    val id           : String        = "",
    val name         : String        = "",
    val description  : String        = "",
    val category     : HabitCategory = HabitCategory.SALUD,
    val icon         : String        = "FitnessCenter",

    val sessionQty   : Int?          = null,
    val sessionUnit  : SessionUnit   = SessionUnit.INDEFINIDO,

    val repeatPreset : RepeatPreset  = RepeatPreset.INDEFINIDO,
    val weekDays     : Set<Int>      = emptySet(),

    val periodQty    : Int?          = null,
    val periodUnit   : PeriodUnit    = PeriodUnit.INDEFINIDO,

    val notifCfg     : NotifConfig   = NotifConfig(),

    val scheduled    : Boolean       = false
)
