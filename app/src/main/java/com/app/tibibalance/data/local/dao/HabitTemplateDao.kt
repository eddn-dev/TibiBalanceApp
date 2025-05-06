package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitTemplateDao {

    /** flujo reactivo para la UI */
    @Query("SELECT * FROM habit_templates ORDER BY category, name")
    fun observeAll(): Flow<List<HabitTemplateEntity>>

    /** inserta o actualiza sincrónicamente */
    @Upsert
    suspend fun upsert(templates: List<HabitTemplateEntity>)

    @Query("DELETE FROM habit_templates")
    suspend fun clear()
}
