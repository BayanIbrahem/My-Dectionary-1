package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.TypeTagWithRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages

fun TypeTagWithRelation.asTagModel(): WordTypeTag = WordTypeTag(
    id = this.tag.id!!,
    name = this.tag.name,
    language = allLanguages[this.tag.language]!!,
    relations = this.relations.map { WordTypeTagRelation(label = it.label, id = it.id!!) }
)

fun TypeTagWithRelation.asTagModelWithCount(relationsCount: Map<Long, Int>): WordTypeTag {
    var tagWordsCount = 0
    return WordTypeTag(
        id = this.tag.id!!,
        name = this.tag.name,
        language = allLanguages[this.tag.language]!!,
        relations = this.relations.map {
            WordTypeTagRelation(
                label = it.label,
                id = it.id!!,
                wordsCount = relationsCount[it.id]?.also { relationWordCount ->
                    tagWordsCount += relationWordCount
                } ?: 0
            )
        },
        wordsCount = tagWordsCount
    )
}
