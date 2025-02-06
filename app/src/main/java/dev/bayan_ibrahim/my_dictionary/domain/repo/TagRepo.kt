package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepo {
    suspend fun getTag(id: Long): Tag?
    fun getTagsStream(
        includeMarkerTags: Boolean = true,
        includeNonMarkerTags: Boolean = true,
    ): Flow<List<Tag>>
    suspend fun addOrUpdateTag(tag: Tag): Tag
    suspend fun removeTag(tag: Tag)
}