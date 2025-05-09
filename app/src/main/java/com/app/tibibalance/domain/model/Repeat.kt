/**
 * @file    Repeat.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define los diferentes patrones de repetición o frecuencia para un hábito ([Habit]).
 *
 * @details Esta interfaz sellada (`sealed interface`) modela las distintas formas en que
 * un hábito puede repetirse en el tiempo. Cada implementación representa una
 * regla de repetición específica: sin repetición, diaria (cada 'n' días),
 * semanal (en días específicos) o mensual (en un día específico del mes).
 *
 * Es utilizada dentro del modelo [Habit] para determinar cuándo debe realizarse
 * o registrarse el hábito.
 *
 * @see Habit Modelo principal que utiliza este patrón de repetición.
 * @see RepeatPreset Enumeración de presets comunes que pueden mapearse a instancias de [Repeat].
 */
package com.app.tibibalance.domain.model

// Nota: El import original usa com.google.type.DayOfWeek.
// Si estás en Android, podrías preferir java.time.DayOfWeek o kotlinx.datetime.DayOfWeek.
// Mantendré el import original según la instrucción.
import com.google.type.DayOfWeek // Asegúrate que este es el tipo DayOfWeek deseado.

/**
 * @brief Interfaz sellada que representa un patrón de repetición para un hábito.
 * @details Define los diferentes tipos de frecuencia con los que un hábito puede ocurrir.
 * Las implementaciones son objetos o data classes que contienen los parámetros específicos
 * para cada tipo de repetición.
 */
sealed interface Repeat {

    /**
     * @brief Representa un hábito que no tiene un patrón de repetición definido (se realiza una sola vez o manualmente).
     */
    object None : Repeat

    /**
     * @brief Representa un hábito que se repite cada un número específico de días.
     * @param every El intervalo en días entre cada repetición. `1` significa diario, `2` cada dos días, etc.
     * Debe ser un entero positivo. Por defecto es `1` (diario).
     */
    data class Daily(val every: Int = 1) : Repeat

    /**
     * @brief Representa un hábito que se repite en días específicos de la semana.
     * @param days Un [Set] de [DayOfWeek] que indica en qué días de la semana se debe realizar el hábito
     * (e.g., `{ MONDAY, WEDNESDAY, FRIDAY }`). El Set no debe estar vacío.
     * **Nota:** Asegúrate de usar el tipo `DayOfWeek` correcto (e.g., `com.google.type.DayOfWeek`, `java.time.DayOfWeek`).
     */
    data class Weekly(val days: Set<DayOfWeek>) : Repeat

    /**
     * @brief Representa un hábito que se repite una vez al mes en un día específico.
     * @param dayOfMonth El día del mes (un entero entre 1 y 31) en el que se debe realizar el hábito.
     * La lógica de manejo debe considerar meses con menos días (e.g., ¿qué pasa el 31 en febrero?).
     */
    data class Monthly(val dayOfMonth: Int) : Repeat
}
