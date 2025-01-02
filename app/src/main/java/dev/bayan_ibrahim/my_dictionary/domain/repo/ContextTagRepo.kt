package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface ContextTagRepo {
    suspend fun getContextTag(id: Long): ContextTag?
    fun getContextTagsStream(
        includeMarkerTags: Boolean = true,
        includeNonMarkerTags: Boolean = true,
    ): Flow<List<ContextTag>>
    suspend fun addOrUpdateContextTag(tag: ContextTag): ContextTag
    suspend fun removeContextTag(tag: ContextTag)
}