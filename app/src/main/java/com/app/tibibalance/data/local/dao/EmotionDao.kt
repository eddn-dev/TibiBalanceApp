package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.EmotionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    // Observar todas las emociones
    @Query("SELECT * FROM emotions ORDER BY date ASC")
    fun observeAll(): Flow<List<EmotionEntity>>

    // Observar una emoción por fecha
    @Query("SELECT * FROM emotions WHERE date = :date")
    fun observeByDate(date: String): Flow<EmotionEntity?>

    // Insertar o actualizar emoción (Room)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EmotionEntity)

    // Borrar una emoción por fecha
    @Query("DELETE FROM emotions WHERE date = :date")
    suspend fun deleteByDate(date: String)

    // Borrar todas las emociones
    @Query("DELETE FROM emotions")
    suspend fun deleteAll()
}
