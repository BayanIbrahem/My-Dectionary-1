package dev.bayan_ibrahim.my_dictionary.domain.repo

import dev.bayan_ibrahim.my_dictionary.domain.model.word.Word
import dev.bayan_ibrahim.my_dictionary.domain.model.WordTypeTag
import dev.bayan_ibrahim.my_dictionary.domain.model.tag.ContextTag
import kotlinx.coroutines.flow.Flow

interface MDWordDetailsViewModeRepo {
    suspend fun getWord(wordId: Long): Word?
}