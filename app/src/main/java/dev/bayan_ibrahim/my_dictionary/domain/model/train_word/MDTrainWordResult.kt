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
import kotlin.time.Duration

@Serializable(TrainWordResultSerializer::class)
sealed interface MDTrainWordResult {
    val consumedDuration: Duration

    val type: TrainWordResultType
        get() = when (this) {
            is Wrong -> Wrong.type
            is Right -> Right.type
            is Pass -> Pass.type
            is Timeout -> Timeout.type
        }

    @Serializable
    data class Wrong(
        val selectedAnswer: String,
        val correctAnswer: String,
        override val consumedDuration: Duration,
    ) : MDTrainWordResult {
        companion object Companion {
            val type = TrainWordResultType.Wrong
        }
    }

    @Serializable
    data class Right(
        override val consumedDuration: Duration,
        val correctAnswer: String,
    ) : MDTrainWordResult {
        companion object Companion {
            val type = TrainWordResultType.Right
        }
    }

    @Serializable
    data class Pass(
        override val consumedDuration: Duration,
    ) : MDTrainWordResult {
        companion object Companion {
            val type = TrainWordResultType.Pass
        }
    }

    @Serializable
    data class Timeout(
        override val consumedDuration: Duration,
    ) : MDTrainWordResult {
        companion object Companion {
            val type = TrainWordResultType.Timeout
        }
    }
}

data object TrainWordResultSerializer : KSerializer<MDTrainWordResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TrainWordResultSerializer") {
        element("wrong", MDTrainWordResult.Wrong.serializer().descriptor)
        element("right", MDTrainWordResult.Right.serializer().descriptor)
        element("pass", MDTrainWordResult.Pass.serializer().descriptor)
        element("timeout", MDTrainWordResult.Timeout.serializer().descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): MDTrainWordResult {
        var result: MDTrainWordResult? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> {
                        val wrong = decodeNullableSerializableElement(descriptor, index, MDTrainWordResult.Wrong.serializer())
                        if (wrong != null) {
                            result = wrong
                            break
                        }
                    }

                    1 -> {
                        val right = decodeNullableSerializableElement(descriptor, index, MDTrainWordResult.Right.serializer())
                        if (right != null) {
                            result = right
                            break
                        }
                    }

                    2 -> {
                        val pass = decodeNullableSerializableElement(
                            descriptor,
                            index,
                            MDTrainWordResult.Pass.serializer()
                        )
                        if (pass != null) {
                            result = pass
                            break
                        }
                    }

                    3 -> {
                        val timeout = decodeNullableSerializableElement(descriptor, index, MDTrainWordResult.Timeout.serializer())
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

    override fun serialize(encoder: Encoder, value: MDTrainWordResult) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is MDTrainWordResult.Wrong -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 0,
                        serializer = MDTrainWordResult.Wrong.serializer(),
                        value = value
                    )
                }

                is MDTrainWordResult.Right -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 1,
                        serializer = MDTrainWordResult.Right.serializer(),
                        value = value
                    )
                }

                is MDTrainWordResult.Pass -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 2,
                        serializer = MDTrainWordResult.Pass.serializer(),
                        value = value
                    )
                }

                is MDTrainWordResult.Timeout -> {
                    this.encodeSerializableElement(
                        descriptor = descriptor,
                        index = 3,
                        serializer = MDTrainWordResult.Timeout.serializer(),
                        value = value
                    )
                }

            }
        }
    }

}