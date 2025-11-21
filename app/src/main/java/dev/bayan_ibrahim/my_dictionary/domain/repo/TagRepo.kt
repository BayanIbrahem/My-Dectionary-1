package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ParentedTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepo {
    suspend fun getTag(id: Long): ParentedTag?
    suspend fun getTag(label: String): ParentedTag?

    suspend fun getTags(ids: Set<Long>): List<ParentedTag>
    suspend fun getTags(labels: Set<String>): List<ParentedTag>
    fun getTagsStream(
        includeMarkerTags: Boolean = true,
        includeNonMarkerTags: Boolean = true,
    ): Flow<List<ParentedTag>>
    suspend fun addOrUpdateTag(tag: Tag, parentId: Long? = null): ParentedTag
    suspend fun addOrUpdateTag(tag: ParentedTag): ParentedTag {
        return addOrUpdateTag(tag, tag.parentId)
    }
    suspend fun removeTag(tag: Tag, parentId: Long? = null)
    suspend fun removeTag(tag: ParentedTag) {
        return removeTag(tag, tag.parentId)
    }
    fun getWordsCount(): Flow<Map<Long, Int>>
}