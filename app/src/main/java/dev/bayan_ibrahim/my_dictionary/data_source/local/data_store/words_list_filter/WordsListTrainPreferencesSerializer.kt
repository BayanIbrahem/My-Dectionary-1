package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListTrainPreferencesProto
import java.io.InputStream
import java.io.OutputStream

object WordsListTrainPreferencesSerializer : Serializer<WordsListTrainPreferencesProto> {
    override val defaultValue: WordsListTrainPreferencesProto = WordsListTrainPreferencesProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WordsListTrainPreferencesProto =
        try {
            // readFrom is already called on the data store background thread
            WordsListTrainPreferencesProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: WordsListTrainPreferencesProto, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
