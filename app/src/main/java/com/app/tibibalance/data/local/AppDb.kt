/**
 * @file    AppDb.kt
 * @ingroup data_local_db // Grupo específico para la definición de la BD
 * @brief   Definición de la base de datos Room principal para la aplicación TibiBalance.
 *
 * @details Esta clase abstracta, anotada con `@Database`, configura la estructura
 * de la base de datos local utilizando Room Persistence Library. Define las
 * entidades que componen la base de datos, la versión del esquema, y registra
 * los [TypeConverter] necesarios para manejar tipos de datos no soportados
 * nativamente por SQLite.
 *
 * - **Entidades Incluidas:**
 * - [UserProfileEntity]: Almacena el perfil del usuario actual.
 * - [HabitEntity]: Almacena los hábitos del usuario (serializados como JSON).
 * - [HabitTemplateEntity]: Almacena las plantillas de hábitos predefinidas.
 * - **TypeConverters Registrados:**
 * - [HabitConverter]: Maneja la serialización/deserialización del objeto [com.app.tibibalance.domain.model.Habit]
 * y tipos de `kotlinx.datetime` como [kotlinx.datetime.Instant] y [kotlinx.datetime.LocalDate].
 * - [NotifConverters]: Maneja la conversión de `List<String>` y `List<Int>`
 * usadas en [com.app.tibibalance.domain.model.NotifConfig] (dentro de [HabitTemplateEntity])
 * a/desde `String`.
 *
 * La **versión 3** del esquema refleja la inclusión de los campos para listas en las notificaciones.
 * Se habilita `exportSchema = true` para generar el historial del esquema en la carpeta `schemas/`,
 * lo cual es crucial para planificar y verificar migraciones de base de datos en futuras versiones
 * y para la integración continua (CI).
 */
package com.app.tibibalance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.tibibalance.data.local.converter.HabitConverter
import com.app.tibibalance.data.local.dao.*
import com.app.tibibalance.data.local.entity.*
import com.app.tibibalance.data.local.mapper.NotifConverters

/**
 * @brief Subclase abstracta de [RoomDatabase] que define la base de datos de la aplicación.
 * @details Proporciona acceso a los Data Access Objects (DAOs) necesarios para interactuar
 * con las tablas de la base de datos. Room genera automáticamente la implementación
 * concreta de esta clase y sus métodos DAO en tiempo de compilación.
 *
 * @see DatabaseModule Módulo Hilt que provee la instancia singleton de esta base de datos.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitTemplateEntity::class
    ],
    version = 4, // Incrementa si cambias el esquema
    exportSchema = true // Recomendado para control de versiones del esquema
)
@TypeConverters(
    HabitConverter::class,
    NotifConverters::class // Registra el nuevo convertidor
)
abstract class AppDb : RoomDatabase() {

    /**
     * @brief Proporciona acceso al Data Access Object (DAO) para la entidad [UserProfileEntity].
     * @return Una instancia de [ProfileDao] generada por Room.
     */
    abstract fun profileDao(): ProfileDao

    /**
     * @brief Proporciona acceso al Data Access Object (DAO) para la entidad [HabitEntity].
     * @return Una instancia de [HabitDao] generada por Room.
     */
    abstract fun habitDao(): HabitDao

    /**
     * @brief Proporciona acceso al Data Access Object (DAO) para la entidad [HabitTemplateEntity].
     * @return Una instancia de [HabitTemplateDao] generada por Room.
     */
    abstract fun habitTemplateDao(): HabitTemplateDao
}
