/**
 * @file    HabitDao.kt
 * @ingroup data_local
 * @brief   DAO para la tabla `habits`.
 */
package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    /* ─────────────–– Consultas reactivas ───────────── */

    /** Observa **todos** los hábitos. */
    @Query("SELECT * FROM habits")
    fun observeAll(): Flow<List<HabitEntity>>

    /** Observa **un** hábito por su `id`. */
    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<HabitEntity?>

    /* ─────────────–– Mutaciones ───────────── */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM habits")
    suspend fun clear()
}
