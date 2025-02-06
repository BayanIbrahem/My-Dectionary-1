package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordCrossTagEntity

/**
 * return a map the key in it is the tag id, and the value is the words ids
 */
fun Collection<WordCrossTagEntity>.tagsOfWords(): Map<Long, Set<Long>> = groupBy {
    it.wordId
}.mapValues {
    it.value.map { it.tagId
    }.toSet()
}

/**
 * return a map the key in it is the word id, and the value is the tags ids
 */
fun Collection<WordCrossTagEntity>.wordsOfTags(): Map<Long, Set<Long>> = groupBy {
    it.tagId
}.mapValues {
    it.value.map {
        it.wordId
    }.toSet()
}
