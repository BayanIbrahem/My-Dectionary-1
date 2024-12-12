package dev.bayan_ibrahim.my_dictionary.domain.model.train_word

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(TrainWordResultSerializer::class)
sealed interface TrainWordResult {
//    val key: Int

    val type: TrainWordResultType
        get() = when (this) {
            is Fail -> Fail.type
            Pass -> Pass.type
            Timeout -> Timeout.type
        }

    @Serializable
    data class Fail(
        val selectedAnswer: String,
        val currentAnswer: String,
    ) : TrainWordResult {
        companion object Companion {
            val type = TrainWordResultType.Fail
        }
    }

    @Serializable
    data object Pass : TrainWordResult {
        override val type = TrainWordResultType.Pass
    }

    @Serializable
    data object Timeout : TrainWordResult {
        override val type = TrainWordResultType.Timeout
    }
}

data object TrainWordResultSerializer : KSerializer<TrainWordResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TrainWordResultSerializer") {
        element("fail", TrainWordResult.Fail.serializer().descriptor)
        element("pass", TrainWordResult.Pass.serializer().descriptor)
        element("timeout", TrainWordResult.Timeout.serializer().descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): TrainWordResult {
        var result: TrainWordResult? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> {
                        val fail = decodeNullableSerializableElement(descriptor, index, TrainWordResult.Fail.serializer())
                        if (fail != null) {
                            result = fail
                            break
                        }
                    }

                    1 -> {
                        val pass = decodeNullableSerializableElement(descriptor, index, TrainWordResult.Pass.serializer())
                        if (pass != null) {
                            result = pass
                            break
                        }
                    }

                    2 -> {
                        val timeout = decodeNullableSerializableElement(descriptor, index, TrainWordResult.Timeout.serializer())
                        if (timeout != null) {
                            result = timeout
                            break
                        }
                    }

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("unexpected index $index")
                }
            }
        }
        return result!!
    }

    override fun serialize(encoder: Encoder, value: TrainWordResult) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is TrainWordResult.Fail -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 0,
                        serializer = TrainWordResult.Fail.serializer(),
                        value = value
                    )
                }

                TrainWordResult.Pass -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 1,
                        serializer = TrainWordResult.Pass.serializer(),
                        value = TrainWordResult.Pass
                    )
                }

                TrainWordResult.Timeout -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 2,
                        serializer = TrainWordResult.Timeout.serializer(),
                        value = TrainWordResult.Timeout
                    )
                }
            }
        }
    }

}