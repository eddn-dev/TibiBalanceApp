package com.app.tibibalance.domain.model


enum class RepeatPreset {
    INDEFINIDO,                    // sin repetición ⇒ no hay notificaciones
    DIARIO, CADA_3_DIAS, SEMANAL, QUINCENAL, MENSUAL,
    PERSONALIZADO                  // se escogen días de la semana libremente
}