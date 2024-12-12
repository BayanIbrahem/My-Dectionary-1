package dev.bayan_ibrahim.my_dictionary.domain.model.language

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LanguageCodeSerializer : KSerializer<LanguageCode> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "LanguageCodeSerializer",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): LanguageCode = decoder.decodeString().code

    override fun serialize(encoder: Encoder, value: LanguageCode) {
        encoder.encodeString(value.code)
    }
}