/**
 * @file    HabitFirestoreMapper.kt
 * @ingroup data_remote_mapper // Grupo específico para mappers remotos
 * @brief   Funciones de extensión para la conversión bidireccional entre el modelo de dominio [Habit]
 * y la representación de datos compatible con Firebase Firestore.
 *
 * @details Este mapeador utiliza una estrategia de serialización JSON intermedia para manejar
 * la complejidad del objeto [Habit], que contiene tipos anidados y tipos no soportados
 * directamente por Firestore (como [kotlinx.datetime.Instant]).
 *
 * <h4>Flujo de Conversión</h4>
 * <ol>
 * <li><b>Dominio ([Habit]) ➜ Firestore (`Map<String, Any?>`)</b><br/>
 * El objeto [Habit] se serializa primero a un [kotlinx.serialization.json.JsonElement]
 * usando Kotlinx Serialization. Luego, este `JsonElement` se convierte recursivamente
 * (usando el helper `toFirestoreAny`) a una estructura de `Map` y `List` que solo contiene
 * tipos primitivos soportados por Firestore (`String`, `Long`, `Double`, `Boolean`, `null`,
 * `Map`, `List`). Finalmente, los campos específicos de tipo [kotlinx.datetime.Instant]
 * (`createdAt`, `nextTrigger`, `challengeFrom`, `challengeTo`) en el mapa resultante
 * se reemplazan explícitamente por su representación como milisegundos desde la época (epoch-ms, `Long`).</li>
 * <li><b>Firestore (`DocumentSnapshot`) ➜ Dominio ([Habit])</b><br/>
 * Los datos del [DocumentSnapshot] (`snapshot.data`) se convierten recursivamente
 * (usando el helper `toJsonElement`) de nuevo a un [kotlinx.serialization.json.JsonObject].
 * Este `JsonObject` se deserializa a una instancia base de [Habit] usando Kotlinx Serialization.
 * Finalmente, los campos `Instant` (`createdAt`, `nextTrigger`, etc.) se restauran
 * leyendo los valores `Long` (epoch-ms) correspondientes directamente del `snapshot.data`
 * original y convirtiéndolos a [kotlinx.datetime.Instant]. El `id` del [Habit] se toma
 * del `snapshot.id`.</li>
 * </ol>
 *
 * Este enfoque asegura que toda la información del hábito se pueda almacenar y recuperar
 * de Firestore, manteniendo la compatibilidad con la estrategia *offline-first* (ya que
 * el objeto [Habit] completo se puede serializar/deserializar localmente) y permitiendo
 * la sincronización LWW (Last-Write-Wins) sin pérdida de precisión en las fechas.
 *
 * @see com.app.tibibalance.domain.model.Habit Modelo de dominio.
 * @see com.app.tibibalance.data.local.mapper.HabitLocalMapper Mapeador para la persistencia local en Room (que también usa JSON).
 * @see com.app.tibibalance.data.serialization.InstantEpochMillisSerializer Serializador KTX usado internamente por `Json.encodeToJsonElement`.
 */
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString // Necesario para encodeToString si se usara directamente
import kotlinx.serialization.decodeFromString // Necesario para decodeFromString si se usara directamente
import kotlinx.serialization.json.Json

/* ------------------------------------------------------------------ */
/* Dominio ([Habit]) → Firestore (Map)          */
/* ------------------------------------------------------------------ */

/**
 * @brief   Convierte un objeto [Habit] del dominio a un [Map] compatible con Firestore.
 *
 * @details Serializa el [Habit] a JSON, convierte la estructura JSON a tipos nativos de Firestore,
 * y finalmente reemplaza los campos [Instant] por su representación en milisegundos epoch (`Long`).
 *
 * @receiver La instancia del modelo de dominio [Habit] que se va a guardar en Firestore.
 * @return   Un [Map]<[String], [Any]?> que representa el hábito con tipos de datos
 * compatibles con Firestore. Los campos `Instant?` nulos se representarán como `null` en el mapa.
 */
