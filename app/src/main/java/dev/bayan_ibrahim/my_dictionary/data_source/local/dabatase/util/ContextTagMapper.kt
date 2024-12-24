package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.ContextTagEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag

fun ContextTagEntity.asModel(wordsCount: Int = 0): ContextTag = ContextTag(
    id = this.tagId!!,
    value = this.path,
    wordsCount = wordsCount
)

fun Collection<ContextTagEntity>.asModelSet(
    wordsCount: Map<Long, Int> = emptyMap(),
): Set<ContextTag> = map { it.asModel(wordsCount[it.tagId] ?: 0) }.toSet()

fun ContextTag.asEntity(
    id: Long? = this.id.nullIfInvalid(),
): ContextTagEntity = ContextTagEntity(
    tagId = id,
    path = this.value,
)
