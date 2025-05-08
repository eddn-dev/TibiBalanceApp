/* data/local/HabitDao.kt */
package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tibibalance.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

/**
 * @file    HabitDao.kt
 * @ingroup data_local
 * @brief   DAO para la tabla **habits**.
 *
 * Provee operaciones de acceso y modificación sobre la tabla
 * de hábitos almacenada localmente con Room.
 * Cada registro se serializa como JSON en la entidad
 * [HabitEntity] para mantener la compatibilidad con el
 * esquema de Firestore y soportar la sincronización
 * _offline-first_ mediante **Last-Write-Wins (LWW)**.
 */
@Dao
interface HabitDao {

    /**
     * @brief   Observa la lista de hábitos almacenados.
     *
     * @details Devuelve un flujo reactivo que se emite de nuevo cada vez que
     *          la tabla **habits** cambia en la base de datos.
     *
     * @return  [Flow] que emite una lista de [HabitEntity].
     */
    @Query("SELECT * FROM habits")
    fun observeAll(): Flow<List<HabitEntity>>

    /**
     * @brief   Inserta o reemplaza un hábito.
     *
     * @param entity Instancia de [HabitEntity] a persistir.
     *               Si existe un registro con el mismo `id`, se reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HabitEntity)

    /**
     * @brief   Elimina un hábito por su identificador.
     *
     * @param id Identificador único del [HabitEntity] que se desea borrar.
     */
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: String)

    /**
     * @brief   Elimina todos los hábitos locales.
     *
     * Se usa normalmente al cerrar sesión o al cambiar de cuenta,
     * para limpiar los datos sincronizados del usuario anterior.
     */
    @Query("DELETE FROM habits")
    suspend fun clear()
}
