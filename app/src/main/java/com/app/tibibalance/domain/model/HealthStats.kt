package com.app.tibibalance.domain.model


/**
 * Modelo de dominio con las métricas de salud que usa toda la app.
 */
data class HealthStats(
    val steps: Long,
    val calories: Double,
    val heartRate: Double,
    val exerciseMinutes: Int
)
