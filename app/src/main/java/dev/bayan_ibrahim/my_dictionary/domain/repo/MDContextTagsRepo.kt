package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface MDContextTagsRepo {
    fun getContextTagsStream(): Flow<List<ContextTag>>
    suspend fun addOrUpdateContextTag(tag: ContextTag): ContextTag
    suspend fun removeContextTag(tag: ContextTag)
}