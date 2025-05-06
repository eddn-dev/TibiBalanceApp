package com.app.tibibalance.domain.model

// domain/model/HabitCategory.kt
enum class HabitCategory(val display: String) {
    SALUD("Salud"),
    PRODUCTIVIDAD("Productividad"),
    BIENESTAR("Bienestar"),
    PERSONALIZADA("Personalizada");

    companion object {
        /** Convierte texto de Firestore a enum (ignora mayúsculas/minúsculas) */
        fun fromRaw(raw: String): HabitCategory =
            entries.firstOrNull { it.name.equals(raw, true) || it.display.equals(raw, true) }
                ?: PERSONALIZADA          // fallback
    }
}

