package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface MDMarkerTagsRepo: MDContextTagsRepo {
    fun getMarkerTagsStream(): Flow<List<ContextTag>>
    fun getNotMarkerTagsStream(): Flow<List<ContextTag>>
    suspend fun updateMarkerTag(tag: ContextTag)
}

