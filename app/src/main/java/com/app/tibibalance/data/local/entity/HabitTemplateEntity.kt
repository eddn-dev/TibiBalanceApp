/**
 * @file    HabitTemplateEntity.kt
 * @ingroup data_local
 * @brief   Entidad Room que persiste las **plantillas de hábitos** en la base de datos local.
 *
 * @details Cada registro en la tabla `habit_templates` corresponde a un documento
 * en la colección **`habitTemplates`** de Firestore. Mantener la misma `id`
 * (que es el docId de Firestore) simplifica la sincronización _offline-first_
 * y el mecanismo *Last-Write-Wins (LWW)*.
 *
 * La plantilla sirve como punto de partida en el asistente de creación
 * de hábitos. Sus campos reflejan la configuración inicial sugerida que se
 * mostrará al usuario: nombre, descripción, categoría, icono, duración
 * de sesión, patrón de repetición, límites de periodo y ajustes de
 * notificación. Los campos Enum ([SessionUnit], [RepeatPreset], [PeriodUnit],
 * [NotifMode]) se almacenan directamente por Room. Las listas (`notifTimesOfDay`,
 * `notifDaysOfWeek`) requieren [TypeConverter] (ver [com.app.tibibalance.data.local.mapper.NotifConverters]).
 */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.tibibalance.domain.model.* // Asegúrate de importar todos los modelos necesarios

/**
 * @brief Representa un registro en la tabla **habit_templates**.
 * @see com.app.tibibalance.domain.model.HabitTemplate Modelo de dominio correspondiente.
 *
 * @property id Clave primaria de la tabla, corresponde al `docId` en Firestore.
 * @property name Nombre visible de la plantilla (e.g., "Beber Agua").
 * @property description Descripción corta mostrada en la UI (e.g., "Mantente hidratado").
 * @property category Categoría lógica del hábito, almacenada como el `name()` del enum [HabitCategory] (e.g., "SALUD").
 * @property icon Nombre del icono Material que representa al hábito (e.g., "LocalDrink").
 *
 * @property sessionQty Cantidad numérica asociada a la sesión (e.g., 2), `null` si la sesión no tiene cantidad medible o es indefinida.
 * @property sessionUnit Unidad de la sesión ([SessionUnit], e.g., LITROS, MINUTOS, INDEFINIDO).
 *
 * @property repeatPreset Preconfiguración de frecuencia sugerida ([RepeatPreset], e.g., DIARIO, SEMANAL).
 * @property periodQty Cantidad numérica del periodo total del hábito (e.g., 30), `null` si el hábito no tiene duración definida.
 * @property periodUnit Unidad del periodo total ([PeriodUnit], e.g., DIAS, MESES, INDEFINIDO).
 *
 * @property notifMode Modo de la notificación ([NotifMode], e.g., SOUND, SILENT).
 * @property notifMessage Texto predeterminado para el cuerpo de la notificación.
 * @property notifTimesOfDay Lista de horas propuestas para los recordatorios (formato `"HH:mm"`). Requiere [com.app.tibibalance.data.local.mapper.NotifConverters].
 * @property notifDaysOfWeek Lista de días de la semana (1=Lunes...7=Domingo) para notificaciones personalizadas. Requiere [com.app.tibibalance.data.local.mapper.NotifConverters].
 * @property notifAdvanceMin Minutos de antelación con los que se mostrará la notificación (0 = justo a la hora).
 * @property notifVibrate Indica si la notificación debe incluir vibración (además del sonido, si aplica).
 *
 * @property scheduled Flag interno (potencialmente para lógica futura) que indica si la plantilla requiere alguna acción de programación al ser seleccionada.
 */
@Entity(tableName = "habit_templates")
data class HabitTemplateEntity(

    /* ────────── Identificador ────────── */
    @PrimaryKey
    val id: String,

    /* ────────── Datos visibles ───────── */
    val name       : String,
    val description: String,
    val category   : String, // Almacena HabitCategory.name
    val icon       : String,

    /* ────────── Tiempo de sesión ─────── */
    val sessionQty : Int?,
    val sessionUnit: SessionUnit, // Room maneja Enums

    /* ────────── Frecuencia y periodo ─── */
    val repeatPreset: RepeatPreset, // Room maneja Enums
    val periodQty   : Int?,
    val periodUnit  : PeriodUnit, // Room maneja Enums

    /* ────────── Notificación ─────────── */
    val notifMode       : NotifMode, // Room maneja Enums
    val notifMessage    : String,
    // TypeConverter necesario para List<String>
    val notifTimesOfDay : List<String>, // Ejemplo: ["08:00", "20:30"]
    // TypeConverter necesario para List<Int>
    val notifDaysOfWeek : List<Int>,    // Ejemplo: [1, 3, 5] (Lunes, Miércoles, Viernes)
    val notifAdvanceMin : Int,
    val notifVibrate    : Boolean,

    /* ────────── Miscelánea ───────────── */
    val scheduled: Boolean
)
