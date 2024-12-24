package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json

import android.util.JsonReader
import android.util.JsonToken
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.MDFileReaderFactory
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_reader.MDJsonFileReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_reader.MDJsonFileReaderV1
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.roundToInt

class MDJsonFileReaderFactory(
    val json: Json,
    private val openInputStreamCallback: (fileData: MDFileData) -> InputStream,
) : MDFileReaderFactory<MDJsonFileReader> {
    override val availableVersions: List<Int> = listOf(1)
    override val name: String = "Json Factory"
    override suspend fun openInputStream(
        fileData: MDFileData,
    ): InputStream = openInputStreamCallback(fileData)

    override suspend fun getVersion(fileData: MDFileData): Int {
        return getValueOfKey(
            key = VERSION_KEY,
            jsonInputStream = openInputStream(fileData),
            deepestLevel = 0
        )?.toDoubleOrNull()?.roundToInt() ?: throw IllegalArgumentException(
            "Can not find key '$VERSION_KEY' in file ${fileData.name} (${fileData.uri}) at top level"
        )
    }

    override suspend fun buildReaderForVersion(version: Int): MDJsonFileReaderV1 {
        return when (version) {
            1 -> MDJsonFileReaderV1(
                json = json,
                openInputStreamCallback = openInputStreamCallback
            )

            else -> throw IllegalArgumentException("invalid version $version for json file reader") // TODO custom callback

        }
    }

    private fun getValueOfKey(
        key: String,
        jsonInputStream: InputStream,
        deepestLevel: Int = Int.MAX_VALUE,
    ): String? {
        JsonReader(InputStreamReader(jsonInputStream)).use { jsonReader ->
            return searchKeyInJson(
                jsonReader = jsonReader,
                key = key,
                currentLevel = 0,
                deepestLevel = deepestLevel
            )
        }
    }

    private fun searchKeyInJson(
        jsonReader: JsonReader,
        key: String,
        currentLevel: Int,
        deepestLevel: Int,
    ): String? {
        if (currentLevel > deepestLevel) return null

        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            val name = jsonReader.nextName()
            if (name == key) {
                return when (jsonReader.peek()) {
                    JsonToken.STRING -> jsonReader.nextString()
                    JsonToken.NUMBER -> jsonReader.nextDouble().toString()
                    JsonToken.BOOLEAN -> jsonReader.nextBoolean().toString()
                    JsonToken.NULL -> {
                        jsonReader.nextNull()
                        null
                    }

                    else -> {
                        jsonReader.skipValue()
                        null
                    }
                }
            }

            when (jsonReader.peek()) {
                JsonToken.BEGIN_OBJECT -> {
                    val value = searchKeyInJson(jsonReader, key, currentLevel + 1, deepestLevel)
                    if (value != null) return value
                }

                JsonToken.BEGIN_ARRAY -> {
                    jsonReader.beginArray()
                    while (jsonReader.hasNext()) {
                        if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                            val value = searchKeyInJson(jsonReader, key, currentLevel + 1, deepestLevel)
                            if (value != null) return value
                        } else {
                            jsonReader.skipValue()
                        }
                    }
                    jsonReader.endArray()
                }

                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()
        return null
    }

    companion object Companion {
        const val VERSION_KEY = "version"
    }
}