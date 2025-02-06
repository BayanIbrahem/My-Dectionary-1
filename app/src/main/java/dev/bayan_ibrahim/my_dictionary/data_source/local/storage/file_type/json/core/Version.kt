package dev.bayan_ibrahim.my_dictionary.data_source.local.storage.file_type.json.core

import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFilePartType

/**
 * this is labels of file part data data in json file for each version,
 * there must be a value for each valid version of the the app
 * @throws IllegalArgumentException if the version is not known
 */
fun getJsonPartKeyOfVersion(part: MDFilePartType, version: Int): String = when (part) {
    MDFilePartType.Language -> when (version) {
        1 -> "languages"
        else -> throw IllegalArgumentException("invalid version $version for language key") // TODO, custom exception
    }

    MDFilePartType.Tag -> when (version) {
        1 -> "tags"
        else -> throw IllegalArgumentException("invalid version $version for tag key") // TODO, custom exception
    }

    MDFilePartType.Word -> when (version) {
        1 -> "words"
        else -> throw IllegalArgumentException("invalid version $version for word key") // TODO, custom exception
    }
}

const val JSON_VERSION_KEY = "version"
