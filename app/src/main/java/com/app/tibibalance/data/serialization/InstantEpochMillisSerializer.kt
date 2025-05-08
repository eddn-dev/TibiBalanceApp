/**
 * @file    InstantEpochMillisSerializer.kt
 * @ingroup data_serializer // Grupo específico para Serializers KTX
 * @brief   Serializador personalizado para [kotlinx.datetime.Instant] utilizando milisegundos desde la época (epoch-millis).
 *
 * @details La librería `kotlinx.serialization` no incluye soporte nativo para serializar/deserializar
 * el tipo [kotlinx.datetime.Instant]. Este objeto implementa la interfaz [KSerializer]
 * para proporcionar dicha funcionalidad, convirtiendo un [Instant] a un primitivo `Long`
 * (representando los milisegundos desde la época Unix) durante la serialización, y realizando
 * la conversión inversa durante la deserialización.
 *
 * Esto permite anotar propiedades de tipo [Instant] en clases `@Serializable` para que
 * puedan ser (des)serializadas correctamente, por ejemplo, a formato JSON.
 *
 * **Uso:**
 * ```kotlin
 * import kotlinx.serialization.Serializable
 * import kotlinx.datetime.Instant
 *
 * @Serializable
 * data class MyData(
 * val id: String,
 * @Serializable(with = InstantEpochMillisSerializer::class) // <-- Aplicar aquí
 * val creationTime: Instant
 * )
 * ```
 * @see kotlinx.datetime.Instant
 * @see kotlinx.serialization.KSerializer
 * @see kotlinx.serialization.Serializable
 */
package com.app.tibibalance.data.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.datetime.Instant

/**
 * @brief Objeto singleton que implementa [KSerializer] para [Instant], usando epoch-millis (`Long`).
 */
@OptIn(ExperimentalSerializationApi::class) // Necesario para @Serializer
@Serializer(forClass = Instant::class) // Indica a KTX para qué clase es este serializer
object InstantEpochMillisSerializer : KSerializer<Instant> {

    /**
     * @brief Describe la estructura serializada, que es un primitivo `Long`.
     * @details Define el descriptor como un [PrimitiveSerialDescriptor] de tipo [PrimitiveKind.LONG],
     * indicando que el [Instant] se representa como un único valor largo.
     */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("EpochMillisInstant", PrimitiveKind.LONG)

    /**
     * @brief Serializa un objeto [Instant] a su representación `Long` (epoch-millis).
     * @param encoder El [Encoder] proporcionado por Kotlinx Serialization para escribir el valor serializado.
     * @param value   La instancia de [Instant] que se va a serializar.
     */
    override fun serialize(encoder: Encoder, value: Instant): Unit =
        // Convierte el Instant a milisegundos desde la época y lo codifica como Long
        encoder.encodeLong(value.toEpochMilliseconds())

    /**
     * @brief Deserializa un valor `Long` (epoch-millis) a un objeto [Instant].
     * @param decoder El [Decoder] proporcionado por Kotlinx Serialization para leer el valor serializado.
     * @return La instancia de [Instant] reconstruida a partir del valor Long leído.
     * @throws SerializationException Si el valor leído del [decoder] no es un `Long` válido
     * (aunque KTX suele manejar esto internamente según el formato).
     */
    override fun deserialize(decoder: Decoder): Instant =
        // Lee el valor Long del decoder y lo convierte de nuevo a Instant
        Instant.fromEpochMilliseconds(decoder.decodeLong())
}
