/**
 * @file    HabitTemplateDao.kt
 * @ingroup data_local
 * @brief   DAO para la tabla **habit_templates**.
 *
 * Gestiona las operaciones CRUD de las plantillas de hábitos almacenadas
 * localmente con Room. Mantiene un flujo reactivo para que la UI se actualice
 * automáticamente cuando cambian los datos.
 */
package com.app.tibibalance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.app.tibibalance.data.local.entity.HabitTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * @brief DAO (Data Access Object) para la entidad [HabitTemplateEntity].
 * @details Define las operaciones de consulta, inserción/actualización y borrado
 * para la tabla `habit_templates` en la base de datos Room.
 */
@Dao
interface HabitTemplateDao {

    /**
     * @brief   Observa todas las plantillas de hábitos.
     *
     * @details Devuelve un flujo que emite la lista ordenada por
     * `category` y luego `name` cada vez que la tabla se modifica.
     *
     * @return  [Flow] que emite una lista de [HabitTemplateEntity].
     */
    @Query("SELECT * FROM habit_templates ORDER BY category, name")
    fun observeAll(): Flow<List<HabitTemplateEntity>>

    /**
     * @brief Inserta o actualiza una colección de plantillas.
     *
     * @param templates Lista de [HabitTemplateEntity] a persistir.
     * Los registros existentes con la misma `id` se reemplazan
     * gracias a la estrategia definida por `@Upsert`.
     */
    @Upsert
    suspend fun upsert(templates: List<HabitTemplateEntity>)

    /**
     * @brief   Elimina todas las plantillas de la base local.
     * @details Se usa, por ejemplo, para forzar una recarga completa desde Firestore
     * o al limpiar datos de usuario.
     */
    @Query("DELETE FROM habit_templates")
    suspend fun clear()
}
