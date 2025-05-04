/* data/local/ProfileDao.kt */
package com.app.tibibalance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    /** Observa el único perfil guardado (o null si no hay). */
    @Query("SELECT * FROM profile LIMIT 1")
    fun observe(): Flow<UserProfile?>

    /** Inserta o sustituye el perfil. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfile)

    /** Borra todos los perfiles (se llama al hacer sign-out). */
    @Query("DELETE FROM profile")
    suspend fun clear()
}
