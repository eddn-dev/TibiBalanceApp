// data/serialization/InstantEpochMillisSerializer.kt
package com.app.tibibalance.data.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.datetime.Instant

/**
 * @file    InstantEpochMillisSerializer.kt
 * @ingroup data_serialization
 * @brief   Serializador de **Instant** basado en epoch-millis.
 *
 * Kotlinx Serialization no provee soporte directo para
 * <code>kotlinx.datetime.Instant</code>.
 * Este objeto anota la clase con <code>@Serializer</code> y convierte
 * el instante a un <code>Long</code> de milisegundos desde la época
 * (<i>Unix epoch</i>) para persistirlo en JSON.
 *
 * Se usa mediante:
 * ```kotlin
 * @Serializable
 * data class Event(
 *     @Serializable(with = InstantEpochMillisSerializer::class)
 *     val timestamp: Instant
 * )
 * ```
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Instant::class)
object InstantEpochMillisSerializer : KSerializer<Instant> {

    /** Descriptor primitivo <code>LONG</code> usado en la codificación. */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("EpochMillisInstant", PrimitiveKind.LONG)

    /**
     * @brief Convierte <code>Instant</code> → <code>Long</code> epoch-millis.
     * @param encoder Codificador proporcionado por Kotlinx.
     * @param value   Instante a serializar.
     */
    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeLong(value.toEpochMilliseconds())

    /**
     * @brief Convierte <code>Long</code> epoch-millis → <code>Instant</code>.
     * @param decoder Decodificador de Kotlinx.
     * @return        Instancia <code>Instant</code> resultante.
     */
    override fun deserialize(decoder: Decoder): Instant =
        Instant.fromEpochMilliseconds(decoder.decodeLong())
}
