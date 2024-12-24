package dev.bayan_ibrahim.my_dictionary.domain.model.file

sealed class MDFileSize(
    open val bytes: Long,
    open val valueOfUnit: Double,
) {
    data class Byte(override val bytes: Long, override val valueOfUnit: Double) : MDFileSize(bytes, valueOfUnit)
    data class KiloByte(override val bytes: Long, override val valueOfUnit: Double) : MDFileSize(bytes, valueOfUnit)
    data class MegaByte(override val bytes: Long, override val valueOfUnit: Double) : MDFileSize(bytes, valueOfUnit)
    data class GigaByte(override val bytes: Long, override val valueOfUnit: Double) : MDFileSize(bytes, valueOfUnit)
    data class TeraByte(override val bytes: Long, override val valueOfUnit: Double) : MDFileSize(bytes, valueOfUnit)
    companion object {
        val zero: MDFileSize = Byte(
            bytes = 0L,
            valueOfUnit = 0.0
        )
    }
}

