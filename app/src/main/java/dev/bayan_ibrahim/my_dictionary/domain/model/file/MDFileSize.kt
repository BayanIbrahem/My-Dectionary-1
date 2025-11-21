package dev.bayan_ibrahim.my_dictionary.domain.model.file

import dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format.stringify

@JvmInline
value class MDFileSize(val bytes: Long) {

    val inBytes: Double get() = bytes.toDouble()
    val inKiloBytes: Double get() = bytes / 1024.0
    val inMegaBytes: Double get() = bytes / 1048576.0
    val inGigaBytes: Double get() = bytes / 1073741824.0
    val inTeraBytes: Double get() = bytes / 1099511627776.0

    companion object {
        val zero = MDFileSize(0L)
    }

    override fun toString(): String = stringify

    operator fun plus(other: MDFileSize): MDFileSize = MDFileSize(this.bytes + other.bytes)
    operator fun minus(other: MDFileSize): MDFileSize = MDFileSize(this.bytes - other.bytes)
    operator fun times(multiplier: Long): MDFileSize = MDFileSize(this.bytes * multiplier)
    operator fun times(multiplier: Int): MDFileSize = MDFileSize(this.bytes * multiplier)
    operator fun div(divisor: Long): MDFileSize = MDFileSize(this.bytes / divisor)
    operator fun div(divisor: Int): MDFileSize = MDFileSize(this.bytes / divisor)
    operator fun compareTo(other: MDFileSize): Int = this.bytes.compareTo(other.bytes)
}

val Int.bytes: MDFileSize get() = MDFileSize(this.toLong())
val Long.bytes: MDFileSize get() = MDFileSize(this)

val Int.kilobytes: MDFileSize get() = MDFileSize(this * 1024L)
val Long.kilobytes: MDFileSize get() = MDFileSize(this * 1024L)

val Int.megabytes: MDFileSize get() = MDFileSize(this * 1048576L)
val Long.megabytes: MDFileSize get() = MDFileSize(this * 1048576L)

val Int.gigabytes: MDFileSize get() = MDFileSize(this * 1073741824L)
val Long.gigabytes: MDFileSize get() = MDFileSize(this * 1073741824L)

val Int.terabytes: MDFileSize get() = MDFileSize(this * 1099511627776L)
val Long.terabytes: MDFileSize get() = MDFileSize(this * 1099511627776L)