package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.util

import dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.entity.table.LanguageEntity
import dev.bayan_ibrahim.my_dictionary.domain.model.Language
import dev.bayan_ibrahim.my_dictionary.domain.model.allLanguages
import dev.bayan_ibrahim.my_dictionary.domain.model.code
import dev.bayan_ibrahim.my_dictionary.domain.model.language


fun LanguageEntity.asLanguageModel(): Language = this.code.code.language