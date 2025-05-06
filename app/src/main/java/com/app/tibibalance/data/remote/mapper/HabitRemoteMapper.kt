// data/remote/mapper/HabitFirestoreMapper.kt
package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/* ------------------------------------------------------------------ */
/*                       Dominio  →  Firestore                        */
/* ------------------------------------------------------------------ */

fun Habit.toFirestoreMap(): Map<String, Any?> {
    // 1) serializamos el modelo completo a JsonElement
    val json = Json.encodeToJsonElement(this)

    // 2) convertimos recursivamente ese Json a tipos Firestore-friendly
    val root = json.toFirestoreAny() as Map<*, *>

    // 3) sustituimos timestamps por epoch-ms
    return root.toMutableMap().apply {
        this["createdAt"]   = createdAt.toEpochMilliseconds()
        this["nextTrigger"] = nextTrigger?.toEpochMilliseconds()
    } as Map<String, Any?>
}

/* ------------------------------------------------------------------ */
/*                       Firestore  →  Dominio                        */
/* ------------------------------------------------------------------ */

fun DocumentSnapshot.toHabit(): Habit {
    /** Convierte recursivamente Any? de Firestore a JsonElement */
    fun Any?.toJsonElement(): JsonElement = when (this) {
        null              -> JsonNull
        is String         -> JsonPrimitive(this)
        is Number         -> JsonPrimitive(this)
        is Boolean        -> JsonPrimitive(this)
        is Map<*, *>      -> buildJsonObject {
            for ((k, v) in this@toJsonElement) {
                put(k as String, v.toJsonElement())
            }
        }
        is List<*>        -> buildJsonArray { this@toJsonElement.forEach { add(it.toJsonElement()) } }
        else              -> JsonPrimitive(this.toString()) // fallback (no debería ocurrir)
    }

    // 1) reconstruimos un JsonObject a partir del map que nos da Firestore
    val dataMap = data ?: emptyMap<String, Any?>()
    val jsonObj = buildJsonObject {
        dataMap.forEach { (k, v) -> put(k, v.toJsonElement()) }
    }

    // 2) deserializamos al modelo base
    val base: Habit = Json.decodeFromJsonElement(jsonObj)

    // 3) restauramos los timestamps
    val created = (dataMap["createdAt"]   as? Number)?.toLong()
    val next    = (dataMap["nextTrigger"] as? Number)?.toLong()

    return base.copy(
        id          = id,
        createdAt   = created?.let { Instant.fromEpochMilliseconds(it) } ?: base.createdAt,
        nextTrigger = next?.let    { Instant.fromEpochMilliseconds(it) } ?: base.nextTrigger
    )
}

/* ------------------------------------------------------------------ */
/*                       Helpers de conversión                        */
/* ------------------------------------------------------------------ */

/** Convierte un JsonElement a un valor aceptado por Firestore:
 *  * JsonPrimitive → String / Long / Double / Boolean
 *  * JsonObject    → Map<String, Any?> (recursivo)
 *  * JsonArray     → List<Any?> (recursivo)                           */
private fun JsonElement.toFirestoreAny(): Any? = when (this) {
    is JsonNull      -> null
    is JsonPrimitive -> when {
        isString        -> content
        booleanOrNull != null -> boolean
        longOrNull    != null -> long
        doubleOrNull  != null -> double
        else                -> content
    }
    is JsonObject    -> mapValues { (_, v) -> v.toFirestoreAny() }
    is JsonArray     -> map { it.toFirestoreAny() }
}
