package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_reader

import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part.MDFilePart
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.core.file_part_reader.MDFilePartReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.core.file_reader.MDJsonFileReader
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part.MDJsonFileLanguagePartV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part.MDJsonFileTagPartV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part.MDJsonFileWordPartV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part_reader.MDJsonFileLanguagePartReaderV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part_reader.MDJsonFileTagPartReaderV1
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.refactor.file_type.json.version.v1.file_part_reader.MDJsonFileWordPartReaderV1
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileData
import kotlinx.serialization.json.Json
import java.io.InputStream

data class MDJsonFileReaderV1(
    val json: Json,
    private val openInputStreamCallback: (data: MDFileData) -> InputStream,
) : MDJsonFileReader(version = 1, json = json) {
    override suspend fun openInputStream(
        data: MDFileData,
    ): InputStream = openInputStreamCallback(data)

    override suspend fun getReaderOfPart(part: MDFilePartType): MDFilePartReader<out MDFilePart> {
        return when (part) {
            MDFilePartType.Language -> MDJsonFileLanguagePartReaderV1(
                json = json,
                jsonObjectFlow = jsonObjectFlow,
                deserializer = MDJsonFileLanguagePartV1.serializer()
            )

            MDFilePartType.Tag -> MDJsonFileTagPartReaderV1(
                json = json,
                jsonObjectFlow = jsonObjectFlow,
                deserializer = MDJsonFileTagPartV1.serializer()
            )

            MDFilePartType.Word -> MDJsonFileWordPartReaderV1(
                json = json,
                jsonObjectFlow = jsonObjectFlow,
                deserializer = MDJsonFileWordPartV1.serializer()
            )
        }
    }
}
