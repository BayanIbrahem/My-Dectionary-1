package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.TypeTagWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordTypeTagRelationEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTagRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.language

fun TypeTagWithRelation.asTagModel(): WordTypeTag = WordTypeTag(
    id = this.tag.id!!,
    name = this.tag.name,
    language = this.tag.language.code.language,
    relations = this.relations.map { it.asModelRelation() }
)

private fun WordTypeTagRelationEntity.asModelRelation(wordsCount: Int = 0) = WordTypeTagRelation(label = label, id = id!!, wordsCount = wordsCount)

fun TypeTagWithRelation.asTagModelWithCount(relationsCount: Map<Long, Int>): WordTypeTag {
    var tagWordsCount = 0
    return WordTypeTag(
        id = this.tag.id!!,
        name = this.tag.name,
        language = this.tag.language.code.language,
        relations = this.relations.map {
            it.asModelRelation(
                wordsCount = relationsCount[it.id]?.also { relationWordCount ->
                    tagWordsCount += relationWordCount
                } ?: 0
            )
        },
        wordsCount = tagWordsCount
    )
}

fun WordTypeTag.asTagEntity(
    id: Long? = this.id.nullIfInvalid(),
) = WordTypeTagEntity(
    id = id,
    name = name,
    language = language.code.code,
)

fun WordTypeTagRelation.asRelationEntity(
    tagId: Long,
    id: Long? = this.id.nullIfInvalid(),
) = WordTypeTagRelationEntity(
    id = id,
    label = label,
    tagId = tagId,
)