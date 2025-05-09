/**
 * @file    ProfileDao.kt
 * @ingroup data_local
 * @brief   DAO para la entidad de perfil de usuario [UserProfileEntity].
 *
 * Gestiona el acceso a la tabla **`profile`** almacenada localmente con Room.
 * Como la aplicación solo maneja **un** perfil por cuenta, las consultas
 * utilizan `LIMIT 1` para evitar ambigüedades y asegurar que solo se
 * manipule un registro.
 */
package com.app.tibibalance.data.local.dao

import androidx.room.*
import com.app.tibibalance.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * @brief DAO (Data Access Object) para la entidad [UserProfileEntity].
 * @details Define las operaciones de consulta, inserción/actualización y borrado
 * para la tabla `profile` en la base de datos Room, que almacena la
 * información del usuario actual.
 */
@Dao
interface ProfileDao {

    /**
     * @brief   Observa el perfil de usuario almacenado en caché.
     * @details Devuelve un flujo reactivo que emite el registro único de la tabla `profile`
     * cada vez que este cambia en la base de datos. La consulta usa `LIMIT 1`
     * ya que solo se espera un perfil por usuario.
     *
     * @return  [Flow] que emite el [UserProfileEntity] actual o `null`
     * si la tabla está vacía (p.ej., antes del primer login o después de `clear()`).
     */
    @Query("SELECT * FROM profile LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    /**
     * @brief   Inserta o reemplaza el perfil de usuario.
     * @details Utiliza la estrategia [OnConflictStrategy.REPLACE], por lo que si ya
     * existe un perfil con el mismo `uid` (clave primaria), será sobrescrito.
     *
     * @param profile Instancia de [UserProfileEntity] que se va a persistir en la base de datos local.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    /**
     * @brief   Elimina el perfil (el único registro) del almacenamiento local.
     * @details Se invoca normalmente al cerrar sesión (`signOut`) o al eliminar la cuenta,
     * para limpiar los datos sensibles del dispositivo y evitar que se muestren
     * datos del usuario anterior si otro inicia sesión.
     */
    @Query("DELETE FROM profile")
    suspend fun clear()
}
