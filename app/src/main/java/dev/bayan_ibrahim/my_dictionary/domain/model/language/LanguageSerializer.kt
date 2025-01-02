package dev.bayan_ibrahim.my_dictionary.domain.model.language

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LanguageSerializer : KSerializer<Language> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "LanguageSerializer",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Language = decoder.decodeString().code.getLanguage()

    override fun serialize(encoder: Encoder, value: Language) {
        encoder.encodeString(value.code)
    }
}