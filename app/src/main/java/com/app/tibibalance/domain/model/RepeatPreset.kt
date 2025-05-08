/**
 * @file    RepeatPreset.kt
 * @ingroup domain
 * @brief   Preajustes de frecuencia para la repetición de hábitos.
 *
 * Estos valores se muestran en la UI como opciones rápidas; cada uno
 * corresponde a un patrón concreto que el *scheduler* interpreta para
 * programar notificaciones y calcular la siguiente instancia.
 *
 * | Constante        | Descripción                                      |
 * |------------------|--------------------------------------------------|
 * | INDEFINIDO       | No hay repetición &rArr; sin notificaciones.     |
 * | DIARIO           | Todos los días.                                  |
 * | CADA_3_DIAS      | Intervalo de 3 días entre sesiones.              |
 * | SEMANAL          | Una vez por semana (mismo día).                  |
 * | QUINCENAL        | Cada 15 días.                                    |
 * | MENSUAL          | Una vez al mes (mismo día).                      |
 * | PERSONALIZADO    | El usuario elige libremente los días de la semana.|
 */
package com.app.tibibalance.domain.model

enum class RepeatPreset {
    INDEFINIDO,
    DIARIO,
    CADA_3_DIAS,
    SEMANAL,
    QUINCENAL,
    MENSUAL,
    PERSONALIZADO
}
