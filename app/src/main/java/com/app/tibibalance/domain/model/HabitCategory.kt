/**
 * @file    HabitCategory.kt
 * @ingroup domain
 * @brief   Tipos de categoría disponibles para un hábito.
 *
 * Cada constante incorpora un texto de presentación que se muestra en la UI.
 */
package com.app.tibibalance.domain.model

/**
 * @enum HabitCategory
 * @brief Enumeración de categorías de hábito.
 *
 * @property display Texto legible que se muestra en la interfaz.
 */
enum class HabitCategory(val display: String) {
    SALUD("Salud"),
    PRODUCTIVIDAD("Productividad"),
    BIENESTAR("Bienestar"),
    PERSONALIZADA("Personalizada");

    companion object {
        /**
         * @brief Convierte una cadena arbitraria a su valor enumerado.
         *
         * Ignora mayúsculas/minúsculas y acepta tanto el nombre interno
         * como el texto de presentación.
         *
         * @param raw Cadena recibida —por ejemplo— desde Firestore.
         * @return    Categoría correspondiente o [PERSONALIZADA] por defecto.
         */
        fun fromRaw(raw: String): HabitCategory =
            entries.firstOrNull {
                it.name.equals(raw, true) || it.display.equals(raw, true)
            } ?: PERSONALIZADA
    }
}
