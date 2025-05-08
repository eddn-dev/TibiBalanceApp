/* data/local/ProfileDao.kt */
package com.app.tibibalance.data.local.dao

import androidx.room.*
import com.app.tibibalance.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * @file    ProfileDao.kt
 * @ingroup data_local
 * @brief   DAO para la entidad de perfil de usuario.
 *
 * Gestiona el acceso a la tabla **`profile`** almacenada localmente con Room.
 * Como la aplicación solo maneja **un** perfil por cuenta, las consultas
 * utilizan `LIMIT 1` para evitar ambigüedades.
 */
@Dao
interface ProfileDao {

    /**
     * @brief   Observa el perfil de usuario almacenado en caché.
     * @details Devuelve un flujo reactivo que emite el registro cada vez que
     *          el perfil cambia en la base de datos.
     *
     * @return  Flow que emite el [UserProfileEntity] actual o `null`
     *          si aún no se ha guardado ninguno.
     */
    @Query("SELECT * FROM profile LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    /**
     * @brief   Inserta o reemplaza el perfil de usuario.
     *
     * @param profile Instancia de [UserProfileEntity] que se persiste.
     *                Si existe un registro con el mismo `uid`, se reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    /**
     * @brief   Elimina el perfil del almacenamiento local.
     *
     * Se invoca normalmente al cerrar sesión para limpiar los datos
     * sensibles del dispositivo.
     */
    @Query("DELETE FROM profile")
    suspend fun clear()
}
