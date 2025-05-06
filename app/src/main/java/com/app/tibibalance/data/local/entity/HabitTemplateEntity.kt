/* data/local/entity/HabitTemplateEntity.kt */
package com.app.tibibalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.tibibalance.domain.model.*

@Entity(tableName = "habit_templates")
data class HabitTemplateEntity(

    /** Clave primaria = docId de Firestore. */
    @PrimaryKey
    val id: String,

    /* -------- Datos visibles -------- */
    val name       : String,
    val description: String,
    val category   : String,
    val icon       : String,

    /* -------- Tiempo de sesión -------- */
    val sessionQty : Int?,
    val sessionUnit: SessionUnit,

    /* -------- Frecuencia y periodo -------- */
    val repeatPattern: RepeatPattern,
    val periodQty    : Int?,
    val periodUnit   : PeriodUnit,

    /* -------- Notificación -------- */
    val notifMode         : NotifMode,
    val notifMessage      : String,
    val notifTimesOfDay   : List<String>,   // ["08:00","20:30"]
    val notifDaysOfWeek   : List<Int>,      // [1,3,5]
    val notifAdvanceMin   : Int,
    val notifVibrate      : Boolean,

    /* -------- Miscelánea -------- */
    val scheduled : Boolean
)
