package dev.bayan_ibrahim.my_dictionary.data_source.local.data_store.words_list_filter

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dev.bayan_ibrahim.my_dictionary.data_source.local.proto.model.WordsListPreferencesProto
import java.io.InputStream
import java.io.OutputStream

object WordsListViewPreferencesSerializer : Serializer<WordsListPreferencesProto> {
    override val defaultValue: WordsListPreferencesProto = WordsListPreferencesProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WordsListPreferencesProto =
        try {
            // readFrom is already called on the data store background thread
            WordsListPreferencesProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: WordsListPreferencesProto, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
