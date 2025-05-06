// domain/model/HabitTemplate.kt
package com.app.tibibalance.domain.model

/**
 * Documento de la colección `habitTemplates` en Firestore.
 *
 * - Todas las unidades y patrones se expresan con enums para evitar “strings mágicos”.
 * - Los campos opcionales (`Int?`, `List<…>?`) pueden omitirse en el JSON si llevan `null` / vacío.
 */

data class HabitTemplate(
    val id            : String        = "",
    val name          : String        = "",
    val description   : String        = "",
    val category    : HabitCategory = HabitCategory.SALUD,
    val icon          : String        = "ic_default_habit",

    // --- Sesión ---
    val sessionQty    : Int?          = null,
    val sessionUnit   : SessionUnit   = SessionUnit.INDEFINIDO,

    // --- Frecuencia ---
    val repeatPattern : RepeatPattern = RepeatPattern.INDEFINIDO,

    // --- Periodo total ---
    val periodQty     : Int?          = null,
    val periodUnit    : PeriodUnit    = PeriodUnit.INDEFINIDO,

    // --- Notificación avanzada ---
    val notifCfg      : NotifConfig   = NotifConfig(),

    val scheduled     : Boolean       = false
)

