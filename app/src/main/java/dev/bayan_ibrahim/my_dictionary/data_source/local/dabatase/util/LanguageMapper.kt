package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.sub_table.LanguageWordSpaceEntity
import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.language.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.language.LanguageWordSpace
import dev.bayan_ibrahim.my_dictionary.domain.model.language.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language.getLanguage


fun LanguageEntity.asLanguageModel(): Language = this.code.code.getLanguage()

fun LanguageWordSpaceEntity.asWordSpaceModel(): LanguageWordSpace = LanguageWordSpace(
    code = languageCode,
    wordsCount = wordsCount,
)

