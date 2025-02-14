package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListViewPreferencesProto
import java.io.InputStream
import java.io.OutputStream

object WordsListViewPreferencesSerializer : Serializer<WordsListViewPreferencesProto> {
    override val defaultValue: WordsListViewPreferencesProto = WordsListViewPreferencesProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WordsListViewPreferencesProto =
        try {
            // readFrom is already called on the data store background thread
            WordsListViewPreferencesProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: WordsListViewPreferencesProto, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
