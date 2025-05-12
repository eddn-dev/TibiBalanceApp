package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.app.tibibalance.data.local.entity.HabitActivityEntity
import kotlinx.coroutines.flow.Flow

/* dao/HabitActivityDao.kt */
@Dao
interface HabitActivityDao {
    @Insert(onConflict = REPLACE) suspend fun insert(act: HabitActivityEntity)
    @Query("SELECT * FROM habit_activity WHERE habitId=:id ORDER BY epochMillis")
    fun observe(id: String): Flow<List<HabitActivityEntity>>
}