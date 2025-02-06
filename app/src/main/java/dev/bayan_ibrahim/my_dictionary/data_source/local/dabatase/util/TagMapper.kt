package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfInvalid
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag

fun TagEntity.asModel(wordsCount: Int = 0): Tag = Tag(
    id = this.tagId!!,
    value = this.path,
    wordsCount = wordsCount,
    color = this.color?.let { Color(it) },
    passColorToChildren = this.passToChildren,
    currentColorIsPassed = this.color == null,
)

fun Collection<TagEntity>.asModelSet(
    wordsCount: Map<Long, Int> = emptyMap(),
): Set<Tag> = map { it.asModel(wordsCount[it.tagId] ?: 0) }.toSet()

fun Tag.asEntity(
    id: Long? = this.id.nullIfInvalid(),
): TagEntity = TagEntity(
    tagId = id,
    path = this.value,
    passToChildren = if (this.color == null) true else this.passColorToChildren,
    color = if (this.currentColorIsPassed) null else this.color?.toArgb()
)
