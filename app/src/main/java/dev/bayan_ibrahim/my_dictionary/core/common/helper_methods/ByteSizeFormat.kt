package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileSize

fun Long.asFileSize(
): MDFileSize {
    require(this >= 0) {
        Log.e("formatting size", "error formatting size $this (must be non negative value)")
    }
    return if (this <= 1024) { // < 2^10  byte
        MDFileSize.Byte(this, this.toDouble())
    } else if (this <= 1_048_576) { // < 2^20 kilo byte
        MDFileSize.KiloByte(this, this / 1024.0)
    } else if (this <= 1_073_741_824) { // < 2^30 mega byte
        MDFileSize.MegaByte(this, this / 1_048_576.0)
    } else if (this <= 1_099_511_627_776) { // < 2^40 giga byte
        MDFileSize.GigaByte(this, this / 1.07374182E9)
    } else { // > 2^40 tera byte
        MDFileSize.TeraByte(this, this / 1.09951163E12)
    }
}

val MDFileSize.shortFormattedValue: String
    @Composable
    @ReadOnlyComposable
    get() = when (this) {
        is MDFileSize.Byte -> R.string.byte_short
        is MDFileSize.KiloByte -> R.string.kilobyte_short
        is MDFileSize.MegaByte -> R.string.megabyte_short
        is MDFileSize.GigaByte -> R.string.gigabyte_short
        is MDFileSize.TeraByte -> R.string.terabyte_short
    }.let {
        stringResource(it)
    }

val MDFileSize.longFormattedValue: String
    @Composable
    @ReadOnlyComposable
    get() = when (this) {
        is MDFileSize.Byte -> R.string.byte_long
        is MDFileSize.KiloByte -> R.string.kilobyte_long
        is MDFileSize.MegaByte -> R.string.megabyte_long
        is MDFileSize.GigaByte -> R.string.gigabyte_long
        is MDFileSize.TeraByte -> R.string.terabyte_long
    }.let {
        stringResource(it)
    }

