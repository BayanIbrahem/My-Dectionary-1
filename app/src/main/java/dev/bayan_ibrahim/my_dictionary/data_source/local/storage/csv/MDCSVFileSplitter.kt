package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv

import androidx.core.net.toUri
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.decodeLines
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileData
import dev.bayan_ibrahim.my_dictionary.domain.model.MDFileType
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MDCSVFileSplitter<Data : Any>(
    val serializer: MDCSVSerializer<Data>,
    val openInputStream: (MDFileData) -> InputStream?,
    val openOutputStream: (File) -> OutputStream,
    val cacheDirectory: File,
) {

    inline fun splitFile(
        file: MDFileData,
        splitter: (Data) -> String,
        filter: (Data) -> Boolean = { true },
        outputFileNameBuilder: (String) -> String = { splitKey -> "${splitKey}_${file.name}" },
    ): Map<String, MDFileData> {
        val results = mutableMapOf<String, MDFileData>()
        val writers = mutableMapOf<String, BufferedWriter>()
        openInputStream(file)?.bufferedReader()?.use { reader ->
            serializer.decodeLines(
                getLine = {
                    reader.readLine()
                },
                onDecodeValidColumns = { data ->
                    if (filter(data)) {
                        val group = splitter(data)
                        val outputFileName = outputFileNameBuilder(group)
                        val outputFile = File(cacheDirectory, outputFileName)
                        outputFile.createNewFile()
                        val writer = writers.getOrPut(group) {
                            openOutputStream(outputFile).bufferedWriter()
                        }
                        results.putIfAbsent(
                            group, MDFileData(
                                uri = outputFile.toUri(),
                                name = outputFileName,
                                mimeType = MDFileType.CSV.mimeType,
                            )
                        )
                        val deserializedData = serializer.encodeRawLine(data)
                        writer.appendLine(deserializedData)
                    }
                    true
                },
            )
        }
        writers.forEach {
            it.value.close() // close each writer
        }
        return results
    }
}