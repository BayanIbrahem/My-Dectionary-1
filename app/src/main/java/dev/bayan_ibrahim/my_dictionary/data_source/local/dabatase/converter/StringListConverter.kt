package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.converter

import kotlinx.serialization.builtins.serializer

object StringListConverter : ListConverter<String>(serializer = String.serializer())