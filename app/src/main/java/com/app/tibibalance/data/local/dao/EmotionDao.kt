package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.EmotionEntity   // <- importa tu entidad
import java.time.LocalDate                                 // <- importa LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    @Query("SELECT * FROM emotions")
    fun observeAll(): Flow<List<EmotionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EmotionEntity)

    @Query("DELETE FROM emotions WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)

    @Query("DELETE FROM emotions")
    suspend fun deleteAll()    // te vendrá bien para limpiar toda la tabla
}
