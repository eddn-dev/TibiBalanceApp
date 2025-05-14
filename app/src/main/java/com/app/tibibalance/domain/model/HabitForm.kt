/**
 * @file    HabitForm.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Modelo intermedio que representa el estado editable de un hábito en la UI,
 * especialmente durante su creación o edición en el asistente (wizard).
 *
 * @details Esta data class actúa como un DTO (Data Transfer Object) o un ViewModel state
 * para la interfaz de usuario. Contiene todos los campos configurables de un hábito
 * antes de que se convierta en la entidad de dominio final [Habit].
 *
 * Simplifica la gestión del estado en la UI, ya que agrupa todos los campos editables.
 * Puede ser inicializado vacío o precargado con valores por defecto tomados de una
 * [HabitTemplate] mediante el método [prefillFromTemplate].
 *
 * Una vez que el usuario completa el formulario en la UI, esta instancia de [HabitForm]
 * se utiliza (junto con la [NotifConfig] correspondiente) para construir el objeto [Habit]
 * final que se pasará al repositorio para su persistencia (ver [com.app.tibibalance.data.mapper.HabitFormMapper]).
 *
 * @see Habit Modelo de dominio final.
 * @see HabitTemplate Modelo de plantilla para precargar valores.
 * @see com.app.tibibalance.ui.wizard.createHabit.AddHabitViewModel ViewModel que probablemente gestiona este formulario.
 * @see com.app.tibibalance.data.mapper.HabitFormMapper Mapper que convierte este formulario a Habit.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Representa el estado mutable de un formulario de creación/edición de hábitos.
 * @details Contiene todos los campos que el usuario puede modificar a través de la UI
 * antes de guardar el hábito definitivo. Proporciona valores por defecto para una
 * creación inicial limpia.
 *
 * @property name Nombre que el usuario asigna al hábito. Vacío por defecto.
 * @property desc Descripción opcional del hábito. Vacío por defecto.
 * @property category La [HabitCategory] seleccionada por el usuario. Por defecto [HabitCategory.SALUD].
 * @property icon El nombre del icono Material seleccionado para representar el hábito. Por defecto "FitnessCenter".
 * @property sessionQty La cantidad numérica asociada a una sesión (e.g., 10 para "10 minutos"). `null` si la unidad es [SessionUnit.INDEFINIDO].
 * @property sessionUnit La [SessionUnit] que define la medida de una sesión (e.g., MINUTOS, VECES, INDEFINIDO). Por defecto [SessionUnit.INDEFINIDO].
 * @property repeatPreset La frecuencia predefinida ([RepeatPreset]) seleccionada (e.g., DIARIO, SEMANAL, PERSONALIZADO). Por defecto [RepeatPreset.INDEFINIDO].
 * @property weekDays Un [Set] de enteros (1=Lunes...7=Domingo) que indica los días específicos de la semana
 * si `repeatPreset` es [RepeatPreset.PERSONALIZADO]. Vacío por defecto o si se usa un preset no personalizado.
 * @property periodQty La cantidad numérica para la duración total del hábito (e.g., 30 para "30 días"). `null` si el periodo es [PeriodUnit.INDEFINIDO].
 * @property periodUnit La [PeriodUnit] que define la duración total del hábito (e.g., DIAS, SEMANAS, INDEFINIDO). Por defecto [PeriodUnit.INDEFINIDO].
 * @property notify Un [Boolean] que indica si el usuario desea activar las notificaciones para este hábito. `false` por defecto.
 * @property challenge Un [Boolean] que indica si el hábito se crea como un "reto" (con posible configuración inmutable después). `false` por defecto.
 */
data class HabitForm(
    val name        : String            = "",
    val desc        : String            = "",
    val category    : HabitCategory     = HabitCategory.SALUD, // Default a SALUD
    val icon        : String            = "FitnessCenter", // Default icon

    val sessionQty  : Int?              = null, // Nullable
    val sessionUnit : SessionUnit       = SessionUnit.INDEFINIDO, // Default

    val repeatPreset: RepeatPreset      = RepeatPreset.INDEFINIDO, // Default
    val weekDays    : Set<Int>          = emptySet(), // Días específicos (1-7) para PERSONALIZADO

    val periodQty   : Int?              = null, // Nullable
    val periodUnit  : PeriodUnit        = PeriodUnit.INDEFINIDO, // Default

    val notify      : Boolean           = false, // Notificaciones desactivadas por defecto
    val challenge   : Boolean           = false  // No es reto por defecto
) {

    /**
     * @brief Crea una nueva instancia de [HabitForm] copiando los valores de una [HabitTemplate].
     * @details Utiliza la función `copy` de la data class para generar un nuevo formulario
     * con los campos inicializados según la plantilla `t`. Es útil para iniciar el
     * asistente de creación de hábitos con valores predefinidos.
     * Los `weekDays` solo se copian si el `repeatPreset` de la plantilla es [RepeatPreset.PERSONALIZADO].
     * El campo `challenge` no se copia de la plantilla, se mantiene en su valor por defecto (`false`).
     *
     * @param t La [HabitTemplate] de la cual se tomarán los valores iniciales.
     * @return Una nueva instancia de [HabitForm] con los campos rellenados según la plantilla.
     */
    fun prefillFromTemplate(t: HabitTemplate) = this.copy( // Usa copy para crear nueva instancia
        name         = t.name,
        desc         = t.description,
        category     = t.category,
        icon         = t.icon,
        sessionQty   = t.sessionQty,
        sessionUnit  = t.sessionUnit,
        repeatPreset = t.repeatPreset,
        // Copia los días de la semana solo si la plantilla usa frecuencia personalizada
        weekDays     = if (t.repeatPreset == RepeatPreset.PERSONALIZADO)
            t.notifCfg.weekDays.days // Obtiene el Set<Int> de WeekDays
        else
            emptySet(), // Si no, deja el Set vacío
        periodQty    = t.periodQty,
        periodUnit   = t.periodUnit,
        // El estado 'notify' se basa en si la config de notificación de la plantilla está habilitada
        notify       = t.notifCfg.enabled
        // 'challenge' no se pre-rellena desde la plantilla
    )
}