fun Habit.toFirestoreMap(): Map<String, Any?> {
    // 1. Serializar el objeto Habit completo a un JsonElement usando Kotlinx Serialization.
    //    Esto utiliza implícitamente los serializadores definidos, incluyendo InstantEpochMillisSerializer.
    val jsonElement = Json.encodeToJsonElement(this)

    // 2. Convertir recursivamente el JsonElement a una estructura de Map/List con tipos primitivos
    //    compatibles con Firestore usando el helper `toFirestoreAny`. Se espera que el resultado sea un Map.
    val firestoreCompatibleMap = jsonElement.toFirestoreAny() as? Map<*, *>
        ?: throw IllegalStateException("La serialización de Habit no produjo un mapa raíz.")

    // 3. Crear un mapa mutable y reemplazar explícitamente los campos Instant (que KTX serializó a Long)
    //    para asegurar que Firestore los reciba como Long (epoch ms), aunque `toFirestoreAny` ya debería hacerlo.
    //    Esto también maneja los campos Instant que son nullable.
    return firestoreCompatibleMap.toMutableMap().apply {
        this["createdAt"]     = createdAt.toEpochMilliseconds() // Siempre presente
        this["nextTrigger"]   = nextTrigger?.toEpochMilliseconds() // Puede ser null
        this["challengeFrom"] = challengeFrom?.toEpochMilliseconds() // Puede ser null
        this["challengeTo"]   = challengeTo?.toEpochMilliseconds() // Puede ser null
    } as Map<String, Any?> // Cast final para asegurar el tipo de retorno
}

/* ------------------------------------------------------------------ */
/* Firestore (DocumentSnapshot) → Dominio ([Habit])      */
/* ------------------------------------------------------------------ */

/**
 * @brief   Reconstruye un objeto [Habit] del dominio a partir de un [DocumentSnapshot] de Firestore.
 *
 * @details Convierte los datos del `DocumentSnapshot` a una estructura JSON, deserializa esta
 * estructura a un objeto [Habit] base, y luego restaura específicamente los campos de tipo [Instant]
 * leyendo los valores `Long` (epoch-ms) del mapa de datos original del snapshot.
 *
 * @receiver El [DocumentSnapshot] recuperado de la colección `habits` en Firestore.
 * @return   La instancia del modelo de dominio [Habit] completamente reconstruida, incluyendo
 * su `id` (tomado del `snapshot.id`) y los campos [Instant] correctamente parseados.
 * @throws kotlinx.serialization.SerializationException Si la estructura JSON reconstruida no coincide
 * con el esquema esperado por el deserializador de [Habit].
 * @throws ClassCastException Si los campos de timestamp en Firestore no son de tipo `Number`.
 */
