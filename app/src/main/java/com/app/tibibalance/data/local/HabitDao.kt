/* data/local/HabitDao.kt */
package com.app.tibibalance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun observeAll(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: String)

    /** Borra la tabla completa (logout o cambio de cuenta). */
    @Query("DELETE FROM habits")
    suspend fun clear()
}
