package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv

import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWord
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWordRelation
import dev.bayan_ibrahim.my_dictionary.core.common.helper_classes.MDRawWordTypeTag
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVListSerializer
import dev.bayan_ibrahim.my_dictionary.data_source.local.storage.csv.serializer.MDCSVSerializer

data class MDRawWordCSVSerializer(
    val separator: String = DEFAULT_SEPARATOR,
    val listItemsSeparator: String = DEFAULT_LIST_ITEMS_SEPARATOR,
    val typeTagSerializer: MDRawWordTagCSVSerializer = MDRawWordTagCSVSerializer(),
) : MDCSVSerializer<MDRawWord>(
    columnsCount = 3, // required columns count
    header = listOf(
        "meaning",
        "translation",
        "language",
        "additional_translations",
        "examples",
        "tags",
        "transcription",
        typeTagSerializer.encodedHeader,
    )
) {
    companion object Companion {
        const val DEFAULT_LIST_ITEMS_SEPARATOR = "; "
        const val DEFAULT_SEPARATOR = ", "
    }

    val listColumnDescriptor = MDCSVListSerializer.stringListSerializer(
        separator = listItemsSeparator
    ).descriptor

    private fun List<String>.getAsList(index: Int): List<String> = getOrNull(index)?.let(listColumnDescriptor::splitLine) ?: emptyList()

    init {
        require(separator.trim() != listItemsSeparator.trim()) {
            "Duplicated nested separators not allowed column separator: $separator with list item separator: $listItemsSeparator"
        }

        require(separator.trim() != typeTagSerializer.descriptor.separator.trim()) {
            "Duplicated nested separators not allowed column separator: $separator with tag relations separator ${typeTagSerializer.descriptor.separator}"
        }
    }

    override fun decodeLine(line: List<String>): MDRawWord {
        return MDRawWord(
            meaning = line[0],
            translation = line[1],
            language = line[2],
            additionalTranslations = line.getAsList(3),
            examples = line.getAsList(4),
            tags = line.getAsList(5),
            transcription = line.getOrNull(6) ?: "",
            wordTypeTag = line.getOrNull(7)?.let(typeTagSerializer::decodeLine)
        )
    }

    override fun encodeLine(value: MDRawWord): Collection<String> = listOfNotNull(
        value.meaning,
        value.translation,
        value.language,
        listColumnDescriptor.joinLine(value.additionalTranslations),
        listColumnDescriptor.joinLine(value.examples),
        listColumnDescriptor.joinLine(value.tags),
        value.transcription,
        // this is a trailing null value so it will not effect if it is existed or not
        value.wordTypeTag?.let {
            typeTagSerializer.encodeRawLine(it)
        }
    )

}

data class MDRawWordTagCSVSerializer(
    /**
     * split relations from each other, and this would be the same separator for the tag name
     * eg the separator is `;` then the encoded string must be:
     * `tag_name; encoded_relation_1; encoded_relation_2`
     */
    val relationsSeparator: String = DEFAULT_RELATIONS_SEPARATOR,
    val relationSerializer: MDRawWordTagRelationCSVSerializer = MDRawWordTagRelationCSVSerializer(),
) : MDCSVSerializer<MDRawWordTypeTag>(
    columnsCount = 2,
    separator = relationsSeparator,
    header = listOf(
        "word_type_tag",
        relationSerializer.encodedHeader
    )
) {
    companion object Companion {
        const val DEFAULT_RELATIONS_SEPARATOR = "; "
    }

    init {
        require(descriptor.separator.trim() != relationSerializer.descriptor.separator.trim()) {
            "Duplicated nested separators not allowed ${descriptor.separator} with ${relationSerializer.descriptor.separator}"
        }
    }

    override fun decodeLine(line: List<String>): MDRawWordTypeTag {
        val relationsIterator = line.subList(1, line.size).iterator()
        return MDRawWordTypeTag(
            name = line[0].trim(),
            relations = relationSerializer.decodeLines(relationsIterator),
        )
    }

    override fun encodeLine(value: MDRawWordTypeTag): Collection<String> = listOf(
        value.name.trim(),
        descriptor.joinLine(
            relationSerializer.encodeLines(value.relations.iterator()),
        )
    )

}

data class MDRawWordTagRelationCSVSerializer(
    /**
     * split single relation fields
     */
    val relationSeparator: String = DEFAULT_RELATION_SEPARATOR,
) : MDCSVSerializer<MDRawWordRelation>(
    columnsCount = 2,
    separator = relationSeparator,
    header = listOf(
        "label",
        "related_word"
    )
) {

    companion object Companion {
        const val DEFAULT_RELATION_SEPARATOR = ": "
    }

    override fun decodeLine(line: List<String>): MDRawWordRelation {
        return MDRawWordRelation(
            label = line[0].trim(),
            relatedWord = line[1].trim()
        )
    }

    override fun encodeLine(value: MDRawWordRelation): Collection<String> = listOf(
        value.label.trim(),
        value.relatedWord.trim()
    )
}
