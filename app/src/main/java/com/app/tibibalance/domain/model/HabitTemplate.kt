/**
 * @file    HabitTemplate.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Modelo de dominio que representa una plantilla predefinida para crear hábitos.
 *
 * @details Esta data class define la estructura de una plantilla de hábito. Las plantillas
 * se obtienen típicamente de una fuente remota (como la colección `habitTemplates`
 * en Firestore) y se utilizan en la UI para ofrecer al usuario puntos de partida
 * rápidos al crear un nuevo hábito personalizado ([Habit]).
 *
 * Contiene valores preconfigurados para la mayoría de las propiedades de un hábito,
 * incluyendo nombre, descripción, categoría, icono, configuración de sesión,
 * frecuencia, periodo y notificaciones.
 *
 * @see Habit Modelo de dominio del hábito final creado por el usuario.
 * @see HabitForm Modelo intermedio usado en la UI para crear/editar, puede ser prellenado desde esta plantilla.
 * @see com.app.tibibalance.data.repository.HabitTemplateRepository Repositorio que gestiona la obtención de estas plantillas.
 * @see com.app.tibibalance.data.remote.mapper.FirestoreHabitTemplateMapper Mapper que convierte documentos de Firestore a este modelo.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Representa una plantilla predefinida para un [Habit].
 * @details Contiene valores sugeridos para facilitar la creación de nuevos hábitos por parte del usuario.
 * Proporciona valores por defecto para todas sus propiedades.
 *
 * @property id Identificador único de la plantilla (generalmente el `docId` de Firestore). Vacío por defecto.
 * @property name Nombre sugerido para el hábito (e.g., "Leer 30 minutos"). Vacío por defecto.
 * @property description Descripción breve que explica el propósito de la plantilla. Vacío por defecto.
 * @property category La [HabitCategory] preseleccionada para esta plantilla. Por defecto [HabitCategory.SALUD].
 * @property icon El nombre del icono Material sugerido para esta plantilla. Por defecto "FitnessCenter".
 *
 * @property sessionQty La cantidad numérica sugerida para la [Session] (e.g., 30 para "30 minutos"). `null` por defecto.
 * @property sessionUnit La [SessionUnit] sugerida para la sesión. Por defecto [SessionUnit.INDEFINIDO].
 *
 * @property repeatPreset La frecuencia predefinida ([RepeatPreset]) sugerida. Por defecto [RepeatPreset.INDEFINIDO].
 * @property weekDays **Nota:** Los días específicos de la semana para frecuencias personalizadas
 * se encuentran dentro de `notifCfg.weekDays`. Este campo no existe directamente aquí.
 *
 * @property periodQty La cantidad numérica sugerida para el [Period] total del hábito. `null` por defecto.
 * @property periodUnit La [PeriodUnit] sugerida para el periodo total. Por defecto [PeriodUnit.INDEFINIDO].
 *
 * @property notifCfg La configuración de notificación ([NotifConfig]) sugerida para esta plantilla.
 * Incluye modo, mensaje, horas, días de la semana ([WeekDays]), antelación y vibración.
 * Utiliza el constructor por defecto de [NotifConfig].
 *
 * @property scheduled Un flag booleano interno, posiblemente para indicar si esta plantilla
 * requiere alguna acción de programación especial al ser seleccionada. `false` por defecto.
 */
data class HabitTemplate(
    val id           : String        = "", // ID (e.g., Firestore docId)
    val name         : String        = "", // Nombre de la plantilla
    val description  : String        = "", // Descripción
    val category     : HabitCategory = HabitCategory.SALUD, // Categoría por defecto
    val icon         : String        = "FitnessCenter", // Icono por defecto

    // Configuración de sesión sugerida
    val sessionQty   : Int?          = null, // Cantidad (nullable)
    val sessionUnit  : SessionUnit   = SessionUnit.INDEFINIDO, // Unidad por defecto

    // Configuración de repetición sugerida
    val repeatPreset : RepeatPreset  = RepeatPreset.INDEFINIDO, // Frecuencia por defecto
    // val weekDays     : Set<Int>      = emptySet(), // Este campo NO pertenece aquí, está en notifCfg

    // Configuración de periodo sugerida
    val periodQty    : Int?          = null, // Cantidad (nullable)
    val periodUnit   : PeriodUnit    = PeriodUnit.INDEFINIDO, // Unidad por defecto

    // Configuración de notificación sugerida (objeto anidado)
    val notifCfg     : NotifConfig   = NotifConfig(), // Usa defaults de NotifConfig

    // Flag misceláneo
    val scheduled    : Boolean       = false // Default false
)
