@file:OptIn(ExperimentalSerializationApi::class)

package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWordRelation
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWordTypeTag
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.blankRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.blankRawWordRelation
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.blankRawWordType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.json.MDRawWordTypeTagRelationJsonSerializer.rawWordRelationListSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

private val stringListSerializer: KSerializer<List<String>> = ListSerializer(String.serializer())

data object MDRawWordJsonSerializer : KSerializer<MDRawWord> {
    val rawWordListSerializer: KSerializer<List<MDRawWord>> by lazy {
        ListSerializer(MDRawWordJsonSerializer)
    }
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MDRawWordSerializer") {
        element<String>("meaning")
        element<String>("translation")
        element<String>("language")
        element("additional_translation", stringListSerializer.descriptor)
        element("tags", stringListSerializer.descriptor)
        element<String>("transcription")
        element<String>("examples")
        element("word_type_tag", MDRawWordTypeTagJsonSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): MDRawWord {
        var meaning: String? = null
        var translation: String? = null
        var language: String? = null
        val additionalTranslations: MutableList<String> = mutableListOf()
        val tags: MutableList<String> = mutableListOf()
        var transcription: String? = null
        val examples: MutableList<String> = mutableListOf()
        var wordTypeTag: MDRawWordTypeTag? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = this.decodeElementIndex(descriptor)) {
                    0 -> meaning = decodeStringElement(descriptor, index)
                    1 -> translation = decodeStringElement(descriptor, index)
                    2 -> language = decodeStringElement(descriptor, index)
                    3 -> decodeNullableSerializableElement(
                        descriptor = descriptor,
                        index = index,
                        deserializer = stringListSerializer
                    )?.let(additionalTranslations::addAll)

                    4 -> decodeNullableSerializableElement(
                        descriptor = descriptor,
                        index = index,
                        deserializer = stringListSerializer
                    )?.let(tags::addAll)

                    5 -> transcription = decodeStringElement(descriptor, index)
                    6 -> decodeNullableSerializableElement(
                        descriptor = descriptor,
                        index = index,
                        deserializer = stringListSerializer
                    )?.let(examples::addAll)

                    7 -> {
                        wordTypeTag = decodeNullableSerializableElement(descriptor, index, MDRawWordTypeTagJsonSerializer)
                    }

                    CompositeDecoder.DECODE_DONE -> break
                    else -> break // ignore unexpected tokens
                }
            }
        }
        meaning?.let { m ->
            translation?.let { t ->
                language?.let { l ->
                    return MDRawWord(
                        meaning = m,
                        translation = t,
                        language = l,
                        additionalTranslations = additionalTranslations,
                        examples = examples,
                        tags = tags,
                        transcription = transcription ?: "",
                        wordTypeTag = wordTypeTag
                    )
                }
            }
        }
        return blankRawWord
    }

    override fun serialize(encoder: Encoder, value: MDRawWord) {
        if (!value.isBlank) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.meaning)
                encodeStringElement(descriptor, 1, value.translation)
                encodeStringElement(descriptor, 2, value.language)
                if (value.additionalTranslations.isNotEmpty()) {
                    encodeSerializableElement(
                        descriptor = descriptor,
                        index = 3,
                        serializer = stringListSerializer,
                        value = value.additionalTranslations
                    )
                }
                if (value.tags.isNotEmpty()) {
                    encodeSerializableElement(
                        descriptor = descriptor,
                        index = 4,
                        serializer = stringListSerializer,
                        value = value.tags
                    )
                }

                if (value.examples.isNotEmpty()) {
                    encodeSerializableElement(
                        descriptor = descriptor,
                        index = 5,
                        serializer = stringListSerializer,
                        value = value.examples
                    )
                }

                if (value.transcription.isNotBlank()) {
                    encodeStringElement(descriptor, 6, value.transcription)
                }
                if (value.wordTypeTag != null) {
                    encodeSerializableElement(
                        descriptor = descriptor,
                        index = 7,
                        serializer = MDRawWordTypeTagJsonSerializer,
                        value = value.wordTypeTag
                    )
                }
            }
        }
    }
}

data object MDRawWordTypeTagJsonSerializer : KSerializer<MDRawWordTypeTag> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MDRawWordTypeTagSerializer") {
        element<String>("name")
        element("relations", rawWordRelationListSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): MDRawWordTypeTag {
        var name: String? = null
        var relations: List<MDRawWordRelation>? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = this.decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> relations = decodeNullableSerializableElement(descriptor, index, rawWordRelationListSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> break // ignore unexpected tokens
                }
            }
        }
        name?.let { n ->
            relations?.let { r ->
                return MDRawWordTypeTag(
                    name = n,
                    relations = r
                )
            }
        }
        return blankRawWordType
    }

    override fun serialize(encoder: Encoder, value: MDRawWordTypeTag) {
        if (!value.isBlank) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(
                    descriptor = descriptor,
                    index = 1,
                    serializer = rawWordRelationListSerializer,
                    value = value.relations
                )
            }
        }
    }
}

data object MDRawWordTypeTagRelationJsonSerializer : KSerializer<MDRawWordRelation> {
    val rawWordRelationListSerializer: KSerializer<List<MDRawWordRelation>> by lazy {
        ListSerializer(MDRawWordTypeTagRelationJsonSerializer)
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MDRawWordTypeTagRelationSerializer") {
        element<String>("label")
        element<String>("related_word")
    }

    override fun deserialize(decoder: Decoder): MDRawWordRelation {
        var label: String? = null
        var relatedWord: String? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = this.decodeElementIndex(descriptor)) {
                    0 -> label = decodeStringElement(descriptor, index)
                    1 -> relatedWord = decodeStringElement(descriptor, index)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> break // ignore unexpected tokens
                }
            }
        }
        label?.let { l ->
            relatedWord?.let { w ->
                return MDRawWordRelation(label = l, relatedWord = w)
            }
        }
        return blankRawWordRelation
    }

    override fun serialize(encoder: Encoder, value: MDRawWordRelation) {
        if (!value.isBlank) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.label)
                encodeStringElement(descriptor, 1, value.relatedWord)
            }
        }
    }
}
