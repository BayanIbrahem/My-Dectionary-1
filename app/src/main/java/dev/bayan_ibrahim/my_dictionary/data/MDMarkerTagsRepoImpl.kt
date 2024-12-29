package dev.bayan_ibrahim.my_dictionary.data

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.context_tag.ContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.dao.word_cross_context_tag.WordsCrossContextTagDao
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util.asEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDContextTagsRepo
import dev.bayan_ibrahim.my_dictionary.domain.repo.MDMarkerTagsRepo
import kotlinx.coroutines.flow.Flow

class MDMarkerTagsRepoImpl(
    private val contextTagDao: ContextTagDao,
    private val wordsCrossContextTagDao: WordsCrossContextTagDao,
) : MDMarkerTagsRepo, MDContextTagsRepo by MDContextTagsRepoImpl(contextTagDao, wordsCrossContextTagDao) {
    override fun getMarkerTagsStream(): Flow<List<ContextTag>> = getContextTagsStream(includeMarkerTags = true, includeNonMarkerTags = false)
    override fun getNotMarkerTagsStream(): Flow<List<ContextTag>> = getContextTagsStream(includeMarkerTags = false, includeNonMarkerTags = true)

    override suspend fun updateMarkerTag(tag: ContextTag) {
        val entity = tag.asEntity()
        if (entity.tagId != null) {
            contextTagDao.updateContextTag(entity)
        } else {
            contextTagDao.insertContextTag(entity)
        }
    }
}