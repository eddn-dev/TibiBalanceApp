/**
 * @file HabitFormSaver.kt
 * @ingroup ui_wizard
 * @brief Define un [Saver] personalizado para la clase [HabitForm].
 *
 * @details
 * Este archivo proporciona la implementación de `HabitFormSaver`, un objeto [Saver]
 * que permite que una instancia de [HabitForm] sea guardada y restaurada
 * automáticamente por el mecanismo `rememberSaveable` de Jetpack Compose.
 * Esto es crucial para preservar el estado del formulario del asistente de hábitos
 * a través de cambios de configuración (como rotaciones de pantalla) o
 * interrupciones del proceso de la aplicación.
 *
 * El `HabitFormSaver` utiliza `mapSaver`, lo que significa que el objeto [HabitForm]
 * se serializa a un `Map<String, Any?>` y se deserializa desde él.
 * Cada propiedad relevante de [HabitForm] se mapea a una clave en el mapa.
 * Durante la restauración, se manejan los valores nulos y se proporcionan
 * valores por defecto apropiados para asegurar la reconstrucción correcta del objeto.
 *
 * Los tipos de datos Enum ([HabitCategory], [SessionUnit], [RepeatPreset], [PeriodUnit])
 * se guardan utilizando su propiedad `name` (String) y se restauran usando `Enum.valueOf()`.
 * El conjunto `weekDays` ([Set]<[Int]>) se convierte a un `IntArray` para la serialización.
 *
 * @see Saver
 * @see androidx.compose.runtime.saveable.rememberSaveable
 * @see mapSaver
 * @see HabitForm
 * @see HabitCategory
 * @see SessionUnit
 * @see RepeatPreset
 * @see PeriodUnit
 */
package com.app.tibibalance.ui.wizard.createHabit

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.app.tibibalance.domain.model.*

/**
 * @brief Objeto [Saver] para la clase [HabitForm].
 *
 * Permite que `HabitForm` se utilice con `rememberSaveable` en Jetpack Compose,
 * guardando su estado en un `Map<String, Any?>` y restaurándolo desde él.
 *
 * **Proceso de Serialización (`save`):**
 * Convierte un objeto `HabitForm` en un `Map<String, Any?>`.
 * - Las propiedades de tipo `String`, `Int?`, `Boolean` se guardan directamente.
 * - Las propiedades de tipo Enum se guardan como el `name` (String) del valor Enum.
 * - La propiedad `weekDays` ([Set]<[Int]>) se convierte a `IntArray` si no está vacía.
 *
 * **Proceso de Deserialización (`restore`):**
 * Reconstruye un objeto `HabitForm` a partir de un `Map<String, Any?>`.
 * - Para cada propiedad, intenta leer el valor del mapa, haciendo el casting al tipo esperado.
 * - Si un valor no se encuentra en el mapa o es `null` (y la propiedad no es nullable),
 * se utilizan valores por defecto seguros (e.g., cadena vacía, `false`, Enum por defecto, conjunto vacío).
 * - Los Enums se reconstruyen usando `Enum.valueOf()` con el nombre guardado.
 * - `weekDays` se reconstruye desde `IntArray` a `Set<Int>`.
 */
val HabitFormSaver: Saver<HabitForm, Any> = mapSaver(

    /* -------- SERIALIZAR (HabitForm -> Map) -------- */
    save = { habitFormInstance -> // 'hf' es la instancia de HabitForm a guardar
        buildMap<String, Any?> { // Construye el mapa de forma eficiente
            // Propiedades básicas
            put("name",         habitFormInstance.name)
            put("desc",         habitFormInstance.desc)
            put("category",     habitFormInstance.category.name) // Guarda el nombre del Enum
            put("icon",         habitFormInstance.icon)

            // Propiedades de la sesión
            put("sessionQty",   habitFormInstance.sessionQty) // Int?
            put("sessionUnit",  habitFormInstance.sessionUnit.name) // Guarda el nombre del Enum

            // Propiedades de repetición
            put("repeatPreset", habitFormInstance.repeatPreset.name) // Guarda el nombre del Enum
            if (habitFormInstance.weekDays.isNotEmpty()) { // Guarda weekDays solo si no está vacío
                put("weekDays", habitFormInstance.weekDays.toIntArray()) // Convierte Set<Int> a IntArray
            }

            // Propiedades del periodo total
            put("periodQty",    habitFormInstance.periodQty) // Int?
            put("periodUnit",   habitFormInstance.periodUnit.name) // Guarda el nombre del Enum

            // Banderas booleanas
            put("notify",       habitFormInstance.notify)
            put("challenge",    habitFormInstance.challenge)
        }
    },

    /* -------- DES-SERIALIZAR (Map -> HabitForm) -------- */
    restore = { map -> // 'map' es el Map<String, Any?> recuperado
        HabitForm(
            // Restaura propiedades básicas, con valores por defecto si no se encuentran o son null.
            name         = map["name"]  as? String  ?: "",
            desc         = map["desc"]  as? String  ?: "",
            category     = map["category"]?.let { HabitCategory.valueOf(it as String) }
                ?: HabitCategory.SALUD, // Valor por defecto para Enum
            icon         = map["icon"]  as? String  ?: "FitnessCenter", // Icono por defecto

            // Restaura propiedades de la sesión
            sessionQty   = map["sessionQty"] as? Int, // Nullable Int
            sessionUnit  = map["sessionUnit"]
                ?.let { SessionUnit.valueOf(it as String) }
                ?: SessionUnit.INDEFINIDO, // Valor por defecto para Enum

            // Restaura propiedades de repetición
            repeatPreset = map["repeatPreset"]
                ?.let { RepeatPreset.valueOf(it as String) }
                ?: RepeatPreset.INDEFINIDO, // Valor por defecto para Enum
            weekDays     = (map["weekDays"] as? IntArray)?.toSet() ?: emptySet(), // Convierte IntArray a Set, o vacío

            // Restaura propiedades del periodo total
            periodQty    = map["periodQty"] as? Int, // Nullable Int
            periodUnit   = map["periodUnit"]
                ?.let { PeriodUnit.valueOf(it as String) }
                ?: PeriodUnit.INDEFINIDO, // Valor por defecto para Enum

            // Restaura banderas booleanas
            notify       = map["notify"]   as? Boolean ?: false,
            challenge    = map["challenge"]as? Boolean ?: false
        )
    }
)
