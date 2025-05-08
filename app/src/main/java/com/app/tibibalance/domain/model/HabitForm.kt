/**
 * @file    HabitForm.kt
 * @ingroup domain
 * @brief   Formulario intermedio que la UI utiliza antes de persistir un {@link Habit}.
 *
 * El formulario encapsula todos los campos que el asistente de creación
 * necesita — nombre, icono, patrón de repetición, periodo, etc.— y permite
 * precargarse a partir de una {@link HabitTemplate}.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Modelo de datos editable por la UI.
 *
 * @param name         Nombre del hábito.
 * @param desc         Descripción opcional.
 * @param category     Categoría lógica.
 * @param icon         Nombre del icono Material.
 * @param sessionQty   Cantidad por sesión; `null` si {@code sessionUnit == INDEFINIDO}.
 * @param sessionUnit  Unidad de la sesión.
 * @param repeatPreset Frecuencia sugerida o personalizada.
 * @param weekDays     Días de la semana (1‥7) si la frecuencia es personalizada.
 * @param periodQty    Límite de periodo; `null` si no aplica.
 * @param periodUnit   Unidad del periodo.
 * @param notify       `true` para activar notificaciones.
 * @param challenge    `true` para modo reto (configuración inmutable).
 */
data class HabitForm(
    val name        : String            = "",
    val desc        : String            = "",
    val category    : HabitCategory     = HabitCategory.SALUD,
    val icon        : String            = "FitnessCenter",

    val sessionQty  : Int?              = null,
    val sessionUnit : SessionUnit       = SessionUnit.INDEFINIDO,

    val repeatPreset: RepeatPreset      = RepeatPreset.INDEFINIDO,
    val weekDays    : Set<Int>          = emptySet(),

    val periodQty   : Int?              = null,
    val periodUnit  : PeriodUnit        = PeriodUnit.INDEFINIDO,

    val notify      : Boolean           = false,
    val challenge   : Boolean           = false
) {

    /**
     * @brief Genera una copia del formulario precargada con una plantilla.
     *
     * @param t Plantilla de la que se toman los valores por defecto.
     * @return  Nuevo {@link HabitForm} con los campos completados.
     */
    fun prefillFromTemplate(t: HabitTemplate) = copy(
        name         = t.name,
        desc         = t.description,
        category     = t.category,
        icon         = t.icon,
        sessionQty   = t.sessionQty,
        sessionUnit  = t.sessionUnit,
        repeatPreset = t.repeatPreset,
        weekDays     = if (t.repeatPreset == RepeatPreset.PERSONALIZADO)
            t.notifCfg.weekDays.days
        else emptySet(),
        periodQty    = t.periodQty,
        periodUnit   = t.periodUnit,
        notify       = t.notifCfg.enabled
    )
}
