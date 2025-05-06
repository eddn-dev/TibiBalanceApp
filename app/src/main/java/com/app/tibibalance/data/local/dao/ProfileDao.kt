/* data/local/ProfileDao.kt */
package com.app.tibibalance.data.local.dao

import androidx.room.*
import com.app.tibibalance.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun clear()
}
