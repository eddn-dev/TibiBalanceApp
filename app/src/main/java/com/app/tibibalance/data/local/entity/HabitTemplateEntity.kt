/* data/local/entity/HabitTemplateEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.tibibalance.domain.model.*

/**
 * @file HabitTemplateEntity.kt
 * @ingroup data_local
 * @brief Entidad Room que persiste las **plantillas de hábitos** en la base de datos local.
 *
 * Cada registro corresponde a un documento en la colección
 * **`habitTemplates`** de Firestore.  Mantener la misma `id` simplifica
 * la sincronización _offline-first_ y el mecanismo *Last-Write-Wins*.
 *
 * La plantilla sirve como punto de partida en el asistente de creación
 * de hábitos.  Sus campos reflejan la configuración inicial que se
 * mostrará al usuario: nombre, descripción, categoría, icono, duración
 * de sesión, patrón de repetición, límites de periodo y ajustes de
 * notificación.
 */

/**
 * @brief Registro de la tabla **habit_templates**.
 *
 * @property id               Clave primaria - docId en Firestore.
 * @property name             Nombre visible de la plantilla.
 * @property description      Descripción corta que aparece en la UI.
 * @property category         Categoría lógica (bienestar, ejercicio, etc.).
 * @property icon             Nombre del icono Material que representa al hábito.
 *
 * @property sessionQty       Cantidad por sesión (nullable si no aplica).
 * @property sessionUnit      Unidad de la cantidad por sesión.
 *
 * @property repeatPreset     Preset de frecuencia sugerida (p.ej. DIARIO).
 * @property periodQty        Límite de periodo (nullable si no aplica).
 * @property periodUnit       Unidad del periodo.
 *
 * @property notifMode        Modo de notificación (p.ej. HORA_FIJA).
 * @property notifMessage     Texto sugerido que mostrará la notificación.
 * @property notifTimesOfDay  Horas del día propuestas (formato `"HH:mm"`).
 * @property notifDaysOfWeek  Días de la semana (1=Lunes … 7=Domingo).
 * @property notifAdvanceMin  Minutos de antelación para mostrar la alerta.
 * @property notifVibrate     `true` si debe vibrar el dispositivo.
 *
 * @property scheduled        `true` si la plantilla está marcada para
 *                            programar tareas de notificación al importarse.
 */
@Entity(tableName = "habit_templates")
data class HabitTemplateEntity(

    /* ────────── Identificador ────────── */
    @PrimaryKey
    val id: String,

    /* ────────── Datos visibles ───────── */
    val name       : String,
    val description: String,
    val category   : String,
    val icon       : String,

    /* ────────── Tiempo de sesión ─────── */
    val sessionQty : Int?,
    val sessionUnit: SessionUnit,

    /* ────────── Frecuencia y periodo ─── */
    val repeatPreset: RepeatPreset,
    val periodQty   : Int?,
    val periodUnit  : PeriodUnit,

    /* ────────── Notificación ─────────── */
    val notifMode       : NotifMode,
    val notifMessage    : String,
    val notifTimesOfDay : List<String>, // ["08:00","20:30"]
    val notifDaysOfWeek : List<Int>,    // [1,3,5]
    val notifAdvanceMin : Int,
    val notifVibrate    : Boolean,

    /* ────────── Miscelánea ───────────── */
    val scheduled: Boolean
)
