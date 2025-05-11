/**
 * @file    HabitCategory.kt
 * @ingroup domain_model // Grupo específico para modelos de dominio
 * @brief   Define las categorías predefinidas en las que se puede clasificar un hábito.
 *
 * @details Esta enumeración proporciona un conjunto fijo de categorías para organizar
 * los hábitos de los usuarios. Cada categoría tiene un nombre interno (la constante enum)
 * y una propiedad `display` que contiene el texto legible para mostrar en la interfaz
 * de usuario. Incluye un metodo `fromRaw` para convertir de forma segura una cadena
 * (potencialmente leída de una fuente externa como Firestore) a la instancia enum correspondiente.
 */
package com.app.tibibalance.domain.model

/**
 * @brief Enumeración que representa las posibles categorías de un [Habit].
 * @details Cada constante enum representa una categoría lógica y almacena
 * una cadena asociada ([display]) para su representación en la UI.
 *
 * @property display El nombre legible de la categoría, utilizado para mostrar al usuario.
 */
enum class HabitCategory(val display: String) {
    /** @brief Categoría para hábitos relacionados con la salud física (ejercicio, dieta, etc.). */
    SALUD("SALUD"),

    /** @brief Categoría para hábitos enfocados en mejorar la eficiencia y el rendimiento (trabajo, estudio, etc.). */
    PRODUCTIVIDAD("PRODUCTIVIDAD"),

    /** @brief Categoría para hábitos centrados en el bienestar mental y emocional (meditación, lectura, hobbies, etc.). */
    BIENESTAR("BIENESTAR"),

    /** @brief Categoría genérica o por defecto para hábitos que no encajan en las otras o creados libremente por el usuario. */
    PERSONALIZADA("PERSONALIZADA");

    /**
     * @brief Objeto compañero que contiene métodos estáticos asociados a [HabitCategory].
     */
    companion object {
        /**
         * @brief Convierte una representación de cadena (String) a la instancia [HabitCategory] correspondiente.
         *
         * @details Intenta encontrar una coincidencia entre la cadena `raw` y el nombre interno (`name`)
         * o el texto de presentación (`display`) de cada constante enum, ignorando mayúsculas y minúsculas.
         * Si no se encuentra ninguna coincidencia, devuelve [PERSONALIZADA] como valor por defecto.
         * Es útil para deserializar valores de categoría provenientes de fuentes externas (e.g., Firestore)
         * de forma robusta.
         *
         * @param raw La cadena de texto que representa la categoría (puede ser el nombre interno o el de display).
         * @return La instancia de [HabitCategory] correspondiente, o [PERSONALIZADA] si no se encontró ninguna coincidencia.
         */
        fun fromRaw(raw: String): HabitCategory =
            // Busca en todas las entradas del enum
            entries.firstOrNull { category ->
                // Compara ignorando mayúsculas/minúsculas con el nombre interno O el texto de display
                category.name.equals(raw, ignoreCase = true) || category.display.equals(raw, ignoreCase = true)
            } ?: PERSONALIZADA // Si no encuentra ninguna, devuelve PERSONALIZADA
    }
}
