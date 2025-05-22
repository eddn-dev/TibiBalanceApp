// src/main/java/com/app/tibibalance/data/local/dao/MetricsDao.kt
package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.MetricsEntity

@Dao
interface MetricsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metrics: MetricsEntity)

    /**
     * @brief Recupera la última entrada de métricas, ordenando por fecha descendente.
     */
    @Query("SELECT * FROM daily_metrics ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMetrics(): MetricsEntity?
}
