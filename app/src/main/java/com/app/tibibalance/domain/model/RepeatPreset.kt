/**
 * @file    RepeatPreset.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define preajustes comunes de frecuencia para la repetición de hábitos ([Habit]).
 *
 * @details Esta enumeración proporciona un conjunto de opciones de frecuencia predefinidas
 * que se pueden presentar al usuario en la interfaz (e.g., en un `Spinner` o `RadioGroup`)
 * para simplificar la configuración de la repetición de un hábito.
 *
 * Cada constante representa un patrón de repetición común. La lógica de la aplicación
 * (posiblemente en un ViewModel o UseCase) puede mapear una selección de [RepeatPreset]
 * a una configuración más detallada de [Repeat] si es necesario (especialmente para
 * [PERSONALIZADO]).
 *
 * La tabla en los detalles del archivo describe el significado de cada preset:
 *
 * | Constante        | Descripción                                        | Mapeo Potencial a [Repeat]          |
 * |------------------|----------------------------------------------------|-------------------------------------|
 * | [INDEFINIDO]     | Sin repetición definida (manual o única vez).        | [Repeat.None]                       |
 * | [DIARIO]         | Se realiza todos los días.                         | [Repeat.Daily] (`every = 1`)        |
 * | [CADA_3_DIAS]    | Se realiza cada 3 días.                            | [Repeat.Daily] (`every = 3`)        |
 * | [SEMANAL]        | Se realiza una vez por semana, el mismo día.       | [Repeat.Weekly] (con un solo día) |
 * | [QUINCENAL]      | Se realiza cada 15 días (aprox. dos veces al mes). | (Lógica personalizada necesaria)    |
 * | [MENSUAL]        | Se realiza una vez al mes, el mismo día del mes.   | [Repeat.Monthly]                  |
 * | [PERSONALIZADO]  | El usuario selecciona días específicos de la semana. | [Repeat.Weekly] (con `Set<DayOfWeek>`) |
 *
 * @see Habit Modelo principal que utiliza este preset.
 * @see Repeat Interfaz sellada que define los patrones de repetición detallados.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Enumeración de preajustes comunes para la frecuencia de repetición de un hábito.
 * @details Simplifica la selección de la frecuencia por parte del usuario en la UI.
 */
enum class RepeatPreset {
    /** @brief Sin frecuencia definida; el hábito se realiza manualmente o una sola vez. */
    INDEFINIDO,

    /** @brief El hábito se realiza todos los días. */
    DIARIO,

    /** @brief El hábito se realiza cada tres días. */
    CADA_3_DIAS,

    /** @brief El hábito se realiza una vez por semana (el día específico puede definirse por separado o basarse en la fecha de inicio). */
    SEMANAL,

    /** @brief El hábito se realiza aproximadamente cada quince días. */
    QUINCENAL,

    /** @brief El hábito se realiza una vez al mes (el día específico del mes puede definirse por separado o basarse en la fecha de inicio). */
    MENSUAL,

    /** @brief Indica que la frecuencia se define mediante una selección personalizada de días de la semana (ver [Habit.notifConfig.weekDays] o [Repeat.Weekly.days]). */
    PERSONALIZADO
}
