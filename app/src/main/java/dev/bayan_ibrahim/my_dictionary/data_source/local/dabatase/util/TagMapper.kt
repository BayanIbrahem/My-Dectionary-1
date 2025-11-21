package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.bayan_ibrahim.my_dictionary.core.util.nullIfNegative
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.TagEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag

fun TagEntity.asModel(): ParentedTag = ParentedTag(
    id = this.tagId!!,
    label = this.label,
    color = this.color?.let { Color(it) },
    passColor = this.passToChildren,
    parentId = this.parentId,
)

fun Collection<TagEntity>.asModelSet(
): Set<ParentedTag> = map { it.asModel() }.toSet()

fun Tag.asEntity(
    id: Long? = this.id.nullIfNegative(),
    parentId: Long? = null,
): TagEntity = TagEntity(
    tagId = id,
    parentId = parentId,
    label = this.label,
    passToChildren = if (this.color == null) true else this.passColor,
    color = this.color?.toArgb()
)

fun ParentedTag.asEntity(
    id: Long? = this.id.nullIfNegative(),
): TagEntity = asEntity(id, parentId)
