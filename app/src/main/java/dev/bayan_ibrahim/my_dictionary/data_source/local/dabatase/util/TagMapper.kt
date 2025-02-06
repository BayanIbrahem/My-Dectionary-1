package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.relation.WordClassWithRelation
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.WordWordClassRelationEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.WordWordClass
import dev.bayan_ibrahim.my_dictionary.domain.model.WordWordClassRelation
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage

fun WordClassWithRelation.asTagModel(): WordWordClass = WordWordClass(
    id = this.tag.id!!,
    name = this.tag.name,
    language = this.tag.language.code.getLanguage(),
    relations = this.relations.map { it.asModelRelation() }
)

private fun WordWordClassRelationEntity.asModelRelation(wordsCount: Int = 0) = WordWordClassRelation(label = label, id = id!!, wordsCount = wordsCount)

fun WordClassWithRelation.asTagModelWithCount(relationsCount: Map<Long, Int>): WordWordClass {
    var tagWordsCount = 0
    return WordWordClass(
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

fun WordWordClass.asTagEntity(
    id: Long? = this.id.nullIfInvalid(),
) = WordWordClassEntity(
    id = id,
    name = name,
    language = language.code,
)

fun WordWordClassRelation.asRelationEntity(
    tagId: Long,
    id: Long? = this.id.nullIfInvalid(),
) = WordWordClassRelationEntity(
    id = id,
    label = label,
    tagId = tagId,
)