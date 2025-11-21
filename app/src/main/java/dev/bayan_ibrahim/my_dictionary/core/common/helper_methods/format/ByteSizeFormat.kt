package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods.format

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import dev.bayan_ibrahim.my_dictionary.R
import dev.bayan_ibrahim.my_dictionary.domain.model.file.MDFileSize
import java.text.DecimalFormat

fun Long.asFileSize(
): MDFileSize {
    require(this >= 0) {
        Log.e("formatting size", "error formatting size $this (must be non negative value)")
    }
    return MDFileSize(this)
}

private val decimalFormat = DecimalFormat("#.##")

/**
 * return value with short unit like:
 * 1 B
 * 1.1 KB
 * 1.1 MB
 * 1.1 GB
 * 1.1 TB
 */
val MDFileSize.stringify: String
    get() = when {
        bytes < 1024 -> "${decimalFormat.format(inBytes)} byte"
        bytes < 1048576 -> "${decimalFormat.format(inKiloBytes)} kilobyte"
        bytes < 1073741824 -> "${decimalFormat.format(inMegaBytes)} megabyte"
        bytes < 1099511627776 -> "${decimalFormat.format(inGigaBytes)} gigabyte"
        else -> "${decimalFormat.format(inTeraBytes)} terabyte"
    }

val MDFileSize.shortFormattedValue: String
    @Composable
    @ReadOnlyComposable
    get() = when {
        bytes < 1024 -> stringResource(R.string.byte_short, decimalFormat.format(inBytes))
        bytes < 1048576 -> stringResource(R.string.kilobyte_short, decimalFormat.format(inKiloBytes))
        bytes < 1073741824 -> stringResource(R.string.megabyte_short, decimalFormat.format(inMegaBytes))
        bytes < 1099511627776 -> stringResource(R.string.gigabyte_short, decimalFormat.format(inGigaBytes))
        else -> stringResource(R.string.terabyte_short, decimalFormat.format(inTeraBytes))
    }

/**
 * return value with short unit like:
 * 1 byte
 * 1.1 kilobyte
 * 1.1 megabyte
 * 1.1 gigabyte
 * 1.1 terabyte
 */
val MDFileSize.longFormattedValue: String
    @Composable
    @ReadOnlyComposable
    get() =
        when {
            bytes < 1024 -> stringResource(R.string.byte_long, decimalFormat.format(inBytes))
            bytes < 1048576 -> stringResource(R.string.kilobyte_long, decimalFormat.format(inKiloBytes))
            bytes < 1073741824 -> stringResource(R.string.megabyte_long, decimalFormat.format(inMegaBytes))
            bytes < 1099511627776 -> stringResource(R.string.gigabyte_long, decimalFormat.format(inGigaBytes))
            else -> stringResource(R.string.terabyte_long, decimalFormat.format(inTeraBytes))
        }

