package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordClassWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage

fun WordClassWithRelation.asTagModel(): WordClass = WordClass(
    id = this.tag.id!!,
    name = this.tag.name,
    language = this.tag.language.code.getLanguage(),
    relations = this.relations.map { it.asModelRelation() }
)

private fun WordClassRelationEntity.asModelRelation(wordsCount: Int = 0) = WordClassRelation(label = label, id = id!!, wordsCount = wordsCount)

fun WordClassWithRelation.asTagModelWithCount(relationsCount: Map<Long, Int>): WordClass {
    var tagWordsCount = 0
    return WordClass(
        id = this.tag.id!!,
        name = this.tag.name,
        language = this.tag.language.code.getLanguage(),
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

fun WordClass.asTagEntity(
    id: Long? = this.id.nullIfInvalid(),
) = WordClassEntity(
    id = id,
    name = name,
    language = language.code,
)

fun WordClassRelation.asRelationEntity(
    tagId: Long,
    id: Long? = this.id.nullIfInvalid(),
) = WordClassRelationEntity(
    id = id,
    label = label,
    tagId = tagId,
)