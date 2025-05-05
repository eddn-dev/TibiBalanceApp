package com.app.tibibalance.data.remote.mapper

import com.app.tibibalance.domain.model.Habit
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/* ---------- Dominio → Map<String, Any?> ---------- */
fun Habit.toFirestoreMap(): Map<String, Any?> {
    // 1. serializa completo a JsonObject
    val jsonObj = Json.encodeToJsonElement(this).jsonObject

    // 2. crea un mapa mutable a partir del JsonObject
    val mutable: MutableMap<String, JsonElement> = jsonObj.toMutableMap()

    // 3. sustituye timestamps por JsonPrimitive(Long)
    mutable["createdAt"]   = JsonPrimitive(createdAt.toEpochMilliseconds())
    mutable["nextTrigger"] = nextTrigger?.let { JsonPrimitive(it.toEpochMilliseconds()) }
        ?: JsonNull

    // 4. convierte a Map<String, Any?> compatible con Firestore
    return mutable.mapValues { (_, v) ->
        when (v) {
            is JsonPrimitive -> when {
                v.isString   -> v.content
                v.booleanOrNull != null -> v.boolean
                else          -> v.longOrNull ?: v.content
            }
            is JsonNull -> null
            else        -> v.toString()        // objetos anidados como String JSON
        }
    }
}

/* ---------- DocumentSnapshot → Dominio ---------- */
fun DocumentSnapshot.toHabit(): Habit {
    val dataMap = data ?: emptyMap<String, Any?>()

    val created = (dataMap["createdAt"] as? Number)?.toLong()
    val next    = (dataMap["nextTrigger"] as? Number)?.toLong()

    // reconstruye JsonObject desde Map plano
    val jsonObj = buildJsonObject {
        dataMap.forEach { (k, v) -> put(k, Json.encodeToJsonElement(v)) }
    }

    val base = Json.decodeFromJsonElement(Habit.serializer(), jsonObj).copy(
        id = id,
        createdAt   = created?.let { Instant.fromEpochMilliseconds(it) }
            ?: Instant.DISTANT_PAST,
        nextTrigger = next?.let { Instant.fromEpochMilliseconds(it) }
    )
    return base
}