fun DocumentSnapshot.toHabit(): Habit {
    // Función helper interna para convertir valores de Firestore a JsonElement
    /** Convierte recursivamente Any? de Firestore a JsonElement */
    fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this) // Firestore Numbers (Long, Double) -> JsonPrimitive Number
        is Boolean -> JsonPrimitive(this)
        is Map<*, *> -> buildJsonObject { // Firestore Map -> JsonObject
            // Itera sobre el mapa de Firestore
            this@toJsonElement.forEach { (key, value) ->
                // Asegura que la clave sea String antes de ponerla en JsonObject
                if (key is String) {
                    put(key, value.toJsonElement()) // Conversión recursiva del valor
                } else {
                    // Opcional: Loguear una advertencia si la clave no es String
                    // Log.w("Mapper", "Clave no String encontrada en mapa Firestore: $key")
                }
            }
        }
        is List<*> -> buildJsonArray { // Firestore List -> JsonArray
            this@toJsonElement.forEach { element ->
                add(element.toJsonElement()) // Conversión recursiva de cada elemento
            }
        }
        // Fallback: Intenta convertir a String. Si falla, devuelve JsonNull para evitar crash.
        else -> try { JsonPrimitive(this.toString()) } catch (_: Exception) { JsonNull }
    }

    // 1. Obtener el mapa de datos del snapshot. Usar emptyMap si data es null.
    val dataMap: Map<String, Any?> = data ?: emptyMap()

    // 2. Reconstruir un JsonObject a partir del dataMap.
    val jsonObject = buildJsonObject {
        dataMap.forEach { (key, value) ->
            put(key, value.toJsonElement())
        }
    }

    // 3. Deserializar el JsonObject a un objeto Habit base.
    //    Esto utiliza los serializadores KTX, incluyendo el de Instant (que espera Long).
    //    Los campos Instant en 'base' tendrán valores potencialmente incorrectos si
    //    no se sobrescriben en el paso 5.
    val base: Habit = Json.decodeFromJsonElement(jsonObject)

    // 4. Extraer los valores Long de los campos timestamp directamente del dataMap original.
    //    Es más seguro leer del dataMap original que del 'base' deserializado por si KTX hizo algo inesperado.
    val createdAtMillis     = dataMap["createdAt"] as? Number // Puede ser Long o Double en Firestore
    val nextTriggerMillis   = dataMap["nextTrigger"] as? Number
    val challengeFromMillis = dataMap["challengeFrom"] as? Number
    val challengeToMillis   = dataMap["challengeTo"] as? Number

    // 5. Crear la instancia final copiando 'base' y sobrescribiendo los campos Instant
    //    con los valores correctos parseados desde los Longs (epoch ms).
    return base.copy(
        id            = this.id, // El ID viene del DocumentSnapshot, no del dataMap
        createdAt     = createdAtMillis?.toLong()?.let { Instant.fromEpochMilliseconds(it) }
            ?: base.createdAt, // Fallback al valor deserializado si no se encontró el Long
        nextTrigger   = nextTriggerMillis?.toLong()?.let { Instant.fromEpochMilliseconds(it) }, // Null si no existe
        challengeFrom = challengeFromMillis?.toLong()?.let { Instant.fromEpochMilliseconds(it) }, // Null si no existe
        challengeTo   = challengeToMillis?.toLong()?.let { Instant.fromEpochMilliseconds(it) }  // Null si no existe
    )
}


/* ------------------------------------------------------------------ */
/* Helper Privado de Conversión                 */
/* ------------------------------------------------------------------ */

/**
 * @brief   Convierte recursivamente un [JsonElement] a un tipo de dato compatible con Firestore.
 *
 * @details Esta función privada es utilizada por `Habit.toFirestoreMap` para transformar la
 * estructura JSON serializada del objeto [Habit] en un `Map<String, Any?>` que
 * Firestore pueda almacenar. Maneja primitivas, objetos anidados (convirtiéndolos
 * a mapas) y arrays (convirtiéndolos a listas).
 *
 * - `JsonPrimitive` se convierte al tipo primitivo subyacente (`String`, `Long`, `Double`, `Boolean`).
 * - `JsonObject` se convierte a `Map<String, Any?>` aplicando esta función recursivamente a sus valores.
 * - `JsonArray` se convierte a `List<Any?>` aplicando esta función recursivamente a sus elementos.
 * - `JsonNull` se convierte a `null`.
 *
 * @receiver El [JsonElement] a convertir.
 * @return   Un objeto (`String`, `Long`, `Double`, `Boolean`, `Map<String, Any?>`, `List<Any?>`, o `null`)
 * que representa el `JsonElement` en un formato almacenable por Firestore.
 */
private fun JsonElement.toFirestoreAny(): Any? = when (this) {
    is JsonNull -> null // Null JSON -> null Kotlin
    is JsonPrimitive -> when { // Primitivas JSON
        this.isString -> this.content // String -> String
        this.booleanOrNull != null -> this.boolean // Boolean -> Boolean
        // Importante: KTX puede serializar Int como Long. Firestore maneja ambos como Number.
        this.longOrNull != null -> this.long // Long -> Long
        this.doubleOrNull != null -> this.double // Double -> Double
        // Fallback por si es un tipo primitivo no reconocido (aunque no debería pasar con KTX)
        else -> this.content
    }
    // Objeto JSON -> Map<String, Any?> recursivo
    is JsonObject -> this.mapValues { (_, jsonValue) -> jsonValue.toFirestoreAny() }
    // Array JSON -> List<Any?> recursivo
    is JsonArray -> this.map { jsonElement -> jsonElement.toFirestoreAny() }
}
